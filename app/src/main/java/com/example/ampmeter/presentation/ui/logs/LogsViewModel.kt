package com.example.ampmeter.presentation.ui.logs

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ampmeter.domain.model.DeviceReading
import com.example.ampmeter.domain.model.Resource
import com.example.ampmeter.domain.usecase.device.GetDeviceReadingsUseCase
import com.example.ampmeter.domain.usecase.settings.GetSettingsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class LogsViewModel @Inject constructor(
    private val getDeviceReadingsUseCase: GetDeviceReadingsUseCase,
    private val getSettingsUseCase: GetSettingsUseCase
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(LogsUiState())
    val uiState: StateFlow<LogsUiState> = _uiState.asStateFlow()
    
    private var currentPage = 0
    private val pageSize = 20
    private var isLastPage = false
    
    init {
        loadDeviceId()
    }
    
    private fun loadDeviceId() {
        viewModelScope.launch {
            try {
                val deviceId = getSettingsUseCase.getDeviceId()
                _uiState.update { it.copy(deviceId = deviceId) }
                
                if (deviceId.isNotBlank()) {
                    loadReadings(refresh = true)
                }
            } catch (e: Exception) {
                Timber.e(e, "Error loading device ID")
                _uiState.update { 
                    it.copy(error = "Error loading device ID: ${e.message}")
                }
            }
        }
    }
    
    fun loadReadings(refresh: Boolean = false) {
        viewModelScope.launch {
            val deviceId = _uiState.value.deviceId
            
            if (deviceId.isBlank()) {
                _uiState.update { 
                    it.copy(error = "Device ID not set. Please configure in settings.")
                }
                return@launch
            }
            
            if (refresh) {
                currentPage = 0
                isLastPage = false
                _uiState.update { 
                    it.copy(
                        isLoading = true,
                        readings = emptyList()
                    )
                }
            } else if (_uiState.value.isLoadingMore || isLastPage) {
                // Don't load more if already loading or reached the end
                return@launch
            } else {
                _uiState.update { it.copy(isLoadingMore = true) }
            }
            
            try {
                val result = getDeviceReadingsUseCase(
                    deviceId = deviceId,
                    limit = pageSize,
                    offset = currentPage * pageSize
                )
                
                when (result) {
                    is Resource.Success -> {
                        val newReadings = result.data
                        isLastPage = newReadings.size < pageSize
                        
                        if (refresh) {
                            _uiState.update { 
                                it.copy(
                                    isLoading = false,
                                    isLoadingMore = false,
                                    readings = newReadings,
                                    error = null
                                )
                            }
                        } else {
                            val updatedReadings = _uiState.value.readings + newReadings
                            _uiState.update { 
                                it.copy(
                                    isLoadingMore = false,
                                    readings = updatedReadings,
                                    error = null
                                )
                            }
                        }
                        
                        if (newReadings.isNotEmpty()) {
                            currentPage++
                        }
                    }
                    is Resource.Error -> {
                        _uiState.update { 
                            it.copy(
                                isLoading = false,
                                isLoadingMore = false,
                                error = result.message
                            )
                        }
                    }
                    is Resource.Loading -> {
                        // Already handled above
                    }
                }
            } catch (e: Exception) {
                Timber.e(e, "Error loading readings")
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        isLoadingMore = false,
                        error = "Error loading readings: ${e.message}"
                    )
                }
            }
        }
    }
    
    fun loadMore() {
        if (!_uiState.value.isLoading && !_uiState.value.isLoadingMore && !isLastPage) {
            loadReadings(refresh = false)
        }
    }
    
    fun refresh() {
        loadReadings(refresh = true)
    }
    
    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}

data class LogsUiState(
    val deviceId: String = "",
    val readings: List<DeviceReading> = emptyList(),
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val error: String? = null
) 