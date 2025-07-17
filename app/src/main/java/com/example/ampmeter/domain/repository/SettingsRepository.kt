package com.example.ampmeter.domain.repository

import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for app settings.
 */
interface SettingsRepository {
    // ChirpStack settings
    suspend fun getChirpStackServerUrl(): String
    suspend fun setChirpStackServerUrl(url: String)
    suspend fun getChirpStackApiKey(): String
    suspend fun setChirpStackApiKey(apiKey: String)
    suspend fun getDeviceId(): String
    suspend fun setDeviceId(deviceId: String)
    suspend fun getApplicationId(): String
    suspend fun setApplicationId(applicationId: String)
    
    // Supabase settings
    suspend fun getSupabaseUrl(): String
    suspend fun setSupabaseUrl(url: String)
    suspend fun getSupabaseApiKey(): String
    suspend fun setSupabaseApiKey(apiKey: String)
    suspend fun getSupabaseTableName(): String
    suspend fun setSupabaseTableName(tableName: String)
    
    // MQTT settings
    suspend fun getMqttBrokerUrl(): String
    suspend fun setMqttBrokerUrl(url: String)
    suspend fun getMqttPort(): Int
    suspend fun setMqttPort(port: Int)
    suspend fun getMqttEnabled(): Boolean
    suspend fun setMqttEnabled(enabled: Boolean)
    suspend fun getMqttUsername(): String?
    suspend fun setMqttUsername(username: String?)
    suspend fun getMqttPassword(): String?
    suspend fun setMqttPassword(password: String?)
    suspend fun getMqttTopic(): String
    suspend fun setMqttTopic(topic: String)
    
    // WiFi settings
    suspend fun getWifiEnabled(): Boolean
    suspend fun setWifiEnabled(enabled: Boolean)
    suspend fun getWifiSsid(): String
    suspend fun setWifiSsid(ssid: String)
    suspend fun getWifiPassword(): String
    suspend fun setWifiPassword(password: String)
    suspend fun getWifiTxInterval(): Int
    suspend fun setWifiTxInterval(interval: Int)
    
    // BLE settings
    suspend fun getBleEnabled(): Boolean
    suspend fun setBleEnabled(enabled: Boolean)
    suspend fun getBleDeviceName(): String
    suspend fun setBleDeviceName(name: String)
    suspend fun getBleTxInterval(): Int
    suspend fun setBleTxInterval(interval: Int)
    
    // LoRaWAN settings
    suspend fun getLorawanDevAddr(): String
    suspend fun setLorawanDevAddr(devAddr: String)
    suspend fun getLorawanNwksKey(): String
    suspend fun setLorawanNwksKey(nwksKey: String)
    suspend fun getLorawanAppsKey(): String
    suspend fun setLorawanAppsKey(appsKey: String)
    suspend fun getLorawanRegion(): Int
    suspend fun setLorawanRegion(region: Int)
    suspend fun getTxInterval(): Int
    suspend fun setTxInterval(interval: Int)
    
    // OTA settings
    suspend fun getOtaEnabled(): Boolean
    suspend fun setOtaEnabled(enabled: Boolean)
    suspend fun getOtaServerUrl(): String
    suspend fun setOtaServerUrl(url: String)
    suspend fun getOtaHttpUsername(): String
    suspend fun setOtaHttpUsername(username: String)
    suspend fun getOtaHttpPassword(): String
    suspend fun setOtaHttpPassword(password: String)
    
    // Sensor configuration
    suspend fun getWcs6800Sensitivity(): Float
    suspend fun setWcs6800Sensitivity(sensitivity: Float)
    suspend fun getWcs6800OffsetVoltage(): Float
    suspend fun setWcs6800OffsetVoltage(offsetVoltage: Float)
    suspend fun getAdcRefVoltage(): Float
    suspend fun setAdcRefVoltage(refVoltage: Float)
    
    // Advanced settings
    suspend fun getDebugLevel(): Int
    suspend fun setDebugLevel(level: Int)
    suspend fun getUseAdaptiveSamplingRate(): Boolean
    suspend fun setUseAdaptiveSamplingRate(useAdaptive: Boolean)
    suspend fun getMeasurementInterval(): Int
    suspend fun setMeasurementInterval(interval: Int)
    suspend fun getDataFormat(): String
    suspend fun setDataFormat(format: String)
    
    // App preferences
    suspend fun getRefreshInterval(): Int
    suspend fun setRefreshInterval(intervalSeconds: Int)
    fun getRefreshIntervalFlow(): Flow<Int>
    
    suspend fun getDataRetentionPeriod(): Int
    suspend fun setDataRetentionPeriod(periodDays: Int)
    
    suspend fun getThemeMode(): Int
    suspend fun setThemeMode(themeMode: Int)
    
    suspend fun getNotificationsEnabled(): Boolean
    suspend fun setNotificationsEnabled(enabled: Boolean)
    
    // Device settings
    suspend fun getDeviceName(): String
    suspend fun setDeviceName(name: String)
    
    suspend fun getAlertThreshold(): Double
    suspend fun setAlertThreshold(threshold: Double)
} 