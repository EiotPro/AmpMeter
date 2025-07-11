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
     * Updates the ChirpStack server URL.
     */
    suspend fun updateChirpStackServerUrl(url: String) {
        settingsRepository.setChirpStackServerUrl(url)
    }
    
    /**
     * Updates the ChirpStack API key.
     */
    suspend fun updateChirpStackApiKey(apiKey: String) {
        settingsRepository.setChirpStackApiKey(apiKey)
    }
    
    /**
     * Updates the device ID.
     */
    suspend fun updateDeviceId(deviceId: String) {
        settingsRepository.setDeviceId(deviceId)
    }
    
    /**
     * Updates the refresh interval.
     */
    suspend fun updateRefreshInterval(intervalSeconds: Int) {
        settingsRepository.setRefreshInterval(intervalSeconds)
    }
    
    /**
     * Updates the device name.
     */
    suspend fun updateDeviceName(name: String) {
        settingsRepository.setDeviceName(name)
    }
    
    /**
     * Updates the Supabase URL.
     */
    suspend fun updateSupabaseUrl(url: String) {
        settingsRepository.setSupabaseUrl(url)
    }
    
    /**
     * Updates the Supabase API key.
     */
    suspend fun updateSupabaseApiKey(apiKey: String) {
        settingsRepository.setSupabaseApiKey(apiKey)
    }
    
    /**
     * Updates the alert threshold.
     */
    suspend fun updateAlertThreshold(threshold: Double) {
        settingsRepository.setAlertThreshold(threshold)
    }
} 