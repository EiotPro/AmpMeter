package com.example.ampmeter.data.local.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.ampmeter.data.local.database.entity.DeviceReadingEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for DeviceReadingEntity.
 */
@Dao
interface DeviceReadingDao {
    /**
     * Insert a new device reading.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(deviceReading: DeviceReadingEntity): Long
    
    /**
     * Insert multiple device readings.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(deviceReadings: List<DeviceReadingEntity>): List<Long>
    
    /**
     * Update an existing device reading.
     */
    @Update
    suspend fun update(deviceReading: DeviceReadingEntity)
    
    /**
     * Get the latest reading for a device.
     */
    @Query("SELECT * FROM device_readings WHERE deviceId = :deviceId ORDER BY timestamp DESC LIMIT 1")
    suspend fun getLatestReading(deviceId: String): DeviceReadingEntity?
    
    /**
     * Get all readings for a device with pagination.
     */
    @Query("SELECT * FROM device_readings WHERE deviceId = :deviceId ORDER BY timestamp DESC LIMIT :limit OFFSET :offset")
    suspend fun getDeviceReadings(deviceId: String, limit: Int, offset: Int): List<DeviceReadingEntity>
    
    /**
     * Get a Flow of all readings for a device.
     */
    @Query("SELECT * FROM device_readings WHERE deviceId = :deviceId ORDER BY timestamp DESC")
    fun getDeviceReadingsFlow(deviceId: String): Flow<List<DeviceReadingEntity>>
    
    /**
     * Get unsynced readings.
     */
    @Query("SELECT * FROM device_readings WHERE isSynced = 0")
    suspend fun getUnsyncedReadings(): List<DeviceReadingEntity>
    
    /**
     * Mark a reading as synced.
     */
    @Query("UPDATE device_readings SET isSynced = 1 WHERE id = :id")
    suspend fun markAsSynced(id: Long)
    
    /**
     * Delete old readings beyond the retention period.
     */
    @Query("DELETE FROM device_readings WHERE timestamp < :timestampThreshold")
    suspend fun deleteOldReadings(timestampThreshold: Long): Int
    
    /**
     * Get count of all readings.
     */
    @Query("SELECT COUNT(*) FROM device_readings")
    suspend fun getCount(): Int
} 