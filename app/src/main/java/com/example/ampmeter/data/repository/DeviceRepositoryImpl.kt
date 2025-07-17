package com.example.ampmeter.data.repository

import android.bluetooth.BluetoothDevice
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import com.example.ampmeter.data.local.database.dao.DeviceReadingDao
import com.example.ampmeter.data.local.mapper.DeviceReadingMapper
import com.example.ampmeter.data.remote.api.ChirpStackApi
import com.example.ampmeter.data.remote.service.BleClientService
import com.example.ampmeter.data.remote.service.MqttClientService
import com.example.ampmeter.domain.model.DeviceReading
import com.example.ampmeter.domain.model.Resource
import com.example.ampmeter.domain.repository.DeviceRepository
import com.example.ampmeter.util.PayloadDecoder
import com.example.ampmeter.util.network.NetworkManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of DeviceRepository.
 */
@Singleton
class DeviceRepositoryImpl @Inject constructor(
    private val context: Context,
    private val chirpStackApi: ChirpStackApi,
    private val deviceReadingDao: DeviceReadingDao,
    private val networkManager: NetworkManager
) : DeviceRepository {
    
    private val coroutineScope = CoroutineScope(Dispatchers.IO)
    
    // MQTT service components
    private var mqttClientService: MqttClientService? = null
    private var mqttBound = false
    private val mqttReadingsFlow = MutableStateFlow<DeviceReading?>(null)
    
    // BLE service components
    private var bleClientService: BleClientService? = null
    private var bleBound = false
    private val bleConnectionStateFlow = MutableStateFlow<Int>(0) // 0: Disconnected, 1: Connecting, 2: Connected
    private val bleReadingsFlow = MutableStateFlow<DeviceReading?>(null)
    
    // Service connections
    private val mqttServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as MqttClientService.MqttBinder
            mqttClientService = binder.getService()
            mqttBound = true
            
            // Start collecting device readings from MQTT
            coroutineScope.launch {
                mqttClientService?.deviceReadings
                    ?.catch { e -> Timber.e(e, "Error collecting MQTT readings") }
                    ?.collect { deviceReading ->
                        // Save to local database
                        insertDeviceReading(deviceReading)
                        mqttReadingsFlow.value = deviceReading
                    }
            }
        }
        
        override fun onServiceDisconnected(name: ComponentName?) {
            mqttClientService = null
            mqttBound = false
        }
    }
    
    private val bleServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as BleClientService.BleBinder
            bleClientService = binder.getService()
            bleBound = true
            
            // Start collecting device readings from BLE
            coroutineScope.launch {
                bleClientService?.deviceReadings
                    ?.catch { e -> Timber.e(e, "Error collecting BLE readings") }
                    ?.collect { deviceReading ->
                        // Save to local database
                        insertDeviceReading(deviceReading)
                        bleReadingsFlow.value = deviceReading
                    }
            }
            
            // Start collecting connection state
            coroutineScope.launch {
                bleClientService?.connectionState
                    ?.catch { e -> Timber.e(e, "Error collecting BLE connection state") }
                    ?.collect { state ->
                        bleConnectionStateFlow.value = when (state) {
                            BleClientService.ConnectionState.Disconnected -> 0
                            BleClientService.ConnectionState.Connecting -> 1
                            BleClientService.ConnectionState.Connected -> 2
                        }
                    }
            }
        }
        
        override fun onServiceDisconnected(name: ComponentName?) {
            bleClientService = null
            bleBound = false
        }
    }
    
    init {
        // Bind to the services
        bindServices()
    }
    
    private fun bindServices() {
        // Bind to MQTT service
        Intent(context, MqttClientService::class.java).also { intent ->
            context.bindService(intent, mqttServiceConnection, Context.BIND_AUTO_CREATE)
        }
        
        // Bind to BLE service
        Intent(context, BleClientService::class.java).also { intent ->
            context.bindService(intent, bleServiceConnection, Context.BIND_AUTO_CREATE)
        }
    }
    
    override suspend fun getLatestReading(deviceId: String): Resource<DeviceReading> {
        return try {
            if (networkManager.isConnected()) {
                // Attempt to fetch from network
                val response = chirpStackApi.getDeviceFrames(deviceId, 1)
                if (response.isSuccessful && response.body() != null) {
                    val frames = response.body()!!.result
                    if (frames.isNotEmpty()) {
                        val frame = frames[0]
                        try {
                            val reading = PayloadDecoder.decodePayload(frame.data, deviceId).copy(
                                frameCount = frame.fCnt,
                                gatewayId = frame.rxInfo?.firstOrNull()?.gatewayId
                            )
                            
                            // Save to local database
                            val entity = DeviceReadingMapper.toEntity(reading)
                            deviceReadingDao.insert(entity)
                            
                            return Resource.Success(reading)
                        } catch (e: Exception) {
                            Timber.e(e, "Error decoding payload: %s", frame.data)
                            getFallbackReading(deviceId)
                        }
                    } else {
                        Timber.d("No frames found for device: %s", deviceId)
                        getFallbackReading(deviceId)
                    }
                } else {
                    Timber.e("API call failed: %s", response.errorBody()?.string())
                    getFallbackReading(deviceId)
                }
            } else {
                Timber.d("Network not available, using cached data")
                getFallbackReading(deviceId)
            }
        } catch (e: IOException) {
            Timber.e(e, "Network error")
            getFallbackReading(deviceId)
        } catch (e: HttpException) {
            Timber.e(e, "HTTP error: %s", e.code())
            getFallbackReading(deviceId)
        } catch (e: Exception) {
            Timber.e(e, "Unknown error")
            Resource.Error("An unexpected error occurred: ${e.message}")
        }
    }
    
    override suspend fun getDeviceReadings(
        deviceId: String,
        limit: Int,
        offset: Int
    ): Resource<List<DeviceReading>> {
        return try {
            val localReadings = deviceReadingDao.getDeviceReadings(deviceId, limit, offset)
            Resource.Success(DeviceReadingMapper.fromEntityList(localReadings))
        } catch (e: Exception) {
            Timber.e(e, "Error fetching device readings")
            Resource.Error("Failed to get device readings: ${e.message}")
        }
    }
    
    override fun getDeviceReadingsFlow(deviceId: String): Flow<List<DeviceReading>> {
        return deviceReadingDao.getDeviceReadingsFlow(deviceId).map { entities ->
            DeviceReadingMapper.fromEntityList(entities)
        }
    }
    
    override suspend fun syncWithCloud(): Resource<Unit> {
        // This will be implemented when Supabase integration is added
        return Resource.Success(Unit)
    }
    
    override suspend fun insertDeviceReading(deviceReading: DeviceReading): Resource<DeviceReading> {
        return try {
            val entity = DeviceReadingMapper.toEntity(deviceReading)
            val id = deviceReadingDao.insert(entity)
            Resource.Success(deviceReading)
        } catch (e: Exception) {
            Timber.e(e, "Error inserting device reading")
            Resource.Error("Failed to insert device reading: ${e.message}")
        }
    }
    
    // MQTT Implementation
    
    override suspend fun connectToMqttBroker(
        brokerUrl: String,
        username: String?,
        password: String?
    ): Resource<Boolean> {
        return withContext(Dispatchers.IO) {
            try {
                if (!mqttBound || mqttClientService == null) {
                    bindServices()
                    // Wait for service to bind
                    var attempts = 0
                    while (!mqttBound && attempts < 5) {
                        delay(500)
                        attempts++
                    }
                    
                    if (!mqttBound) {
                        return@withContext Resource.Error("Failed to bind to MQTT service")
                    }
                }
                
                mqttClientService?.connect(brokerUrl, username = username, password = password)
                Resource.Success(true)
            } catch (e: Exception) {
                Timber.e(e, "Error connecting to MQTT broker")
                Resource.Error("Failed to connect to MQTT broker: ${e.message}")
            }
        }
    }
    
    override suspend fun subscribeMqttTopics(topics: List<String>): Resource<Boolean> {
        return withContext(Dispatchers.IO) {
            try {
                if (!mqttBound || mqttClientService == null) {
                    return@withContext Resource.Error("MQTT service not bound")
                }
                
                mqttClientService?.subscribeToTopics(topics)
                Resource.Success(true)
            } catch (e: Exception) {
                Timber.e(e, "Error subscribing to MQTT topics")
                Resource.Error("Failed to subscribe to MQTT topics: ${e.message}")
            }
        }
    }
    
    override suspend fun disconnectMqtt(): Resource<Boolean> {
        return withContext(Dispatchers.IO) {
            try {
                if (!mqttBound || mqttClientService == null) {
                    return@withContext Resource.Success(true) // Already disconnected
                }
                
                mqttClientService?.disconnect()
                Resource.Success(true)
            } catch (e: Exception) {
                Timber.e(e, "Error disconnecting from MQTT broker")
                Resource.Error("Failed to disconnect from MQTT broker: ${e.message}")
            }
        }
    }
    
    override fun getMqttDeviceReadingsFlow(): Flow<DeviceReading> {
        return mqttReadingsFlow
            .map { it ?: throw IllegalStateException("No MQTT reading available") }
            .catch { e -> Timber.e(e, "Error in MQTT readings flow") }
            .shareIn(coroutineScope, SharingStarted.Lazily, replay = 1)
    }
    
    // BLE Implementation
    
    override suspend fun startBleScan(): Resource<Boolean> {
        return withContext(Dispatchers.IO) {
            try {
                if (!bleBound || bleClientService == null) {
                    bindServices()
                    // Wait for service to bind
                    var attempts = 0
                    while (!bleBound && attempts < 5) {
                        delay(500)
                        attempts++
                    }
                    
                    if (!bleBound) {
                        return@withContext Resource.Error("Failed to bind to BLE service")
                    }
                }
                
                bleClientService?.startScan()
                Resource.Success(true)
            } catch (e: Exception) {
                Timber.e(e, "Error starting BLE scan")
                Resource.Error("Failed to start BLE scan: ${e.message}")
            }
        }
    }
    
    override suspend fun stopBleScan(): Resource<Boolean> {
        return withContext(Dispatchers.IO) {
            try {
                if (!bleBound || bleClientService == null) {
                    return@withContext Resource.Success(true) // Already stopped
                }
                
                bleClientService?.stopScan()
                Resource.Success(true)
            } catch (e: Exception) {
                Timber.e(e, "Error stopping BLE scan")
                Resource.Error("Failed to stop BLE scan: ${e.message}")
            }
        }
    }
    
    override fun getBleDevicesFlow(): Flow<Map<String, BluetoothDevice>> {
        return bleClientService?.scanResultsFlow
            ?: MutableStateFlow<Map<String, BluetoothDevice>>(emptyMap())
    }
    
    override suspend fun connectToBleDevice(device: BluetoothDevice): Resource<Boolean> {
        return withContext(Dispatchers.IO) {
            try {
                if (!bleBound || bleClientService == null) {
                    return@withContext Resource.Error("BLE service not bound")
                }
                
                bleClientService?.connectToDevice(device)
                Resource.Success(true)
            } catch (e: Exception) {
                Timber.e(e, "Error connecting to BLE device")
                Resource.Error("Failed to connect to BLE device: ${e.message}")
            }
        }
    }
    
    override suspend fun disconnectBle(): Resource<Boolean> {
        return withContext(Dispatchers.IO) {
            try {
                if (!bleBound || bleClientService == null) {
                    return@withContext Resource.Success(true) // Already disconnected
                }
                
                bleClientService?.disconnect()
                Resource.Success(true)
            } catch (e: Exception) {
                Timber.e(e, "Error disconnecting BLE")
                Resource.Error("Failed to disconnect BLE: ${e.message}")
            }
        }
    }
    
    override fun getBleConnectionStateFlow(): Flow<Int> {
        return bleConnectionStateFlow
    }
    
    override fun getBleDeviceReadingsFlow(): Flow<DeviceReading> {
        return bleReadingsFlow
            .map { it ?: throw IllegalStateException("No BLE reading available") }
            .catch { e -> Timber.e(e, "Error in BLE readings flow") }
            .shareIn(coroutineScope, SharingStarted.Lazily, replay = 1)
    }
    
    override suspend fun sendBleCommand(command: ByteArray): Resource<Boolean> {
        return withContext(Dispatchers.IO) {
            try {
                if (!bleBound || bleClientService == null) {
                    return@withContext Resource.Error("BLE service not bound")
                }
                
                val result = bleClientService?.sendCommand(command) ?: false
                if (result) {
                    Resource.Success(true)
                } else {
                    Resource.Error("Failed to send BLE command")
                }
            } catch (e: Exception) {
                Timber.e(e, "Error sending BLE command")
                Resource.Error("Failed to send BLE command: ${e.message}")
            }
        }
    }
    
    /**
     * Gets fallback reading from local database.
     */
    private suspend fun getFallbackReading(deviceId: String): Resource<DeviceReading> {
        val localReading = deviceReadingDao.getLatestReading(deviceId)
        return if (localReading != null) {
            Resource.Success(DeviceReadingMapper.fromEntity(localReading))
        } else {
            Resource.Error("No data available for device $deviceId")
        }
    }
    
    /**
     * Utility method to delay execution.
     */
    private suspend fun delay(timeMillis: Long) {
        kotlinx.coroutines.delay(timeMillis)
    }
} 