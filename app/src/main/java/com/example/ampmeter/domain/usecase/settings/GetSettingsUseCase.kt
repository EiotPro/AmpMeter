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
} 