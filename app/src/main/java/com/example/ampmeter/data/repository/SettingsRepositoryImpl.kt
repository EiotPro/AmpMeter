package com.example.ampmeter.data.repository

import com.example.ampmeter.data.local.datastore.PreferencesKeys
import com.example.ampmeter.data.local.datastore.SettingsDataStore
import com.example.ampmeter.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of SettingsRepository using DataStore.
 */
@Singleton
class SettingsRepositoryImpl @Inject constructor(
    private val settingsDataStore: SettingsDataStore
) : SettingsRepository {
    
    // ChirpStack settings
    override suspend fun getChirpStackServerUrl(): String {
        return settingsDataStore.getString(PreferencesKeys.CHIRP_STACK_SERVER_URL).first()
    }
    
    override suspend fun setChirpStackServerUrl(url: String) {
        settingsDataStore.setString(PreferencesKeys.CHIRP_STACK_SERVER_URL, url)
    }
    
    override suspend fun getChirpStackApiKey(): String {
        return settingsDataStore.getString(PreferencesKeys.CHIRP_STACK_API_KEY).first()
    }
    
    override suspend fun setChirpStackApiKey(apiKey: String) {
        settingsDataStore.setString(PreferencesKeys.CHIRP_STACK_API_KEY, apiKey)
    }
    
    override suspend fun getDeviceId(): String {
        return settingsDataStore.getString(PreferencesKeys.DEVICE_ID).first()
    }
    
    override suspend fun setDeviceId(deviceId: String) {
        settingsDataStore.setString(PreferencesKeys.DEVICE_ID, deviceId)
    }
    
    override suspend fun getApplicationId(): String {
        return settingsDataStore.getString(PreferencesKeys.APPLICATION_ID).first()
    }
    
    override suspend fun setApplicationId(applicationId: String) {
        settingsDataStore.setString(PreferencesKeys.APPLICATION_ID, applicationId)
    }
    
    // Supabase settings
    override suspend fun getSupabaseUrl(): String {
        return settingsDataStore.getString(PreferencesKeys.SUPABASE_URL).first()
    }
    
    override suspend fun setSupabaseUrl(url: String) {
        settingsDataStore.setString(PreferencesKeys.SUPABASE_URL, url)
    }
    
    override suspend fun getSupabaseApiKey(): String {
        return settingsDataStore.getString(PreferencesKeys.SUPABASE_API_KEY).first()
    }
    
    override suspend fun setSupabaseApiKey(apiKey: String) {
        settingsDataStore.setString(PreferencesKeys.SUPABASE_API_KEY, apiKey)
    }
    
    override suspend fun getSupabaseTableName(): String {
        return settingsDataStore.getString(PreferencesKeys.SUPABASE_TABLE_NAME, "device_logs").first()
    }
    
    override suspend fun setSupabaseTableName(tableName: String) {
        settingsDataStore.setString(PreferencesKeys.SUPABASE_TABLE_NAME, tableName)
    }
    
    // App preferences
    override suspend fun getRefreshInterval(): Int {
        return settingsDataStore.getInt(PreferencesKeys.REFRESH_INTERVAL, 30).first()
    }
    
    override suspend fun setRefreshInterval(intervalSeconds: Int) {
        settingsDataStore.setInt(PreferencesKeys.REFRESH_INTERVAL, intervalSeconds)
    }
    
    override fun getRefreshIntervalFlow(): Flow<Int> {
        return settingsDataStore.getInt(PreferencesKeys.REFRESH_INTERVAL, 30)
    }
    
    override suspend fun getDataRetentionPeriod(): Int {
        return settingsDataStore.getInt(PreferencesKeys.DATA_RETENTION_PERIOD, 30).first()
    }
    
    override suspend fun setDataRetentionPeriod(periodDays: Int) {
        settingsDataStore.setInt(PreferencesKeys.DATA_RETENTION_PERIOD, periodDays)
    }
    
    override suspend fun getThemeMode(): Int {
        return settingsDataStore.getInt(PreferencesKeys.THEME_MODE, 0).first()
    }
    
    override suspend fun setThemeMode(themeMode: Int) {
        settingsDataStore.setInt(PreferencesKeys.THEME_MODE, themeMode)
    }
    
    override suspend fun getNotificationsEnabled(): Boolean {
        return settingsDataStore.getBoolean(PreferencesKeys.NOTIFICATIONS_ENABLED, true).first()
    }
    
    override suspend fun setNotificationsEnabled(enabled: Boolean) {
        settingsDataStore.setBoolean(PreferencesKeys.NOTIFICATIONS_ENABLED, enabled)
    }
    
    // Device settings
    override suspend fun getDeviceName(): String {
        return settingsDataStore.getString(PreferencesKeys.DEVICE_NAME, "Current Sensor").first()
    }
    
    override suspend fun setDeviceName(name: String) {
        settingsDataStore.setString(PreferencesKeys.DEVICE_NAME, name)
    }
    
    override suspend fun getAlertThreshold(): Double {
        return settingsDataStore.getDouble(PreferencesKeys.ALERT_THRESHOLD, 20.0).first()
    }
    
    override suspend fun setAlertThreshold(threshold: Double) {
        settingsDataStore.setDouble(PreferencesKeys.ALERT_THRESHOLD, threshold)
    }
} 