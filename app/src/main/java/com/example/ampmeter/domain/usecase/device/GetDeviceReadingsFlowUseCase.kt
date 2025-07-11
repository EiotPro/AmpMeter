package com.example.ampmeter.domain.usecase.device

import com.example.ampmeter.domain.model.DeviceReading
import com.example.ampmeter.domain.repository.DeviceRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for getting a flow of device readings.
 */
class GetDeviceReadingsFlowUseCase @Inject constructor(
    private val deviceRepository: DeviceRepository
) {
    /**
     * Executes the use case.
     * 
     * @param deviceId The device identifier
     * @return Flow of List of DeviceReading
     */
    operator fun invoke(deviceId: String): Flow<List<DeviceReading>> {
        return deviceRepository.getDeviceReadingsFlow(deviceId)
    }
} 