package com.example.ampmeter.data.repository

import com.example.ampmeter.data.local.datastore.PreferencesKeys
import com.example.ampmeter.data.local.datastore.SettingsDataStore
import com.example.ampmeter.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of SettingsRepository using DataStore.
 */
@Singleton
class SettingsRepositoryImpl @Inject constructor(
    private val settingsDataStore: SettingsDataStore
) : SettingsRepository {
    
    // ChirpStack settings
    override suspend fun getChirpStackServerUrl(): String {
        return settingsDataStore.getString(PreferencesKeys.CHIRP_STACK_SERVER_URL).first()
    }
    
    override suspend fun setChirpStackServerUrl(url: String) {
        settingsDataStore.setString(PreferencesKeys.CHIRP_STACK_SERVER_URL, url)
    }
    
    override suspend fun getChirpStackApiKey(): String {
        return settingsDataStore.getString(PreferencesKeys.CHIRP_STACK_API_KEY).first()
    }
    
    override suspend fun setChirpStackApiKey(apiKey: String) {
        settingsDataStore.setString(PreferencesKeys.CHIRP_STACK_API_KEY, apiKey)
    }
    
    override suspend fun getDeviceId(): String {
        return settingsDataStore.getString(PreferencesKeys.DEVICE_ID).first()
    }
    
    override suspend fun setDeviceId(deviceId: String) {
        settingsDataStore.setString(PreferencesKeys.DEVICE_ID, deviceId)
    }
    
    override suspend fun getApplicationId(): String {
        return settingsDataStore.getString(PreferencesKeys.APPLICATION_ID).first()
    }
    
    override suspend fun setApplicationId(applicationId: String) {
        settingsDataStore.setString(PreferencesKeys.APPLICATION_ID, applicationId)
    }
    
    // Supabase settings
    override suspend fun getSupabaseUrl(): String {
        return settingsDataStore.getString(PreferencesKeys.SUPABASE_URL).first()
    }
    
    override suspend fun setSupabaseUrl(url: String) {
        settingsDataStore.setString(PreferencesKeys.SUPABASE_URL, url)
    }
    
    override suspend fun getSupabaseApiKey(): String {
        return settingsDataStore.getString(PreferencesKeys.SUPABASE_API_KEY).first()
    }
    
    override suspend fun setSupabaseApiKey(apiKey: String) {
        settingsDataStore.setString(PreferencesKeys.SUPABASE_API_KEY, apiKey)
    }
    
    override suspend fun getSupabaseTableName(): String {
        return settingsDataStore.getString(PreferencesKeys.SUPABASE_TABLE_NAME, "device_logs").first()
    }
    
    override suspend fun setSupabaseTableName(tableName: String) {
        settingsDataStore.setString(PreferencesKeys.SUPABASE_TABLE_NAME, tableName)
    }
    
    // MQTT settings
    override suspend fun getMqttBrokerUrl(): String {
        return settingsDataStore.getString(PreferencesKeys.MQTT_BROKER_URL, "").first()
    }
    
    override suspend fun setMqttBrokerUrl(url: String) {
        settingsDataStore.setString(PreferencesKeys.MQTT_BROKER_URL, url)
    }
    
    override suspend fun getMqttPort(): Int {
        return settingsDataStore.getInt(PreferencesKeys.MQTT_PORT, 1883).first()
    }
    
    override suspend fun setMqttPort(port: Int) {
        settingsDataStore.setInt(PreferencesKeys.MQTT_PORT, port)
    }
    
    override suspend fun getMqttEnabled(): Boolean {
        return settingsDataStore.getBoolean(PreferencesKeys.MQTT_ENABLED, false).first()
    }
    
    override suspend fun setMqttEnabled(enabled: Boolean) {
        settingsDataStore.setBoolean(PreferencesKeys.MQTT_ENABLED, enabled)
    }
    
    override suspend fun getMqttUsername(): String? {
        val username = settingsDataStore.getString(PreferencesKeys.MQTT_USERNAME, "").first()
        return if (username.isEmpty()) null else username
    }
    
    override suspend fun setMqttUsername(username: String?) {
        settingsDataStore.setString(PreferencesKeys.MQTT_USERNAME, username ?: "")
    }
    
    override suspend fun getMqttPassword(): String? {
        val password = settingsDataStore.getString(PreferencesKeys.MQTT_PASSWORD, "").first()
        return if (password.isEmpty()) null else password
    }
    
    override suspend fun setMqttPassword(password: String?) {
        settingsDataStore.setString(PreferencesKeys.MQTT_PASSWORD, password ?: "")
    }
    
    override suspend fun getMqttTopic(): String {
        return settingsDataStore.getString(PreferencesKeys.MQTT_TOPIC, "ampmeter/device/+/data").first()
    }
    
    override suspend fun setMqttTopic(topic: String) {
        settingsDataStore.setString(PreferencesKeys.MQTT_TOPIC, topic)
    }
    
    // WiFi settings
    override suspend fun getWifiEnabled(): Boolean {
        return settingsDataStore.getBoolean(PreferencesKeys.WIFI_ENABLED, true).first()
    }
    
    override suspend fun setWifiEnabled(enabled: Boolean) {
        settingsDataStore.setBoolean(PreferencesKeys.WIFI_ENABLED, enabled)
    }
    
    override suspend fun getWifiSsid(): String {
        return settingsDataStore.getString(PreferencesKeys.WIFI_SSID, "").first()
    }
    
    override suspend fun setWifiSsid(ssid: String) {
        settingsDataStore.setString(PreferencesKeys.WIFI_SSID, ssid)
    }
    
    override suspend fun getWifiPassword(): String {
        return settingsDataStore.getString(PreferencesKeys.WIFI_PASSWORD, "").first()
    }
    
    override suspend fun setWifiPassword(password: String) {
        settingsDataStore.setString(PreferencesKeys.WIFI_PASSWORD, password)
    }
    
    override suspend fun getWifiTxInterval(): Int {
        return settingsDataStore.getInt(PreferencesKeys.WIFI_TX_INTERVAL, 10000).first()
    }
    
    override suspend fun setWifiTxInterval(interval: Int) {
        settingsDataStore.setInt(PreferencesKeys.WIFI_TX_INTERVAL, interval)
    }
    
    // BLE settings
    override suspend fun getBleEnabled(): Boolean {
        return settingsDataStore.getBoolean(PreferencesKeys.BLE_ENABLED, true).first()
    }
    
    override suspend fun setBleEnabled(enabled: Boolean) {
        settingsDataStore.setBoolean(PreferencesKeys.BLE_ENABLED, enabled)
    }
    
    override suspend fun getBleDeviceName(): String {
        return settingsDataStore.getString(PreferencesKeys.BLE_DEVICE_NAME, "AmpMeter_Device").first()
    }
    
    override suspend fun setBleDeviceName(name: String) {
        settingsDataStore.setString(PreferencesKeys.BLE_DEVICE_NAME, name)
    }
    
    override suspend fun getBleTxInterval(): Int {
        return settingsDataStore.getInt(PreferencesKeys.BLE_TX_INTERVAL, 5000).first()
    }
    
    override suspend fun setBleTxInterval(interval: Int) {
        settingsDataStore.setInt(PreferencesKeys.BLE_TX_INTERVAL, interval)
    }
    
    // LoRaWAN settings
    override suspend fun getLorawanDevAddr(): String {
        return settingsDataStore.getString(PreferencesKeys.LORAWAN_DEV_ADDR, "01d3257c").first()
    }
    
    override suspend fun setLorawanDevAddr(devAddr: String) {
        settingsDataStore.setString(PreferencesKeys.LORAWAN_DEV_ADDR, devAddr)
    }
    
    override suspend fun getLorawanNwksKey(): String {
        return settingsDataStore.getString(PreferencesKeys.LORAWAN_NWKS_KEY, "").first()
    }
    
    override suspend fun setLorawanNwksKey(nwksKey: String) {
        settingsDataStore.setString(PreferencesKeys.LORAWAN_NWKS_KEY, nwksKey)
    }
    
    override suspend fun getLorawanAppsKey(): String {
        return settingsDataStore.getString(PreferencesKeys.LORAWAN_APPS_KEY, "").first()
    }
    
    override suspend fun setLorawanAppsKey(appsKey: String) {
        settingsDataStore.setString(PreferencesKeys.LORAWAN_APPS_KEY, appsKey)
    }
    
    override suspend fun getLorawanRegion(): Int {
        return settingsDataStore.getInt(PreferencesKeys.LORAWAN_REGION, 3).first()
    }
    
    override suspend fun setLorawanRegion(region: Int) {
        settingsDataStore.setInt(PreferencesKeys.LORAWAN_REGION, region)
    }
    
    override suspend fun getTxInterval(): Int {
        return settingsDataStore.getInt(PreferencesKeys.TX_INTERVAL, 60000).first()
    }
    
    override suspend fun setTxInterval(interval: Int) {
        settingsDataStore.setInt(PreferencesKeys.TX_INTERVAL, interval)
    }
    
    // OTA settings
    override suspend fun getOtaEnabled(): Boolean {
        return settingsDataStore.getBoolean(PreferencesKeys.OTA_ENABLED, false).first()
    }
    
    override suspend fun setOtaEnabled(enabled: Boolean) {
        settingsDataStore.setBoolean(PreferencesKeys.OTA_ENABLED, enabled)
    }
    
    override suspend fun getOtaServerUrl(): String {
        return settingsDataStore.getString(PreferencesKeys.OTA_SERVER_URL, "").first()
    }
    
    override suspend fun setOtaServerUrl(url: String) {
        settingsDataStore.setString(PreferencesKeys.OTA_SERVER_URL, url)
    }
    
    override suspend fun getOtaHttpUsername(): String {
        return settingsDataStore.getString(PreferencesKeys.OTA_HTTP_USERNAME, "").first()
    }
    
    override suspend fun setOtaHttpUsername(username: String) {
        settingsDataStore.setString(PreferencesKeys.OTA_HTTP_USERNAME, username)
    }
    
    override suspend fun getOtaHttpPassword(): String {
        return settingsDataStore.getString(PreferencesKeys.OTA_HTTP_PASSWORD, "").first()
    }
    
    override suspend fun setOtaHttpPassword(password: String) {
        settingsDataStore.setString(PreferencesKeys.OTA_HTTP_PASSWORD, password)
    }
    
    // Sensor configuration
    override suspend fun getWcs6800Sensitivity(): Float {
        return settingsDataStore.getFloat(PreferencesKeys.WCS6800_SENSITIVITY, 0.0429f).first()
    }
    
    override suspend fun setWcs6800Sensitivity(sensitivity: Float) {
        settingsDataStore.setFloat(PreferencesKeys.WCS6800_SENSITIVITY, sensitivity)
    }
    
    override suspend fun getWcs6800OffsetVoltage(): Float {
        return settingsDataStore.getFloat(PreferencesKeys.WCS6800_OFFSET_VOLTAGE, 1.65f).first()
    }
    
    override suspend fun setWcs6800OffsetVoltage(offsetVoltage: Float) {
        settingsDataStore.setFloat(PreferencesKeys.WCS6800_OFFSET_VOLTAGE, offsetVoltage)
    }
    
    override suspend fun getAdcRefVoltage(): Float {
        return settingsDataStore.getFloat(PreferencesKeys.ADC_REF_VOLTAGE, 3.3f).first()
    }
    
    override suspend fun setAdcRefVoltage(refVoltage: Float) {
        settingsDataStore.setFloat(PreferencesKeys.ADC_REF_VOLTAGE, refVoltage)
    }
    
    // Advanced settings
    override suspend fun getDebugLevel(): Int {
        return settingsDataStore.getInt(PreferencesKeys.DEBUG_LEVEL, 2).first()
    }
    
    override suspend fun setDebugLevel(level: Int) {
        settingsDataStore.setInt(PreferencesKeys.DEBUG_LEVEL, level)
    }
    
    override suspend fun getUseAdaptiveSamplingRate(): Boolean {
        return settingsDataStore.getBoolean(PreferencesKeys.USE_ADAPTIVE_SAMPLING_RATE, false).first()
    }
    
    override suspend fun setUseAdaptiveSamplingRate(useAdaptive: Boolean) {
        settingsDataStore.setBoolean(PreferencesKeys.USE_ADAPTIVE_SAMPLING_RATE, useAdaptive)
    }
    
    override suspend fun getMeasurementInterval(): Int {
        return settingsDataStore.getInt(PreferencesKeys.MEASUREMENT_INTERVAL, 1000).first()
    }
    
    override suspend fun setMeasurementInterval(interval: Int) {
        settingsDataStore.setInt(PreferencesKeys.MEASUREMENT_INTERVAL, interval)
    }
    
    override suspend fun getDataFormat(): String {
        return settingsDataStore.getString(PreferencesKeys.DATA_FORMAT, "JSON").first()
    }
    
    override suspend fun setDataFormat(format: String) {
        settingsDataStore.setString(PreferencesKeys.DATA_FORMAT, format)
    }
    
    // App preferences
    override suspend fun getRefreshInterval(): Int {
        return settingsDataStore.getInt(PreferencesKeys.REFRESH_INTERVAL, 30).first()
    }
    
    override suspend fun setRefreshInterval(intervalSeconds: Int) {
        settingsDataStore.setInt(PreferencesKeys.REFRESH_INTERVAL, intervalSeconds)
    }
    
    override fun getRefreshIntervalFlow(): Flow<Int> {
        return settingsDataStore.getInt(PreferencesKeys.REFRESH_INTERVAL, 30)
    }
    
    override suspend fun getDataRetentionPeriod(): Int {
        return settingsDataStore.getInt(PreferencesKeys.DATA_RETENTION_PERIOD, 30).first()
    }
    
    override suspend fun setDataRetentionPeriod(periodDays: Int) {
        settingsDataStore.setInt(PreferencesKeys.DATA_RETENTION_PERIOD, periodDays)
    }
    
    override suspend fun getThemeMode(): Int {
        return settingsDataStore.getInt(PreferencesKeys.THEME_MODE, 0).first()
    }
    
    override suspend fun setThemeMode(themeMode: Int) {
        settingsDataStore.setInt(PreferencesKeys.THEME_MODE, themeMode)
    }
    
    override suspend fun getNotificationsEnabled(): Boolean {
        return settingsDataStore.getBoolean(PreferencesKeys.NOTIFICATIONS_ENABLED, true).first()
    }
    
    override suspend fun setNotificationsEnabled(enabled: Boolean) {
        settingsDataStore.setBoolean(PreferencesKeys.NOTIFICATIONS_ENABLED, enabled)
    }
    
    // Device settings
    override suspend fun getDeviceName(): String {
        return settingsDataStore.getString(PreferencesKeys.DEVICE_NAME, "Current Sensor").first()
    }
    
    override suspend fun setDeviceName(name: String) {
        settingsDataStore.setString(PreferencesKeys.DEVICE_NAME, name)
    }
    
    override suspend fun getAlertThreshold(): Double {
        return settingsDataStore.getDouble(PreferencesKeys.ALERT_THRESHOLD, 20.0).first()
    }
    
    override suspend fun setAlertThreshold(threshold: Double) {
        settingsDataStore.setDouble(PreferencesKeys.ALERT_THRESHOLD, threshold)
    }
} 