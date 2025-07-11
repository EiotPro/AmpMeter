package com.example.ampmeter.data.repository

import com.example.ampmeter.data.local.database.dao.DeviceReadingDao
import com.example.ampmeter.data.local.mapper.DeviceReadingMapper
import com.example.ampmeter.data.remote.api.ChirpStackApi
import com.example.ampmeter.domain.model.DeviceReading
import com.example.ampmeter.domain.model.Resource
import com.example.ampmeter.domain.repository.DeviceRepository
import com.example.ampmeter.util.PayloadDecoder
import com.example.ampmeter.util.network.NetworkManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import retrofit2.HttpException
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of DeviceRepository.
 */
@Singleton
class DeviceRepositoryImpl @Inject constructor(
    private val chirpStackApi: ChirpStackApi,
    private val deviceReadingDao: DeviceReadingDao,
    private val networkManager: NetworkManager
) : DeviceRepository {
    
    override suspend fun getLatestReading(deviceId: String): Resource<DeviceReading> {
        return try {
            if (networkManager.isConnected()) {
                // Attempt to fetch from network
                val response = chirpStackApi.getDeviceFrames(deviceId, 1)
                if (response.isSuccessful && response.body() != null) {
                    val frames = response.body()!!.result
                    if (frames.isNotEmpty()) {
                        val frame = frames[0]
                        try {
                            val reading = PayloadDecoder.decodePayload(frame.data, deviceId).copy(
                                frameCount = frame.fCnt,
                                gatewayId = frame.rxInfo?.firstOrNull()?.gatewayId
                            )
                            
                            // Save to local database
                            val entity = DeviceReadingMapper.toEntity(reading)
                            deviceReadingDao.insert(entity)
                            
                            return Resource.Success(reading)
                        } catch (e: Exception) {
                            Timber.e(e, "Error decoding payload: %s", frame.data)
                            getFallbackReading(deviceId)
                        }
                    } else {
                        Timber.d("No frames found for device: %s", deviceId)
                        getFallbackReading(deviceId)
                    }
                } else {
                    Timber.e("API call failed: %s", response.errorBody()?.string())
                    getFallbackReading(deviceId)
                }
            } else {
                Timber.d("Network not available, using cached data")
                getFallbackReading(deviceId)
            }
        } catch (e: IOException) {
            Timber.e(e, "Network error")
            getFallbackReading(deviceId)
        } catch (e: HttpException) {
            Timber.e(e, "HTTP error: %s", e.code())
            getFallbackReading(deviceId)
        } catch (e: Exception) {
            Timber.e(e, "Unknown error")
            Resource.Error("An unexpected error occurred: ${e.message}")
        }
    }
    
    override suspend fun getDeviceReadings(
        deviceId: String,
        limit: Int,
        offset: Int
    ): Resource<List<DeviceReading>> {
        return try {
            val localReadings = deviceReadingDao.getDeviceReadings(deviceId, limit, offset)
            Resource.Success(DeviceReadingMapper.fromEntityList(localReadings))
        } catch (e: Exception) {
            Timber.e(e, "Error fetching device readings")
            Resource.Error("Failed to get device readings: ${e.message}")
        }
    }
    
    override fun getDeviceReadingsFlow(deviceId: String): Flow<List<DeviceReading>> {
        return deviceReadingDao.getDeviceReadingsFlow(deviceId).map { entities ->
            DeviceReadingMapper.fromEntityList(entities)
        }
    }
    
    override suspend fun syncWithCloud(): Resource<Unit> {
        // This will be implemented when Supabase integration is added
        return Resource.Success(Unit)
    }
    
    override suspend fun insertDeviceReading(deviceReading: DeviceReading): Resource<DeviceReading> {
        return try {
            val entity = DeviceReadingMapper.toEntity(deviceReading)
            val id = deviceReadingDao.insert(entity)
            Resource.Success(deviceReading)
        } catch (e: Exception) {
            Timber.e(e, "Error inserting device reading")
            Resource.Error("Failed to insert device reading: ${e.message}")
        }
    }
    
    /**
     * Gets fallback reading from local database.
     */
    private suspend fun getFallbackReading(deviceId: String): Resource<DeviceReading> {
        val localReading = deviceReadingDao.getLatestReading(deviceId)
        return if (localReading != null) {
            Resource.Success(DeviceReadingMapper.fromEntity(localReading))
        } else {
            Resource.Error("No data available for device $deviceId")
        }
    }
} 