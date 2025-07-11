package com.example.ampmeter.data.local.mapper

import com.example.ampmeter.data.local.database.entity.DeviceReadingEntity
import com.example.ampmeter.domain.model.DeviceReading
import java.util.UUID

/**
 * Mapper for converting between DeviceReadingEntity and DeviceReading.
 */
object DeviceReadingMapper {
    
    /**
     * Maps a DeviceReading to a DeviceReadingEntity.
     */
    fun toEntity(deviceReading: DeviceReading): DeviceReadingEntity {
        return DeviceReadingEntity(
            uuid = deviceReading.id.ifEmpty { UUID.randomUUID().toString() },
            deviceId = deviceReading.deviceId,
            current = deviceReading.current,
            voltage = deviceReading.voltage,
            batteryLevel = deviceReading.batteryLevel,
            rssi = deviceReading.rssi,
            snr = deviceReading.snr,
            timestamp = deviceReading.timestamp,
            frameCount = deviceReading.frameCount,
            gatewayId = deviceReading.gatewayId,
            status = deviceReading.status,
            rawData = deviceReading.rawData,
            isSynced = deviceReading.isSynced
        )
    }
    
    /**
     * Maps a DeviceReadingEntity to a DeviceReading.
     */
    fun fromEntity(entity: DeviceReadingEntity): DeviceReading {
        return DeviceReading(
            id = entity.uuid,
            deviceId = entity.deviceId,
            current = entity.current,
            voltage = entity.voltage,
            batteryLevel = entity.batteryLevel,
            rssi = entity.rssi,
            snr = entity.snr,
            timestamp = entity.timestamp,
            frameCount = entity.frameCount,
            gatewayId = entity.gatewayId,
            status = entity.status,
            rawData = entity.rawData,
            isSynced = entity.isSynced
        )
    }
    
    /**
     * Maps a list of DeviceReadingEntities to a list of DeviceReadings.
     */
    fun fromEntityList(entities: List<DeviceReadingEntity>): List<DeviceReading> {
        return entities.map { fromEntity(it) }
    }
    
    /**
     * Maps a list of DeviceReadings to a list of DeviceReadingEntities.
     */
    fun toEntityList(deviceReadings: List<DeviceReading>): List<DeviceReadingEntity> {
        return deviceReadings.map { toEntity(it) }
    }
} 