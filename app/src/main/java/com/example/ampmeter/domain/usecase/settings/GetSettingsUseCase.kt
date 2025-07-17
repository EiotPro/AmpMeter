package com.example.ampmeter.domain.usecase.settings

import com.example.ampmeter.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for getting app settings.
 */
class GetSettingsUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository
) {
    /**
     * Gets the ChirpStack server URL.
     */
    suspend fun getChirpStackServerUrl(): String {
        return settingsRepository.getChirpStackServerUrl()
    }
    
    /**
     * Gets the ChirpStack API key.
     */
    suspend fun getChirpStackApiKey(): String {
        return settingsRepository.getChirpStackApiKey()
    }
    
    /**
     * Gets the device ID.
     */
    suspend fun getDeviceId(): String {
        return settingsRepository.getDeviceId()
    }
    
    /**
     * Gets the refresh interval in seconds.
     */
    suspend fun getRefreshInterval(): Int {
        return settingsRepository.getRefreshInterval()
    }
    
    /**
     * Gets a flow of the refresh interval.
     */
    fun getRefreshIntervalFlow(): Flow<Int> {
        return settingsRepository.getRefreshIntervalFlow()
    }
    
    /**
     * Gets the device name.
     */
    suspend fun getDeviceName(): String {
        return settingsRepository.getDeviceName()
    }
    
    /**
     * Gets whether notifications are enabled.
     */
    suspend fun getNotificationsEnabled(): Boolean {
        return settingsRepository.getNotificationsEnabled()
    }
    
    /**
     * Gets the alert threshold for current values.
     */
    suspend fun getAlertThreshold(): Double {
        return settingsRepository.getAlertThreshold()
    }
    
    /**
     * Gets the MQTT broker URL.
     */
    suspend fun getMqttBrokerUrl(): String {
        return settingsRepository.getMqttBrokerUrl()
    }
    
    /**
     * Gets the MQTT port.
     */
    suspend fun getMqttPort(): Int {
        return settingsRepository.getMqttPort()
    }
    
    /**
     * Gets whether MQTT is enabled.
     */
    suspend fun getMqttEnabled(): Boolean {
        return settingsRepository.getMqttEnabled()
    }
    
    /**
     * Gets the MQTT username.
     */
    suspend fun getMqttUsername(): String? {
        return settingsRepository.getMqttUsername()
    }
    
    /**
     * Gets the MQTT password.
     */
    suspend fun getMqttPassword(): String? {
        return settingsRepository.getMqttPassword()
    }
    
    /**
     * Gets the MQTT topic.
     */
    suspend fun getMqttTopic(): String {
        return settingsRepository.getMqttTopic()
    }
    
    /**
     * Gets whether WiFi is enabled.
     */
    suspend fun getWifiEnabled(): Boolean {
        return settingsRepository.getWifiEnabled()
    }
    
    /**
     * Gets the WiFi SSID.
     */
    suspend fun getWifiSsid(): String {
        return settingsRepository.getWifiSsid()
    }
    
    /**
     * Gets the WiFi password.
     */
    suspend fun getWifiPassword(): String {
        return settingsRepository.getWifiPassword()
    }
    
    /**
     * Gets the WiFi transmission interval in milliseconds.
     */
    suspend fun getWifiTxInterval(): Int {
        return settingsRepository.getWifiTxInterval()
    }
    
    /**
     * Gets whether BLE is enabled.
     */
    suspend fun getBleEnabled(): Boolean {
        return settingsRepository.getBleEnabled()
    }
    
    /**
     * Gets the BLE device name.
     */
    suspend fun getBleDeviceName(): String {
        return settingsRepository.getBleDeviceName()
    }
    
    /**
     * Gets the BLE transmission interval in milliseconds.
     */
    suspend fun getBleTxInterval(): Int {
        return settingsRepository.getBleTxInterval()
    }
    
    /**
     * Gets the LoRaWAN device address.
     */
    suspend fun getLorawanDevAddr(): String {
        return settingsRepository.getLorawanDevAddr()
    }
    
    /**
     * Gets the LoRaWAN network session key.
     */
    suspend fun getLorawanNwksKey(): String {
        return settingsRepository.getLorawanNwksKey()
    }
    
    /**
     * Gets the LoRaWAN application session key.
     */
    suspend fun getLorawanAppsKey(): String {
        return settingsRepository.getLorawanAppsKey()
    }
    
    /**
     * Gets the LoRaWAN region.
     */
    suspend fun getLorawanRegion(): Int {
        return settingsRepository.getLorawanRegion()
    }
    
    /**
     * Gets the transmission interval in milliseconds.
     */
    suspend fun getTxInterval(): Int {
        return settingsRepository.getTxInterval()
    }
    
    /**
     * Gets whether OTA updates are enabled.
     */
    suspend fun getOtaEnabled(): Boolean {
        return settingsRepository.getOtaEnabled()
    }
    
    /**
     * Gets the OTA server URL.
     */
    suspend fun getOtaServerUrl(): String {
        return settingsRepository.getOtaServerUrl()
    }
    
    /**
     * Gets the OTA HTTP username.
     */
    suspend fun getOtaHttpUsername(): String {
        return settingsRepository.getOtaHttpUsername()
    }
    
    /**
     * Gets the OTA HTTP password.
     */
    suspend fun getOtaHttpPassword(): String {
        return settingsRepository.getOtaHttpPassword()
    }
    
    /**
     * Gets the WCS6800 sensitivity in V/A.
     */
    suspend fun getWcs6800Sensitivity(): Float {
        return settingsRepository.getWcs6800Sensitivity()
    }
    
    /**
     * Gets the WCS6800 offset voltage in volts.
     */
    suspend fun getWcs6800OffsetVoltage(): Float {
        return settingsRepository.getWcs6800OffsetVoltage()
    }
    
    /**
     * Gets the ADC reference voltage in volts.
     */
    suspend fun getAdcRefVoltage(): Float {
        return settingsRepository.getAdcRefVoltage()
    }
    
    /**
     * Gets the debug level.
     */
    suspend fun getDebugLevel(): Int {
        return settingsRepository.getDebugLevel()
    }
    
    /**
     * Gets whether to use adaptive sampling rate.
     */
    suspend fun getUseAdaptiveSamplingRate(): Boolean {
        return settingsRepository.getUseAdaptiveSamplingRate()
    }
    
    /**
     * Gets the measurement interval in milliseconds.
     */
    suspend fun getMeasurementInterval(): Int {
        return settingsRepository.getMeasurementInterval()
    }
    
    /**
     * Gets the data format (JSON or COMPACT).
     */
    suspend fun getDataFormat(): String {
        return settingsRepository.getDataFormat()
    }
} 