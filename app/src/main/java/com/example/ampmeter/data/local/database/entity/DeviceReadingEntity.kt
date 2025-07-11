package com.example.ampmeter.data.local.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Index

/**
 * Room database entity for device sensor readings.
 */
@Entity(
    tableName = "device_readings",
    indices = [
        Index("deviceId"),
        Index("timestamp")
    ]
)
data class DeviceReadingEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val uuid: String,
    val deviceId: String,
    val current: Double,
    val voltage: Double,
    val batteryLevel: Int,
    val rssi: Int?,
    val snr: Double?,
    val timestamp: Long,
    val frameCount: Int?,
    val gatewayId: String?,
    val status: String?,
    val rawData: String?,
    val isSynced: Boolean
) 