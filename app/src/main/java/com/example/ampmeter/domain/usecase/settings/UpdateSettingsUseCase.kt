package com.example.ampmeter.domain.usecase.settings

import com.example.ampmeter.domain.repository.SettingsRepository
import javax.inject.Inject

/**
 * Use case for updating app settings.
 */
class UpdateSettingsUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository
) {
    /**
     * Sets the ChirpStack server URL.
     */
    suspend fun setChirpStackServerUrl(url: String) {
        settingsRepository.setChirpStackServerUrl(url)
    }
    
    /**
     * Sets the ChirpStack API key.
     */
    suspend fun setChirpStackApiKey(apiKey: String) {
        settingsRepository.setChirpStackApiKey(apiKey)
    }
    
    /**
     * Sets the device ID.
     */
    suspend fun setDeviceId(deviceId: String) {
        settingsRepository.setDeviceId(deviceId)
    }
    
    /**
     * Sets the application ID.
     */
    suspend fun setApplicationId(applicationId: String) {
        settingsRepository.setApplicationId(applicationId)
    }
    
    /**
     * Sets the refresh interval in seconds.
     */
    suspend fun setRefreshInterval(intervalSeconds: Int) {
        settingsRepository.setRefreshInterval(intervalSeconds)
    }
    
    /**
     * Sets the device name.
     */
    suspend fun setDeviceName(name: String) {
        settingsRepository.setDeviceName(name)
    }
    
    /**
     * Sets whether notifications are enabled.
     */
    suspend fun setNotificationsEnabled(enabled: Boolean) {
        settingsRepository.setNotificationsEnabled(enabled)
    }
    
    /**
     * Sets the alert threshold.
     */
    suspend fun setAlertThreshold(threshold: Double) {
        settingsRepository.setAlertThreshold(threshold)
    }
    
    /**
     * Sets the MQTT broker URL.
     */
    suspend fun setMqttBrokerUrl(url: String) {
        settingsRepository.setMqttBrokerUrl(url)
    }

    /**
     * Sets the MQTT port.
     */
    suspend fun setMqttPort(port: Int) {
        settingsRepository.setMqttPort(port)
    }
    
    /**
     * Sets whether MQTT is enabled.
     */
    suspend fun setMqttEnabled(enabled: Boolean) {
        settingsRepository.setMqttEnabled(enabled)
    }
    
    /**
     * Sets the MQTT username.
     */
    suspend fun setMqttUsername(username: String?) {
        settingsRepository.setMqttUsername(username)
    }
    
    /**
     * Sets the MQTT password.
     */
    suspend fun setMqttPassword(password: String?) {
        settingsRepository.setMqttPassword(password)
    }
    
    /**
     * Sets the MQTT topic.
     */
    suspend fun setMqttTopic(topic: String) {
        settingsRepository.setMqttTopic(topic)
    }
    
    /**
     * Sets whether WiFi is enabled.
     */
    suspend fun setWifiEnabled(enabled: Boolean) {
        settingsRepository.setWifiEnabled(enabled)
    }
    
    /**
     * Sets the WiFi SSID.
     */
    suspend fun setWifiSsid(ssid: String) {
        settingsRepository.setWifiSsid(ssid)
    }
    
    /**
     * Sets the WiFi password.
     */
    suspend fun setWifiPassword(password: String) {
        settingsRepository.setWifiPassword(password)
    }
    
    /**
     * Sets the WiFi transmission interval in milliseconds.
     */
    suspend fun setWifiTxInterval(interval: Int) {
        settingsRepository.setWifiTxInterval(interval)
    }
    
    /**
     * Sets whether BLE is enabled.
     */
    suspend fun setBleEnabled(enabled: Boolean) {
        settingsRepository.setBleEnabled(enabled)
    }
    
    /**
     * Sets the BLE device name.
     */
    suspend fun setBleDeviceName(name: String) {
        settingsRepository.setBleDeviceName(name)
    }
    
    /**
     * Sets the BLE transmission interval in milliseconds.
     */
    suspend fun setBleTxInterval(interval: Int) {
        settingsRepository.setBleTxInterval(interval)
    }
    
    /**
     * Sets the LoRaWAN device address.
     */
    suspend fun setLorawanDevAddr(devAddr: String) {
        settingsRepository.setLorawanDevAddr(devAddr)
    }
    
    /**
     * Sets the LoRaWAN network session key.
     */
    suspend fun setLorawanNwksKey(nwksKey: String) {
        settingsRepository.setLorawanNwksKey(nwksKey)
    }
    
    /**
     * Sets the LoRaWAN application session key.
     */
    suspend fun setLorawanAppsKey(appsKey: String) {
        settingsRepository.setLorawanAppsKey(appsKey)
    }
    
    /**
     * Sets the LoRaWAN region.
     */
    suspend fun setLorawanRegion(region: Int) {
        settingsRepository.setLorawanRegion(region)
    }
    
    /**
     * Sets the transmission interval in milliseconds.
     */
    suspend fun setTxInterval(interval: Int) {
        settingsRepository.setTxInterval(interval)
    }
    
    /**
     * Sets whether OTA updates are enabled.
     */
    suspend fun setOtaEnabled(enabled: Boolean) {
        settingsRepository.setOtaEnabled(enabled)
    }
    
    /**
     * Sets the OTA server URL.
     */
    suspend fun setOtaServerUrl(url: String) {
        settingsRepository.setOtaServerUrl(url)
    }
    
    /**
     * Sets the OTA HTTP username.
     */
    suspend fun setOtaHttpUsername(username: String) {
        settingsRepository.setOtaHttpUsername(username)
    }
    
    /**
     * Sets the OTA HTTP password.
     */
    suspend fun setOtaHttpPassword(password: String) {
        settingsRepository.setOtaHttpPassword(password)
    }
    
    /**
     * Sets the WCS6800 sensitivity in V/A.
     */
    suspend fun setWcs6800Sensitivity(sensitivity: Float) {
        settingsRepository.setWcs6800Sensitivity(sensitivity)
    }
    
    /**
     * Sets the WCS6800 offset voltage in volts.
     */
    suspend fun setWcs6800OffsetVoltage(offsetVoltage: Float) {
        settingsRepository.setWcs6800OffsetVoltage(offsetVoltage)
    }
    
    /**
     * Sets the ADC reference voltage in volts.
     */
    suspend fun setAdcRefVoltage(refVoltage: Float) {
        settingsRepository.setAdcRefVoltage(refVoltage)
    }
    
    /**
     * Sets the debug level.
     */
    suspend fun setDebugLevel(level: Int) {
        settingsRepository.setDebugLevel(level)
    }
    
    /**
     * Sets whether to use adaptive sampling rate.
     */
    suspend fun setUseAdaptiveSamplingRate(useAdaptive: Boolean) {
        settingsRepository.setUseAdaptiveSamplingRate(useAdaptive)
    }
    
    /**
     * Sets the measurement interval in milliseconds.
     */
    suspend fun setMeasurementInterval(interval: Int) {
        settingsRepository.setMeasurementInterval(interval)
    }
    
    /**
     * Sets the data format (JSON or COMPACT).
     */
    suspend fun setDataFormat(format: String) {
        settingsRepository.setDataFormat(format)
    }
} 