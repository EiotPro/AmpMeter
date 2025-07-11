package com.example.ampmeter.domain.usecase.device

import com.example.ampmeter.domain.model.DeviceReading
import com.example.ampmeter.domain.model.Resource
import com.example.ampmeter.domain.repository.DeviceRepository
import javax.inject.Inject

/**
 * Use case for getting device readings with pagination.
 */
class GetDeviceReadingsUseCase @Inject constructor(
    private val deviceRepository: DeviceRepository
) {
    /**
     * Executes the use case.
     * 
     * @param deviceId The device identifier
     * @param limit Maximum number of readings to return
     * @param offset Starting position for pagination
     * @return Resource wrapping List of DeviceReading
     */
    suspend operator fun invoke(
        deviceId: String,
        limit: Int = 20,
        offset: Int = 0
    ): Resource<List<DeviceReading>> {
        return deviceRepository.getDeviceReadings(deviceId, limit, offset)
    }
} 