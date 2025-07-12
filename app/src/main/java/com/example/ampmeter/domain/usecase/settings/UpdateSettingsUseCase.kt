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
} 