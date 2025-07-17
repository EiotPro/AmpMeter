package com.example.ampmeter.data.local.datastore

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.floatPreferencesKey
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
    
    // MQTT settings
    val MQTT_BROKER_URL = stringPreferencesKey("mqtt_broker_url")
    val MQTT_PORT = intPreferencesKey("mqtt_port")
    val MQTT_ENABLED = booleanPreferencesKey("mqtt_enabled")
    val MQTT_USERNAME = stringPreferencesKey("mqtt_username")
    val MQTT_PASSWORD = stringPreferencesKey("mqtt_password")
    val MQTT_TOPIC = stringPreferencesKey("mqtt_topic")
    
    // WiFi settings
    val WIFI_ENABLED = booleanPreferencesKey("wifi_enabled")
    val WIFI_SSID = stringPreferencesKey("wifi_ssid")
    val WIFI_PASSWORD = stringPreferencesKey("wifi_password")
    val WIFI_TX_INTERVAL = intPreferencesKey("wifi_tx_interval")
    
    // BLE settings
    val BLE_ENABLED = booleanPreferencesKey("ble_enabled")
    val BLE_DEVICE_NAME = stringPreferencesKey("ble_device_name")
    val BLE_TX_INTERVAL = intPreferencesKey("ble_tx_interval")
    
    // LoRaWAN settings
    val LORAWAN_DEV_ADDR = stringPreferencesKey("lorawan_dev_addr")
    val LORAWAN_NWKS_KEY = stringPreferencesKey("lorawan_nwks_key")
    val LORAWAN_APPS_KEY = stringPreferencesKey("lorawan_apps_key")
    val LORAWAN_REGION = intPreferencesKey("lorawan_region")
    val TX_INTERVAL = intPreferencesKey("tx_interval")
    
    // OTA settings
    val OTA_ENABLED = booleanPreferencesKey("ota_enabled")
    val OTA_SERVER_URL = stringPreferencesKey("ota_server_url")
    val OTA_HTTP_USERNAME = stringPreferencesKey("ota_http_username")
    val OTA_HTTP_PASSWORD = stringPreferencesKey("ota_http_password")
    
    // Sensor configuration
    val WCS6800_SENSITIVITY = floatPreferencesKey("wcs6800_sensitivity")
    val WCS6800_OFFSET_VOLTAGE = floatPreferencesKey("wcs6800_offset_voltage")
    val ADC_REF_VOLTAGE = floatPreferencesKey("adc_ref_voltage")
    
    // Advanced settings
    val DEBUG_LEVEL = intPreferencesKey("debug_level")
    val USE_ADAPTIVE_SAMPLING_RATE = booleanPreferencesKey("use_adaptive_sampling_rate")
    val MEASUREMENT_INTERVAL = intPreferencesKey("measurement_interval")
    val DATA_FORMAT = stringPreferencesKey("data_format")
    
    // App preferences
    val REFRESH_INTERVAL = intPreferencesKey("refresh_interval")
    val DATA_RETENTION_PERIOD = intPreferencesKey("data_retention_period")
    val THEME_MODE = intPreferencesKey("theme_mode")
    val NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
    
    // Device settings
    val DEVICE_NAME = stringPreferencesKey("device_name")
    val ALERT_THRESHOLD = doublePreferencesKey("alert_threshold")
} 