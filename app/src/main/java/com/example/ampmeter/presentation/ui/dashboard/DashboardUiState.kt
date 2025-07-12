package com.example.ampmeter.presentation.ui.dashboard

import com.example.ampmeter.domain.model.ConnectionStatus
import com.example.ampmeter.domain.model.DeviceReading

/**
 * UI state for the Dashboard screen.
 */
data class DashboardUiState(
    val isLoading: Boolean = false,
    val deviceReading: DeviceReading? = null,
    val connectionStatus: ConnectionStatus = ConnectionStatus.UNKNOWN,
    val lastUpdated: Long = 0,
    val error: String? = null,
    val alert: String? = null,
    val deviceName: String = "Current Sensor"
) 