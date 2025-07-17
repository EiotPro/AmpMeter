package com.example.ampmeter.presentation.ui.settings

import android.bluetooth.BluetoothDevice
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ampmeter.data.remote.api.ChirpStackApi
import com.example.ampmeter.domain.model.Resource
import com.example.ampmeter.domain.repository.DeviceRepository
import com.example.ampmeter.domain.usecase.settings.GetSettingsUseCase
import com.example.ampmeter.domain.usecase.settings.UpdateSettingsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okhttp3.MediaType
import retrofit2.HttpException
import timber.log.Timber
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val getSettingsUseCase: GetSettingsUseCase,
    private val updateSettingsUseCase: UpdateSettingsUseCase,
    private val deviceRepository: DeviceRepository,
    private val chirpStackApi: ChirpStackApi
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()
    
    // BLE devices flow
    val bleDevices = deviceRepository.getBleDevicesFlow()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyMap())
    
    init {
        loadSettings()
        observeConnectionStates()
    }
    
    private fun observeConnectionStates() {
        // Observe BLE connection state
        viewModelScope.launch {
            deviceRepository.getBleConnectionStateFlow().collectLatest { state ->
                _uiState.update { it.copy(bleConnectionState = state) }
            }
        }
    }
    
    private fun loadSettings() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            try {
                // Load device settings
                val deviceId = getSettingsUseCase.getDeviceId()
                val deviceName = getSettingsUseCase.getDeviceName()
                
                // Load ChirpStack settings
                val serverUrl = getSettingsUseCase.getChirpStackServerUrl()
                val apiKey = getSettingsUseCase.getChirpStackApiKey()
                
                // Load MQTT settings
                val mqttBrokerUrl = getSettingsUseCase.getMqttBrokerUrl()
                val mqttPort = getSettingsUseCase.getMqttPort()
                val mqttUsername = getSettingsUseCase.getMqttUsername()
                val mqttPassword = getSettingsUseCase.getMqttPassword()
                val mqttTopic = getSettingsUseCase.getMqttTopic()
                val mqttEnabled = getSettingsUseCase.getMqttEnabled()
                
                // Load WiFi settings
                val wifiEnabled = getSettingsUseCase.getWifiEnabled()
                val wifiSsid = getSettingsUseCase.getWifiSsid()
                val wifiPassword = getSettingsUseCase.getWifiPassword()
                val wifiTxInterval = getSettingsUseCase.getWifiTxInterval()
                
                // Load BLE settings
                val bleEnabled = getSettingsUseCase.getBleEnabled()
                val bleDeviceName = getSettingsUseCase.getBleDeviceName()
                val bleTxInterval = getSettingsUseCase.getBleTxInterval()
                
                // Load LoRaWAN settings
                val lorawanDevAddr = getSettingsUseCase.getLorawanDevAddr()
                val lorawanNwksKey = getSettingsUseCase.getLorawanNwksKey()
                val lorawanAppsKey = getSettingsUseCase.getLorawanAppsKey()
                val lorawanRegion = getSettingsUseCase.getLorawanRegion()
                val txInterval = getSettingsUseCase.getTxInterval()
                
                // Load OTA settings
                val otaEnabled = getSettingsUseCase.getOtaEnabled()
                val otaServerUrl = getSettingsUseCase.getOtaServerUrl()
                val otaHttpUsername = getSettingsUseCase.getOtaHttpUsername()
                val otaHttpPassword = getSettingsUseCase.getOtaHttpPassword()
                
                // Load sensor configuration
                val wcs6800Sensitivity = getSettingsUseCase.getWcs6800Sensitivity()
                val wcs6800OffsetVoltage = getSettingsUseCase.getWcs6800OffsetVoltage()
                val adcRefVoltage = getSettingsUseCase.getAdcRefVoltage()
                
                // Load advanced settings
                val debugLevel = getSettingsUseCase.getDebugLevel()
                val useAdaptiveSamplingRate = getSettingsUseCase.getUseAdaptiveSamplingRate()
                val measurementInterval = getSettingsUseCase.getMeasurementInterval()
                val dataFormat = getSettingsUseCase.getDataFormat()
                
                // Load app settings
                val refreshInterval = getSettingsUseCase.getRefreshInterval()
                val notificationsEnabled = getSettingsUseCase.getNotificationsEnabled()
                
                _uiState.update {
                    it.copy(
                        // Device settings
                        deviceId = deviceId,
                        deviceName = deviceName,
                        
                        // ChirpStack settings
                        serverUrl = serverUrl,
                        apiKey = apiKey,
                        
                        // MQTT settings
                        mqttBrokerUrl = mqttBrokerUrl,
                        mqttPort = mqttPort,
                        mqttUsername = mqttUsername,
                        mqttPassword = mqttPassword,
                        mqttTopic = mqttTopic,
                        mqttEnabled = mqttEnabled,
                        
                        // WiFi settings
                        wifiEnabled = wifiEnabled,
                        wifiSsid = wifiSsid,
                        wifiPassword = wifiPassword,
                        wifiTxInterval = wifiTxInterval,
                        
                        // BLE settings
                        bleEnabled = bleEnabled,
                        bleDeviceName = bleDeviceName,
                        bleTxInterval = bleTxInterval,
                        
                        // LoRaWAN settings
                        lorawanDevAddr = lorawanDevAddr,
                        lorawanNwksKey = lorawanNwksKey,
                        lorawanAppsKey = lorawanAppsKey,
                        lorawanRegion = lorawanRegion,
                        txInterval = txInterval,
                        
                        // OTA settings
                        otaEnabled = otaEnabled,
                        otaServerUrl = otaServerUrl,
                        otaHttpUsername = otaHttpUsername,
                        otaHttpPassword = otaHttpPassword,
                        
                        // Sensor configuration
                        wcs6800Sensitivity = wcs6800Sensitivity,
                        wcs6800OffsetVoltage = wcs6800OffsetVoltage,
                        adcRefVoltage = adcRefVoltage,
                        
                        // Advanced settings
                        debugLevel = debugLevel,
                        useAdaptiveSamplingRate = useAdaptiveSamplingRate,
                        measurementInterval = measurementInterval,
                        dataFormat = dataFormat,
                        
                        // App settings
                        refreshInterval = refreshInterval,
                        notificationsEnabled = notificationsEnabled,
                        
                        // UI state
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                Timber.e(e, "Error loading settings")
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Error loading settings: ${e.message}"
                    )
                }
            }
        }
    }
    
    fun saveSettings(
        // Device settings
        deviceId: String,
        deviceName: String,
        
        // ChirpStack settings
        serverUrl: String,
        apiKey: String,
        
        // MQTT settings
        mqttBrokerUrl: String,
        mqttPort: Int,
        mqttUsername: String?,
        mqttPassword: String?,
        mqttTopic: String,
        mqttEnabled: Boolean,
        
        // WiFi settings
        wifiEnabled: Boolean,
        wifiSsid: String,
        wifiPassword: String,
        wifiTxInterval: Int,
        
        // BLE settings
        bleEnabled: Boolean,
        bleDeviceName: String,
        bleTxInterval: Int,
        
        // LoRaWAN settings
        lorawanDevAddr: String,
        lorawanNwksKey: String,
        lorawanAppsKey: String,
        lorawanRegion: Int,
        txInterval: Int,
        
        // OTA settings
        otaEnabled: Boolean,
        otaServerUrl: String,
        otaHttpUsername: String,
        otaHttpPassword: String,
        
        // Sensor configuration
        wcs6800Sensitivity: Float,
        wcs6800OffsetVoltage: Float,
        adcRefVoltage: Float,
        
        // Advanced settings
        debugLevel: Int,
        useAdaptiveSamplingRate: Boolean,
        measurementInterval: Int,
        dataFormat: String,
        
        // App settings
        refreshInterval: Int,
        notificationsEnabled: Boolean
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            
            try {
                // Save device settings
                updateSettingsUseCase.setDeviceId(deviceId)
                updateSettingsUseCase.setDeviceName(deviceName)
                
                // Save ChirpStack settings
                updateSettingsUseCase.setChirpStackServerUrl(serverUrl)
                updateSettingsUseCase.setChirpStackApiKey(apiKey)
                
                // Save MQTT settings
                updateSettingsUseCase.setMqttBrokerUrl(mqttBrokerUrl)
                updateSettingsUseCase.setMqttPort(mqttPort)
                updateSettingsUseCase.setMqttUsername(mqttUsername)
                updateSettingsUseCase.setMqttPassword(mqttPassword)
                updateSettingsUseCase.setMqttTopic(mqttTopic)
                updateSettingsUseCase.setMqttEnabled(mqttEnabled)
                
                // Save WiFi settings
                updateSettingsUseCase.setWifiEnabled(wifiEnabled)
                updateSettingsUseCase.setWifiSsid(wifiSsid)
                updateSettingsUseCase.setWifiPassword(wifiPassword)
                updateSettingsUseCase.setWifiTxInterval(wifiTxInterval)
                
                // Save BLE settings
                updateSettingsUseCase.setBleEnabled(bleEnabled)
                updateSettingsUseCase.setBleDeviceName(bleDeviceName)
                updateSettingsUseCase.setBleTxInterval(bleTxInterval)
                
                // Save LoRaWAN settings
                updateSettingsUseCase.setLorawanDevAddr(lorawanDevAddr)
                updateSettingsUseCase.setLorawanNwksKey(lorawanNwksKey)
                updateSettingsUseCase.setLorawanAppsKey(lorawanAppsKey)
                updateSettingsUseCase.setLorawanRegion(lorawanRegion)
                updateSettingsUseCase.setTxInterval(txInterval)
                
                // Save OTA settings
                updateSettingsUseCase.setOtaEnabled(otaEnabled)
                updateSettingsUseCase.setOtaServerUrl(otaServerUrl)
                updateSettingsUseCase.setOtaHttpUsername(otaHttpUsername)
                updateSettingsUseCase.setOtaHttpPassword(otaHttpPassword)
                
                // Save sensor configuration
                updateSettingsUseCase.setWcs6800Sensitivity(wcs6800Sensitivity)
                updateSettingsUseCase.setWcs6800OffsetVoltage(wcs6800OffsetVoltage)
                updateSettingsUseCase.setAdcRefVoltage(adcRefVoltage)
                
                // Save advanced settings
                updateSettingsUseCase.setDebugLevel(debugLevel)
                updateSettingsUseCase.setUseAdaptiveSamplingRate(useAdaptiveSamplingRate)
                updateSettingsUseCase.setMeasurementInterval(measurementInterval)
                updateSettingsUseCase.setDataFormat(dataFormat)
                
                // Save app settings
                updateSettingsUseCase.setRefreshInterval(refreshInterval)
                updateSettingsUseCase.setNotificationsEnabled(notificationsEnabled)
                
                _uiState.update {
                    it.copy(
                        // Device settings
                        deviceId = deviceId,
                        deviceName = deviceName,
                        
                        // ChirpStack settings
                        serverUrl = serverUrl,
                        apiKey = apiKey,
                        
                        // MQTT settings
                        mqttBrokerUrl = mqttBrokerUrl,
                        mqttPort = mqttPort,
                        mqttUsername = mqttUsername,
                        mqttPassword = mqttPassword,
                        mqttTopic = mqttTopic,
                        mqttEnabled = mqttEnabled,
                        
                        // WiFi settings
                        wifiEnabled = wifiEnabled,
                        wifiSsid = wifiSsid,
                        wifiPassword = wifiPassword,
                        wifiTxInterval = wifiTxInterval,
                        
                        // BLE settings
                        bleEnabled = bleEnabled,
                        bleDeviceName = bleDeviceName,
                        bleTxInterval = bleTxInterval,
                        
                        // LoRaWAN settings
                        lorawanDevAddr = lorawanDevAddr,
                        lorawanNwksKey = lorawanNwksKey,
                        lorawanAppsKey = lorawanAppsKey,
                        lorawanRegion = lorawanRegion,
                        txInterval = txInterval,
                        
                        // OTA settings
                        otaEnabled = otaEnabled,
                        otaServerUrl = otaServerUrl,
                        otaHttpUsername = otaHttpUsername,
                        otaHttpPassword = otaHttpPassword,
                        
                        // Sensor configuration
                        wcs6800Sensitivity = wcs6800Sensitivity,
                        wcs6800OffsetVoltage = wcs6800OffsetVoltage,
                        adcRefVoltage = adcRefVoltage,
                        
                        // Advanced settings
                        debugLevel = debugLevel,
                        useAdaptiveSamplingRate = useAdaptiveSamplingRate,
                        measurementInterval = measurementInterval,
                        dataFormat = dataFormat,
                        
                        // App settings
                        refreshInterval = refreshInterval,
                        notificationsEnabled = notificationsEnabled,
                        
                        // UI state
                        isSaving = false,
                        saveSuccess = true
                    )
                }
            } catch (e: Exception) {
                Timber.e(e, "Error saving settings")
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        error = "Error saving settings: ${e.message}"
                    )
                }
            }
        }
    }
    
    fun testConnection() {
        viewModelScope.launch {
            val deviceId = _uiState.value.deviceId
            val serverUrl = _uiState.value.serverUrl
            
            if (deviceId.isBlank()) {
                _uiState.update { 
                    it.copy(
                        isTestingConnection = false,
                        connectionTestResult = "Device ID cannot be empty"
                    )
                }
                return@launch
            }
            
            _uiState.update { it.copy(isTestingConnection = true) }
            
            try {
                // Try to get device info from ChirpStack
                val response = chirpStackApi.getDeviceInfo(deviceId)
                
                if (response.isSuccessful && response.body() != null) {
                    _uiState.update { 
                        it.copy(
                            isTestingConnection = false,
                            connectionTestResult = "Connection successful! Device found: ${response.body()?.device?.name ?: deviceId}"
                        )
                    }
                } else {
                    val errorBody = response.errorBody()?.string() ?: ""
                    val errorMessage = when {
                        errorBody.contains("<!doctype html>") || errorBody.contains("<html>") -> 
                            "Server returned HTML instead of JSON. Check that the URL format is correct (IP:port)."
                        response.code() == 401 -> 
                            "Authentication failed. Check your API key."
                        response.code() == 404 -> 
                            "Device not found or API endpoint incorrect."
                        response.code() >= 500 -> 
                            "Server error. Please try again later."
                        else -> 
                            "Error: ${errorBody.take(100)}"
                    }
                    
                    _uiState.update { 
                        it.copy(
                            isTestingConnection = false,
                            connectionTestResult = errorMessage
                        )
                    }
                }
            } catch (e: Exception) {
                Timber.e(e, "Error testing connection")
                
                val errorMessage = when (e) {
                    is UnknownHostException -> 
                        "Could not resolve host. Check server address and internet connection."
                    is ConnectException -> 
                        "Connection failed. Server may be down or unreachable."
                    is SocketTimeoutException -> 
                        "Connection timed out. Server may be slow or unreachable."
                    is HttpException -> {
                        if (e.response()?.errorBody()?.contentType()?.toString()?.contains("text/html") == true) {
                            "Server returned HTML instead of JSON. Check that server URL includes http:// prefix."
                        } else {
                            "HTTP error: ${e.code()}"
                        }
                    }
                    is IllegalArgumentException -> {
                        if (e.message?.contains("URL") == true || e.message?.contains("host") == true) {
                            "Invalid URL format. Make sure server URL is in format 'host:port' or 'http://host:port'"
                        } else {
                            "Invalid parameters: ${e.message}"
                        }
                    }
                    else -> e.message ?: "Unknown error"
                }
                
                _uiState.update { 
                    it.copy(
                        isTestingConnection = false,
                        connectionTestResult = "Connection failed: $errorMessage"
                    )
                }
            }
        }
    }
    
    fun connectToMqttBroker(
        brokerUrl: String,
        username: String? = null,
        password: String? = null,
        topic: String = "ampmeter/device/+/data"
    ) {
        viewModelScope.launch {
            // Check if we need to disconnect
            if (_uiState.value.isMqttConnected) {
                _uiState.update { it.copy(isConnectingMqtt = true) }
                
                when (val result = deviceRepository.disconnectMqtt()) {
                    is Resource.Success -> {
                        _uiState.update { 
                            it.copy(
                                isMqttConnected = false,
                                isConnectingMqtt = false,
                                mqttConnectionResult = "Disconnected from MQTT broker"
                            )
                        }
                    }
                    is Resource.Error -> {
                        _uiState.update { 
                            it.copy(
                                isConnectingMqtt = false,
                                mqttConnectionResult = "Failed to disconnect: ${result.message}"
                            )
                        }
                    }
                    is Resource.Loading -> {
                        // Nothing to do here, loading state is handled by isConnectingMqtt
                    }
                }
                
                return@launch
            }
            
            _uiState.update { it.copy(isConnectingMqtt = true) }
            
            try {
                // Connect to MQTT broker
                when (val connectResult = deviceRepository.connectToMqttBroker(brokerUrl, username, password)) {
                    is Resource.Success -> {
                        // Subscribe to topic
                        when (val subscribeResult = deviceRepository.subscribeMqttTopics(listOf(topic))) {
                            is Resource.Success -> {
                                // Save MQTT settings
                                updateSettingsUseCase.setMqttBrokerUrl(brokerUrl)
                                updateSettingsUseCase.setMqttUsername(username)
                                updateSettingsUseCase.setMqttPassword(password)
                                updateSettingsUseCase.setMqttTopic(topic)
                                
                                _uiState.update { 
                                    it.copy(
                                        mqttBrokerUrl = brokerUrl,
                                        mqttUsername = username,
                                        mqttPassword = password,
                                        mqttTopic = topic,
                                        isMqttConnected = true,
                                        isConnectingMqtt = false,
                                        mqttConnectionResult = "Connected to MQTT broker and subscribed to $topic"
                                    )
                                }
                            }
                            is Resource.Error -> {
                                _uiState.update { 
                                    it.copy(
                                        isConnectingMqtt = false,
                                        mqttConnectionResult = "Connected but failed to subscribe: ${subscribeResult.message}"
                                    )
                                }
                            }
                            is Resource.Loading -> {
                                // Nothing to do here, loading state is handled by isConnectingMqtt
                            }
                        }
                    }
                    is Resource.Error -> {
                        _uiState.update { 
                            it.copy(
                                isConnectingMqtt = false,
                                mqttConnectionResult = "Failed to connect: ${connectResult.message}"
                            )
                        }
                    }
                    is Resource.Loading -> {
                        // Nothing to do here, loading state is handled by isConnectingMqtt
                    }
                }
            } catch (e: Exception) {
                Timber.e(e, "Error connecting to MQTT broker")
                _uiState.update { 
                    it.copy(
                        isConnectingMqtt = false,
                        mqttConnectionResult = "Error connecting to MQTT broker: ${e.message}"
                    )
                }
            }
        }
    }
    
    fun disconnectMqtt() {
        viewModelScope.launch {
            _uiState.update { it.copy(isConnectingMqtt = true) }
            
            when (val result = deviceRepository.disconnectMqtt()) {
                is Resource.Success -> {
                    _uiState.update { 
                        it.copy(
                            isMqttConnected = false,
                            isConnectingMqtt = false,
                            mqttConnectionResult = "Disconnected from MQTT broker"
                        )
                    }
                }
                is Resource.Error -> {
                    _uiState.update { 
                        it.copy(
                            isConnectingMqtt = false,
                            mqttConnectionResult = "Failed to disconnect: ${result.message}"
                        )
                    }
                }
                is Resource.Loading -> {
                    // Nothing to do here, loading state is handled by isConnectingMqtt
                }
            }
        }
    }
    
    fun startBleScan() {
        viewModelScope.launch {
            _uiState.update { it.copy(isScanningBle = true) }
            
            when (val result = deviceRepository.startBleScan()) {
                is Resource.Success -> {
                    _uiState.update { it.copy(isScanningBle = true) }
                    
                    // Wait for scan to complete (handled by the BLE service)
                    kotlinx.coroutines.delay(10000) // 10 seconds
                    
                    _uiState.update { it.copy(isScanningBle = false) }
                }
                is Resource.Error -> {
                    _uiState.update { 
                        it.copy(
                            isScanningBle = false,
                            error = "Failed to start BLE scan: ${result.message}"
                        )
                    }
                }
                is Resource.Loading -> {
                    // Nothing to do here, loading state is handled by isScanningBle
                }
            }
        }
    }
    
    fun stopBleScan() {
        viewModelScope.launch {
            when (val result = deviceRepository.stopBleScan()) {
                is Resource.Success -> {
                    _uiState.update { it.copy(isScanningBle = false) }
                }
                is Resource.Error -> {
                    _uiState.update { 
                        it.copy(
                            isScanningBle = false,
                            error = "Failed to stop BLE scan: ${result.message}"
                        )
                    }
                }
                is Resource.Loading -> {
                    // Nothing to do here, loading state is handled by isScanningBle
                }
            }
        }
    }
    
    fun connectToBleDevice(device: BluetoothDevice) {
        viewModelScope.launch {
            when (val result = deviceRepository.connectToBleDevice(device)) {
                is Resource.Success -> {
                    // Connection state is updated via the flow observer
                }
                is Resource.Error -> {
                    _uiState.update { 
                        it.copy(error = "Failed to connect to device: ${result.message}")
                    }
                }
                is Resource.Loading -> {
                    // Connection state is updated via the flow observer
                }
            }
        }
    }
    
    fun disconnectBle() {
        viewModelScope.launch {
            when (val result = deviceRepository.disconnectBle()) {
                is Resource.Success -> {
                    // Connection state is updated via the flow observer
                }
                is Resource.Error -> {
                    _uiState.update { 
                        it.copy(error = "Failed to disconnect: ${result.message}")
                    }
                }
                is Resource.Loading -> {
                    // Connection state is updated via the flow observer
                }
            }
        }
    }
    
    fun clearSaveSuccess() {
        _uiState.update { it.copy(saveSuccess = false) }
    }
    
    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
    
    fun clearConnectionTestResult() {
        _uiState.update { it.copy(connectionTestResult = null) }
    }
    
    fun clearMqttConnectionResult() {
        _uiState.update { it.copy(mqttConnectionResult = null) }
    }
} 