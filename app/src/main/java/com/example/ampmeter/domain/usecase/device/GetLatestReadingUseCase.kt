package com.example.ampmeter.domain.usecase.device

import com.example.ampmeter.domain.model.DeviceReading
import com.example.ampmeter.domain.model.Resource
import com.example.ampmeter.domain.repository.DeviceRepository
import javax.inject.Inject

/**
 * Use case for getting the latest device reading.
 */
class GetLatestReadingUseCase @Inject constructor(
    private val deviceRepository: DeviceRepository
) {
    /**
     * Executes the use case.
     * 
     * @param deviceId The device identifier
     * @return Resource wrapping DeviceReading
     */
    suspend operator fun invoke(deviceId: String): Resource<DeviceReading> {
        return deviceRepository.getLatestReading(deviceId)
    }
} 