package com.example.ampmeter.presentation.ui.settings

/**
 * UI state for the Settings screen.
 */
data class SettingsUiState(
    val deviceId: String = "",
    val deviceName: String = "",
    val serverUrl: String = "",
    val apiKey: String = "",
    val refreshInterval: Int = 30,
    val notificationsEnabled: Boolean = true,
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val saveSuccess: Boolean = false,
    val error: String? = null
) 