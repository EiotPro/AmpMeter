package com.example.ampmeter.data.local.datastore

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

/**
 * Keys for DataStore preferences.
 */
object PreferencesKeys {
    // ChirpStack settings
    val CHIRP_STACK_SERVER_URL = stringPreferencesKey("chirp_stack_server_url")
    val CHIRP_STACK_API_KEY = stringPreferencesKey("chirp_stack_api_key")
    val DEVICE_ID = stringPreferencesKey("device_id")
    val APPLICATION_ID = stringPreferencesKey("application_id")
    
    // Supabase settings
    val SUPABASE_URL = stringPreferencesKey("supabase_url")
    val SUPABASE_API_KEY = stringPreferencesKey("supabase_api_key")
    val SUPABASE_TABLE_NAME = stringPreferencesKey("supabase_table_name")
    
    // App preferences
    val REFRESH_INTERVAL = intPreferencesKey("refresh_interval")
    val DATA_RETENTION_PERIOD = intPreferencesKey("data_retention_period")
    val THEME_MODE = intPreferencesKey("theme_mode")
    val NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
    
    // Device settings
    val DEVICE_NAME = stringPreferencesKey("device_name")
    val ALERT_THRESHOLD = doublePreferencesKey("alert_threshold")
} 