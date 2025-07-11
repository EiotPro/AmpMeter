package com.example.ampmeter.presentation.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ampmeter.domain.model.ConnectionStatus
import com.example.ampmeter.domain.model.DeviceReading
import com.example.ampmeter.domain.model.Resource
import com.example.ampmeter.domain.usecase.device.GetLatestReadingUseCase
import com.example.ampmeter.domain.usecase.settings.GetSettingsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

/**
 * ViewModel for the Dashboard screen.
 */
@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val getLatestReadingUseCase: GetLatestReadingUseCase,
    private val getSettingsUseCase: GetSettingsUseCase
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()
    
    private var refreshJob: Job? = null
    
    init {
        loadDeviceName()
        observeRefreshInterval()
        refreshData()
    }
    
    /**
     * Refreshes the device data.
     */
    fun refreshData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            try {
                val deviceId = getSettingsUseCase.getDeviceId()
                if (deviceId.isBlank()) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = "Device ID not set. Please configure in settings."
                        )
                    }
                    return@launch
                }
                
                when (val result = getLatestReadingUseCase(deviceId)) {
                    is Resource.Success -> {
                        val reading = result.data
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                deviceReading = reading,
                                connectionStatus = determineConnectionStatus(reading),
                                lastUpdated = System.currentTimeMillis(),
                                error = null
                            )
                        }
                    }
                    is Resource.Error -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                error = result.message
                            )
                        }
                    }
                    is Resource.Loading -> {
                        // Already set loading state above
                    }
                }
            } catch (e: Exception) {
                Timber.e(e, "Error refreshing data")
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Error: ${e.message}"
                    )
                }
            }
        }
    }
    
    /**
     * Loads the device name from settings.
     */
    private fun loadDeviceName() {
        viewModelScope.launch {
            try {
                val deviceName = getSettingsUseCase.getDeviceName()
                _uiState.update { it.copy(deviceName = deviceName) }
            } catch (e: Exception) {
                Timber.e(e, "Error loading device name")
            }
        }
    }
    
    /**
     * Observes changes to the refresh interval setting.
     */
    private fun observeRefreshInterval() {
        viewModelScope.launch {
            getSettingsUseCase.getRefreshIntervalFlow().collectLatest { interval ->
                refreshJob?.cancel()
                refreshJob = startPeriodicRefresh(interval)
            }
        }
    }
    
    /**
     * Starts periodic refresh based on the interval.
     */
    private fun startPeriodicRefresh(intervalSeconds: Int): Job {
        return viewModelScope.launch {
            while (isActive) {
                delay(intervalSeconds * 1000L)
                refreshData()
            }
        }
    }
    
    /**
     * Determines the connection status based on the reading.
     */
    private fun determineConnectionStatus(reading: DeviceReading?): ConnectionStatus {
        return ConnectionStatus.fromTimestamp(reading?.timestamp)
    }
} 