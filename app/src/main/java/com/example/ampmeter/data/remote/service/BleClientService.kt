package com.example.ampmeter.data.remote.service

import android.app.Service
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothManager
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import com.example.ampmeter.domain.model.DeviceReading
import com.example.ampmeter.util.PayloadDecoder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import no.nordicsemi.android.ble.BleManager
import no.nordicsemi.android.ble.ConnectionPriorityRequest
import no.nordicsemi.android.ble.callback.DataReceivedCallback
import no.nordicsemi.android.ble.data.Data
import no.nordicsemi.android.ble.observer.ConnectionObserver
import timber.log.Timber
import java.util.*

/**
 * Service for BLE client functionality to discover and connect to nearby AmpMeter transmitter devices.
 */
class BleClientService : Service() {
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val binder = BleBinder()
    
    // Bluetooth components
    private var bluetoothAdapter: BluetoothAdapter? = null
    private var scanning = false
    private val scanResults = mutableMapOf<String, BluetoothDevice>()
    
    // Nordic BLE Manager
    private var ampMeterBleManager: AmpMeterBleManager? = null
    
    // Scan results flow
    private val _scanResultsFlow = MutableStateFlow<Map<String, BluetoothDevice>>(emptyMap())
    val scanResultsFlow = _scanResultsFlow.asStateFlow()
    
    // Connection state flow
    private val _connectionState = MutableStateFlow<ConnectionState>(ConnectionState.Disconnected)
    val connectionState = _connectionState.asStateFlow()
    
    // Reading data flow
    private val _deviceReadings = MutableSharedFlow<DeviceReading>(replay = 0)
    val deviceReadings = _deviceReadings.asSharedFlow()
    
    // Service UUID constants
    companion object {
        private const val SCAN_PERIOD: Long = 10000 // 10 seconds
        
        // Default service UUID for AmpMeter devices
        // These would match the actual UUIDs used by your LoRaWAN transmitter
        private val AMPMETER_SERVICE_UUID = UUID.fromString("6e400001-b5a3-f393-e0a9-e50e24dcca9e")
        private val AMPMETER_TX_CHAR_UUID = UUID.fromString("6e400002-b5a3-f393-e0a9-e50e24dcca9e")
        private val AMPMETER_RX_CHAR_UUID = UUID.fromString("6e400003-b5a3-f393-e0a9-e50e24dcca9e")
        
        // Helper method to start the service
        fun startService(context: Context) {
            val startIntent = Intent(context, BleClientService::class.java)
            context.startService(startIntent)
        }
        
        // Helper method to stop the service
        fun stopService(context: Context) {
            val stopIntent = Intent(context, BleClientService::class.java)
            context.stopService(stopIntent)
        }
    }
    
    override fun onCreate() {
        super.onCreate()
        Timber.d("BLE Client Service created")
        
        // Initialize BluetoothAdapter
        val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothAdapter = bluetoothManager.adapter
    }
    
    override fun onBind(intent: Intent?): IBinder {
        return binder
    }
    
    /**
     * Start scanning for AmpMeter BLE devices.
     */
    fun startScan() {
        if (scanning) return
        
        scanResults.clear()
        _scanResultsFlow.value = emptyMap()
        
        val bluetoothLeScanner = bluetoothAdapter?.bluetoothLeScanner ?: run {
            Timber.e("Bluetooth not available")
            return
        }
        
        // Set up scan filters for AmpMeter devices
        // In a real app, you'd filter based on service UUID or device name
        val filters = listOf<ScanFilter>()
        
        // Set scan settings
        val settings = ScanSettings.Builder()
            .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
            .build()
        
        // Start scanning
        try {
            bluetoothLeScanner.startScan(filters, settings, scanCallback)
            scanning = true
            Timber.d("BLE scan started")
            
            // Stop scanning after SCAN_PERIOD
            serviceScope.launch {
                kotlinx.coroutines.delay(SCAN_PERIOD)
                if (scanning) {
                    stopScan()
                }
            }
        } catch (e: Exception) {
            Timber.e(e, "Failed to start BLE scan")
        }
    }
    
    /**
     * Stop the BLE scan.
     */
    fun stopScan() {
        if (!scanning) return
        
        val bluetoothLeScanner = bluetoothAdapter?.bluetoothLeScanner ?: return
        
        try {
            bluetoothLeScanner.stopScan(scanCallback)
            scanning = false
            Timber.d("BLE scan stopped")
        } catch (e: Exception) {
            Timber.e(e, "Failed to stop BLE scan")
        }
    }
    
    /**
     * Connect to a specific BLE device.
     */
    fun connectToDevice(device: BluetoothDevice) {
        // Disconnect from any existing connection
        ampMeterBleManager?.disconnect()
        
        // Create and connect a new BLE manager
        ampMeterBleManager = AmpMeterBleManager(applicationContext).apply {
            setConnectionObserver(object : ConnectionObserver {
                override fun onDeviceConnecting(device: BluetoothDevice) {
                    _connectionState.value = ConnectionState.Connecting
                }
                
                override fun onDeviceConnected(device: BluetoothDevice) {
                    _connectionState.value = ConnectionState.Connected
                }
                
                override fun onDeviceDisconnecting(device: BluetoothDevice) {
                    // Optional handling for disconnecting state
                }
                
                override fun onDeviceDisconnected(device: BluetoothDevice, reason: Int) {
                    _connectionState.value = ConnectionState.Disconnected
                }
                
                override fun onDeviceReady(device: BluetoothDevice) {
                    // Device is ready for communication
                }

                override fun onDeviceFailedToConnect(device: BluetoothDevice, reason: Int) {
                    _connectionState.value = ConnectionState.Disconnected
                    Timber.e("Failed to connect to device: $device, reason: $reason")
                }
            })
            
            connect(device)
                .retry(3, 200)
                .useAutoConnect(true)
                .enqueue()
        }
    }
    
    /**
     * Disconnect from the current device.
     */
    fun disconnect() {
        ampMeterBleManager?.disconnect()
        ampMeterBleManager = null
    }
    
    /**
     * Send command to the connected device.
     */
    fun sendCommand(command: ByteArray): Boolean {
        if (_connectionState.value != ConnectionState.Connected) {
            Timber.d("Not connected to device")
            return false
        }
        
        return ampMeterBleManager?.sendCommand(command) ?: false
    }
    
    /**
     * ScanCallback for BLE device discovery.
     */
    private val scanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            val device = result.device
            val deviceName = device.name ?: "Unknown Device"
            val deviceAddress = device.address
            val rssi = result.rssi
            
            Timber.d("Found device: $deviceName ($deviceAddress), RSSI: $rssi")
            
            // Add to scan results
            scanResults[deviceAddress] = device
            _scanResultsFlow.value = HashMap(scanResults)
        }
        
        override fun onScanFailed(errorCode: Int) {
            Timber.e("BLE scan failed with error: $errorCode")
            scanning = false
        }
    }
    
    /**
     * BLE Manager for AmpMeter devices.
     */
    inner class AmpMeterBleManager(context: Context) : BleManager(context) {
        private var txCharacteristic: BluetoothGattCharacteristic? = null
        private var rxCharacteristic: BluetoothGattCharacteristic? = null
        
        override fun isRequiredServiceSupported(gatt: BluetoothGatt): Boolean {
            // Check if the device has the required service and characteristics
            val service = gatt.getService(AMPMETER_SERVICE_UUID) ?: return false
            
            // Get the TX characteristic (write)
            txCharacteristic = service.getCharacteristic(AMPMETER_TX_CHAR_UUID)
            if (txCharacteristic == null || txCharacteristic?.properties?.and(BluetoothGattCharacteristic.PROPERTY_WRITE) == 0) {
                return false
            }
            
            // Get the RX characteristic (notifications)
            rxCharacteristic = service.getCharacteristic(AMPMETER_RX_CHAR_UUID)
            if (rxCharacteristic == null || rxCharacteristic?.properties?.and(BluetoothGattCharacteristic.PROPERTY_NOTIFY) == 0) {
                return false
            }
            
            return true
        }
        
        override fun initialize() {
            // Set notification callback
            setNotificationCallback(rxCharacteristic)
                .with { device, data ->
                    // Process received data
                    processReceivedData(device, data.value)
                }
            
            // Enable notifications
            enableNotifications(rxCharacteristic)
                .enqueue()
                
            Timber.d("BLE connection initialized")
        }
        
        override fun onServicesInvalidated() {
            // Release resources
            txCharacteristic = null
            rxCharacteristic = null
        }
        
        /**
         * Send command to the device.
         */
        fun sendCommand(command: ByteArray): Boolean {
            if (txCharacteristic == null) {
                Timber.e("TX characteristic not available")
                return false
            }
            
            // Write the command
            writeCharacteristic(txCharacteristic, command)
                .with { _, data -> Timber.d("Data sent: ${data.value?.contentToString()}") }
                .enqueue()
                
            return true
        }
        
        /**
         * Process data received from the device.
         */
        private fun processReceivedData(device: BluetoothDevice, data: ByteArray?) {
            if (data == null) return
            
            Timber.d("Received data from ${device.address}: ${data.contentToString()}")
            
            // Process the data
            serviceScope.launch {
                try {
                    // Convert binary to base64 for PayloadDecoder
                    val base64Data = android.util.Base64.encodeToString(data, android.util.Base64.DEFAULT)
                    
                    // Use existing PayloadDecoder to parse the data
                    val deviceReading = PayloadDecoder.decodePayload(base64Data, device.address)
                    
                    _deviceReadings.emit(deviceReading)
                } catch (e: Exception) {
                    Timber.e(e, "Error processing BLE data")
                }
            }
        }
    }
    
    inner class BleBinder : Binder() {
        fun getService(): BleClientService = this@BleClientService
    }
    
    override fun onDestroy() {
        stopScan()
        disconnect()
        super.onDestroy()
    }
    
    /**
     * Connection states for BLE devices.
     */
    enum class ConnectionState {
        Disconnected,
        Connecting,
        Connected
    }
} 