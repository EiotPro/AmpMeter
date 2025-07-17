package com.example.ampmeter.domain.repository

import android.bluetooth.BluetoothDevice
import com.example.ampmeter.domain.model.DeviceReading
import com.example.ampmeter.domain.model.Resource
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for device-related operations.
 * Follows the repository pattern to abstract data sources.
 */
interface DeviceRepository {
    /**
     * Gets the latest reading for a device.
     * 
     * @param deviceId The device identifier
     * @return Resource wrapping DeviceReading
     */
    suspend fun getLatestReading(deviceId: String): Resource<DeviceReading>
    
    /**
     * Gets a list of device readings with pagination.
     * 
     * @param deviceId The device identifier
     * @param limit Maximum number of readings to return
     * @param offset Starting position for pagination
     * @return Resource wrapping List of DeviceReading
     */
    suspend fun getDeviceReadings(
        deviceId: String, 
        limit: Int, 
        offset: Int
    ): Resource<List<DeviceReading>>
    
    /**
     * Gets a Flow of device readings for observing changes.
     * 
     * @param deviceId The device identifier
     * @return Flow of List of DeviceReading
     */
    fun getDeviceReadingsFlow(deviceId: String): Flow<List<DeviceReading>>
    
    /**
     * Synchronizes local data with cloud.
     * 
     * @return Resource wrapping Unit
     */
    suspend fun syncWithCloud(): Resource<Unit>
    
    /**
     * Inserts a device reading.
     * 
     * @param deviceReading The device reading to insert
     * @return Resource wrapping the inserted DeviceReading
     */
    suspend fun insertDeviceReading(deviceReading: DeviceReading): Resource<DeviceReading>
    
    /**
     * MQTT Connectivity Methods
     */
    
    /**
     * Connects to MQTT broker.
     * 
     * @param brokerUrl The MQTT broker URL
     * @param username Optional username for authentication
     * @param password Optional password for authentication
     * @return Resource wrapping connection status
     */
    suspend fun connectToMqttBroker(
        brokerUrl: String,
        username: String? = null,
        password: String? = null
    ): Resource<Boolean>
    
    /**
     * Subscribes to MQTT topics.
     * 
     * @param topics List of topics to subscribe to
     * @return Resource wrapping subscription status
     */
    suspend fun subscribeMqttTopics(topics: List<String>): Resource<Boolean>
    
    /**
     * Disconnects from MQTT broker.
     * 
     * @return Resource wrapping disconnect status
     */
    suspend fun disconnectMqtt(): Resource<Boolean>
    
    /**
     * Gets Flow of device readings from MQTT.
     * 
     * @return Flow of DeviceReading
     */
    fun getMqttDeviceReadingsFlow(): Flow<DeviceReading>
    
    /**
     * Bluetooth Connectivity Methods
     */
    
    /**
     * Starts scanning for BLE devices.
     * 
     * @return Resource wrapping scan start status
     */
    suspend fun startBleScan(): Resource<Boolean>
    
    /**
     * Stops scanning for BLE devices.
     * 
     * @return Resource wrapping scan stop status
     */
    suspend fun stopBleScan(): Resource<Boolean>
    
    /**
     * Gets Flow of discovered BLE devices.
     * 
     * @return Flow of Map of device address to BluetoothDevice
     */
    fun getBleDevicesFlow(): Flow<Map<String, BluetoothDevice>>
    
    /**
     * Connects to a BLE device.
     * 
     * @param device The BluetoothDevice to connect to
     * @return Resource wrapping connection status
     */
    suspend fun connectToBleDevice(device: BluetoothDevice): Resource<Boolean>
    
    /**
     * Disconnects from the current BLE device.
     * 
     * @return Resource wrapping disconnect status
     */
    suspend fun disconnectBle(): Resource<Boolean>
    
    /**
     * Gets Flow of BLE connection state.
     * 
     * @return Flow of connection state
     */
    fun getBleConnectionStateFlow(): Flow<Int>
    
    /**
     * Gets Flow of device readings from BLE.
     * 
     * @return Flow of DeviceReading
     */
    fun getBleDeviceReadingsFlow(): Flow<DeviceReading>
    
    /**
     * Sends a command to the connected BLE device.
     * 
     * @param command The command to send
     * @return Resource wrapping send status
     */
    suspend fun sendBleCommand(command: ByteArray): Resource<Boolean>
} 