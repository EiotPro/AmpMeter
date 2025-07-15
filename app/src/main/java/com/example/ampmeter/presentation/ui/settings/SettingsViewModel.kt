package com.example.ampmeter.presentation.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ampmeter.data.remote.api.ChirpStackApi
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
import java.net.UnknownHostException
import java.net.ConnectException
import okhttp3.MediaType
import retrofit2.HttpException
import java.net.SocketTimeoutException

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val getSettingsUseCase: GetSettingsUseCase,
    private val updateSettingsUseCase: UpdateSettingsUseCase,
    private val chirpStackApi: ChirpStackApi
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
    
    fun testConnection() {
        viewModelScope.launch {
            val deviceId = _uiState.value.deviceId
            val serverUrl = _uiState.value.serverUrl
            
            if (deviceId.isBlank()) {
                _uiState.update { 
                    it.copy(
                        isTestingConnection = false,
                        connectionTestResult = "Device ID cannot be empty"
                    )
                }
                return@launch
            }
            
            _uiState.update { it.copy(isTestingConnection = true) }
            
            try {
                // Try to get device info from ChirpStack
                val response = chirpStackApi.getDeviceInfo(deviceId)
                
                if (response.isSuccessful && response.body() != null) {
                    _uiState.update { 
                        it.copy(
                            isTestingConnection = false,
                            connectionTestResult = "Connection successful! Device found: ${response.body()?.device?.name ?: deviceId}"
                        )
                    }
                } else {
                    val errorBody = response.errorBody()?.string() ?: ""
                    val errorMessage = when {
                        errorBody.contains("<!doctype html>") || errorBody.contains("<html>") -> 
                            "Server returned HTML instead of JSON. Check that the URL format is correct (IP:port)."
                        response.code() == 401 -> 
                            "Authentication failed. Check your API key."
                        response.code() == 404 -> 
                            "Device not found or API endpoint incorrect."
                        response.code() >= 500 -> 
                            "Server error. Please try again later."
                        else -> 
                            "Error: ${errorBody.take(100)}"
                    }
                    
                    _uiState.update { 
                        it.copy(
                            isTestingConnection = false,
                            connectionTestResult = errorMessage
                        )
                    }
                }
            } catch (e: Exception) {
                Timber.e(e, "Error testing connection")
                
                val errorMessage = when (e) {
                    is UnknownHostException -> 
                        "Could not resolve host. Check server address and internet connection."
                    is ConnectException -> 
                        "Connection failed. Server may be down or unreachable."
                    is SocketTimeoutException -> 
                        "Connection timed out. Server may be slow or unreachable."
                    is HttpException -> {
                        if (e.response()?.errorBody()?.contentType()?.toString()?.contains("text/html") == true) {
                            "Server returned HTML instead of JSON. Check that server URL includes http:// prefix."
                        } else {
                            "HTTP error: ${e.code()}"
                        }
                    }
                    is IllegalArgumentException -> {
                        if (e.message?.contains("URL") == true || e.message?.contains("host") == true) {
                            "Invalid URL format. Make sure server URL is in format 'host:port' or 'http://host:port'"
                        } else {
                            "Invalid parameters: ${e.message}"
                        }
                    }
                    else -> e.message ?: "Unknown error"
                }
                
                _uiState.update { 
                    it.copy(
                        isTestingConnection = false,
                        connectionTestResult = "Connection failed: $errorMessage"
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
    
    fun clearConnectionTestResult() {
        _uiState.update { it.copy(connectionTestResult = null) }
    }
} 