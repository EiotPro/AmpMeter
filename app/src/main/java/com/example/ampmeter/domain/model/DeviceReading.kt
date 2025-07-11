package com.example.ampmeter.domain.model

/**
 * Domain model representing a single device sensor reading.
 */
data class DeviceReading(
    val id: String = "",
    val deviceId: String,
    val current: Double,
    val voltage: Double,
    val batteryLevel: Int,
    val rssi: Int? = null,
    val snr: Double? = null,
    val timestamp: Long,
    val frameCount: Int? = null,
    val gatewayId: String? = null,
    val status: String? = null,
    val rawData: String? = null,
    val isSynced: Boolean = false
) 