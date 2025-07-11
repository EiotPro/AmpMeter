package com.example.ampmeter.domain.repository

import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for app settings.
 */
interface SettingsRepository {
    // ChirpStack settings
    suspend fun getChirpStackServerUrl(): String
    suspend fun setChirpStackServerUrl(url: String)
    suspend fun getChirpStackApiKey(): String
    suspend fun setChirpStackApiKey(apiKey: String)
    suspend fun getDeviceId(): String
    suspend fun setDeviceId(deviceId: String)
    suspend fun getApplicationId(): String
    suspend fun setApplicationId(applicationId: String)
    
    // Supabase settings
    suspend fun getSupabaseUrl(): String
    suspend fun setSupabaseUrl(url: String)
    suspend fun getSupabaseApiKey(): String
    suspend fun setSupabaseApiKey(apiKey: String)
    suspend fun getSupabaseTableName(): String
    suspend fun setSupabaseTableName(tableName: String)
    
    // App preferences
    suspend fun getRefreshInterval(): Int
    suspend fun setRefreshInterval(intervalSeconds: Int)
    fun getRefreshIntervalFlow(): Flow<Int>
    
    suspend fun getDataRetentionPeriod(): Int
    suspend fun setDataRetentionPeriod(periodDays: Int)
    
    suspend fun getThemeMode(): Int
    suspend fun setThemeMode(themeMode: Int)
    
    suspend fun getNotificationsEnabled(): Boolean
    suspend fun setNotificationsEnabled(enabled: Boolean)
    
    // Device settings
    suspend fun getDeviceName(): String
    suspend fun setDeviceName(name: String)
    
    suspend fun getAlertThreshold(): Double
    suspend fun setAlertThreshold(threshold: Double)
} 