package com.example.ampmeter.presentation.ui.settings

import android.bluetooth.BluetoothDevice

/**
 * UI state for the Settings screen.
 */
data class SettingsUiState(
    // Device settings
    val deviceId: String = "",
    val deviceName: String = "",
    
    // ChirpStack settings
    val serverUrl: String = "",
    val apiKey: String = "",
    
    // MQTT settings
    val mqttBrokerUrl: String = "",
    val mqttPort: Int = 1883,
    val mqttUsername: String? = null,
    val mqttPassword: String? = null,
    val mqttTopic: String = "ampmeter/device/+/data",
    val mqttEnabled: Boolean = false,
    val isMqttConnected: Boolean = false,
    val isConnectingMqtt: Boolean = false,
    val mqttConnectionResult: String? = null,
    
    // BLE settings
    val bleDevices: Map<String, BluetoothDevice> = emptyMap(),
    val bleEnabled: Boolean = true,
    val bleDeviceName: String = "AmpMeter_Device",
    val bleTxInterval: Int = 5000,
    val isScanningBle: Boolean = false,
    val bleConnectionState: Int = 0, // 0: disconnected, 1: connecting, 2: connected
    
    // WiFi settings
    val wifiEnabled: Boolean = true,
    val wifiSsid: String = "",
    val wifiPassword: String = "",
    val wifiTxInterval: Int = 10000,
    
    // LoRaWAN settings
    val lorawanDevAddr: String = "",
    val lorawanNwksKey: String = "",
    val lorawanAppsKey: String = "",
    val lorawanRegion: Int = 3,  // Default: IN865
    val txInterval: Int = 60000,  // 60 seconds in milliseconds
    
    // OTA settings
    val otaEnabled: Boolean = false,
    val otaServerUrl: String = "",
    val otaHttpUsername: String = "",
    val otaHttpPassword: String = "",
    
    // Sensor configuration
    val wcs6800Sensitivity: Float = 0.0429f,
    val wcs6800OffsetVoltage: Float = 1.65f,
    val adcRefVoltage: Float = 3.3f,
    
    // Advanced settings
    val debugLevel: Int = 2,
    val useAdaptiveSamplingRate: Boolean = false,
    val measurementInterval: Int = 1000,
    val dataFormat: String = "JSON",
    
    // App settings
    val refreshInterval: Int = 30,
    val notificationsEnabled: Boolean = true,
    
    // UI state
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val isTestingConnection: Boolean = false,
    val saveSuccess: Boolean = false,
    val connectionTestResult: String? = null,
    val error: String? = null
) 