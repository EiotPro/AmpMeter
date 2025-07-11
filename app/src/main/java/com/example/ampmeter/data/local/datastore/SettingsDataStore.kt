package com.example.ampmeter.data.local.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

// Extension property for Context to create a single DataStore instance
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

/**
 * Implementation of DataStore for app settings.
 */
@Singleton
class SettingsDataStore @Inject constructor(
    private val context: Context
) {
    /**
     * Get a string from DataStore.
     */
    fun getString(key: Preferences.Key<String>, defaultValue: String = ""): Flow<String> {
        return context.dataStore.data.map { preferences ->
            preferences[key] ?: defaultValue
        }
    }
    
    /**
     * Set a string in DataStore.
     */
    suspend fun setString(key: Preferences.Key<String>, value: String) {
        context.dataStore.edit { preferences ->
            preferences[key] = value
        }
    }
    
    /**
     * Get an integer from DataStore.
     */
    fun getInt(key: Preferences.Key<Int>, defaultValue: Int = 0): Flow<Int> {
        return context.dataStore.data.map { preferences ->
            preferences[key] ?: defaultValue
        }
    }
    
    /**
     * Set an integer in DataStore.
     */
    suspend fun setInt(key: Preferences.Key<Int>, value: Int) {
        context.dataStore.edit { preferences ->
            preferences[key] = value
        }
    }
    
    /**
     * Get a boolean from DataStore.
     */
    fun getBoolean(key: Preferences.Key<Boolean>, defaultValue: Boolean = false): Flow<Boolean> {
        return context.dataStore.data.map { preferences ->
            preferences[key] ?: defaultValue
        }
    }
    
    /**
     * Set a boolean in DataStore.
     */
    suspend fun setBoolean(key: Preferences.Key<Boolean>, value: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[key] = value
        }
    }
    
    /**
     * Get a double from DataStore.
     */
    fun getDouble(key: Preferences.Key<Double>, defaultValue: Double = 0.0): Flow<Double> {
        return context.dataStore.data.map { preferences ->
            preferences[key] ?: defaultValue
        }
    }
    
    /**
     * Set a double in DataStore.
     */
    suspend fun setDouble(key: Preferences.Key<Double>, value: Double) {
        context.dataStore.edit { preferences ->
            preferences[key] = value
        }
    }
    
    /**
     * Get a string value synchronously (for use in API initialization).
     */
    suspend fun getStringSynchronously(key: Preferences.Key<String>, defaultValue: String = ""): String {
        return getString(key, defaultValue).first()
    }
} 