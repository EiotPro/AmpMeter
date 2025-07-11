package com.example.ampmeter.domain.repository

import com.example.ampmeter.domain.model.DeviceReading
import com.example.ampmeter.domain.model.Resource
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for device-related operations.
 * Follows the repository pattern to abstract data sources.
 */
interface DeviceRepository {
    /**
     * Gets the latest reading for a device.
     * 
     * @param deviceId The device identifier
     * @return Resource wrapping DeviceReading
     */
    suspend fun getLatestReading(deviceId: String): Resource<DeviceReading>
    
    /**
     * Gets a list of device readings with pagination.
     * 
     * @param deviceId The device identifier
     * @param limit Maximum number of readings to return
     * @param offset Starting position for pagination
     * @return Resource wrapping List of DeviceReading
     */
    suspend fun getDeviceReadings(
        deviceId: String, 
        limit: Int, 
        offset: Int
    ): Resource<List<DeviceReading>>
    
    /**
     * Gets a Flow of device readings for observing changes.
     * 
     * @param deviceId The device identifier
     * @return Flow of List of DeviceReading
     */
    fun getDeviceReadingsFlow(deviceId: String): Flow<List<DeviceReading>>
    
    /**
     * Synchronizes local data with cloud.
     * 
     * @return Resource wrapping Unit
     */
    suspend fun syncWithCloud(): Resource<Unit>
    
    /**
     * Inserts a device reading.
     * 
     * @param deviceReading The device reading to insert
     * @return Resource wrapping the inserted DeviceReading
     */
    suspend fun insertDeviceReading(deviceReading: DeviceReading): Resource<DeviceReading>
} 