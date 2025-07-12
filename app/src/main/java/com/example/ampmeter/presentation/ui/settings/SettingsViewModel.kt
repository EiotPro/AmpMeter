package com.example.ampmeter.presentation.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ampmeter.domain.model.Resource
import com.example.ampmeter.domain.usecase.settings.GetSettingsUseCase
import com.example.ampmeter.domain.usecase.settings.UpdateSettingsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val getSettingsUseCase: GetSettingsUseCase,
    private val updateSettingsUseCase: UpdateSettingsUseCase
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()
    
    init {
        loadSettings()
    }
    
    private fun loadSettings() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            try {
                // Load device settings
                val deviceId = getSettingsUseCase.getDeviceId()
                val deviceName = getSettingsUseCase.getDeviceName()
                
                // Load ChirpStack settings
                val serverUrl = getSettingsUseCase.getChirpStackServerUrl()
                val apiKey = getSettingsUseCase.getChirpStackApiKey()
                
                // Load app settings
                val refreshInterval = getSettingsUseCase.getRefreshInterval()
                val notificationsEnabled = getSettingsUseCase.getNotificationsEnabled()
                
                _uiState.update {
                    it.copy(
                        deviceId = deviceId,
                        deviceName = deviceName,
                        serverUrl = serverUrl,
                        apiKey = apiKey,
                        refreshInterval = refreshInterval,
                        notificationsEnabled = notificationsEnabled,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                Timber.e(e, "Error loading settings")
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Error loading settings: ${e.message}"
                    )
                }
            }
        }
    }
    
    fun saveSettings(
        deviceId: String,
        deviceName: String,
        serverUrl: String,
        apiKey: String,
        refreshInterval: Int,
        notificationsEnabled: Boolean
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            
            try {
                // Save device settings
                updateSettingsUseCase.setDeviceId(deviceId)
                updateSettingsUseCase.setDeviceName(deviceName)
                
                // Save ChirpStack settings
                updateSettingsUseCase.setChirpStackServerUrl(serverUrl)
                updateSettingsUseCase.setChirpStackApiKey(apiKey)
                
                // Save app settings
                updateSettingsUseCase.setRefreshInterval(refreshInterval)
                updateSettingsUseCase.setNotificationsEnabled(notificationsEnabled)
                
                _uiState.update {
                    it.copy(
                        deviceId = deviceId,
                        deviceName = deviceName,
                        serverUrl = serverUrl,
                        apiKey = apiKey,
                        refreshInterval = refreshInterval,
                        notificationsEnabled = notificationsEnabled,
                        isSaving = false,
                        saveSuccess = true
                    )
                }
            } catch (e: Exception) {
                Timber.e(e, "Error saving settings")
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        error = "Error saving settings: ${e.message}"
                    )
                }
            }
        }
    }
    
    fun clearSaveSuccess() {
        _uiState.update { it.copy(saveSuccess = false) }
    }
    
    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
    
    fun testConnection(): Resource<Unit> {
        // This would be implemented with actual connection testing logic
        // For now, just return success
        return Resource.Success(Unit)
    }
} 