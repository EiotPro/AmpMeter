package com.example.ampmeter.data.remote.dto.chirpstack

import com.google.gson.annotations.SerializedName

/**
 * Data Transfer Object for ChirpStack device response.
 */
data class DeviceResponse(
    @SerializedName("device")
    val device: Device
)

/**
 * Data Transfer Object for device information.
 */
data class Device(
    @SerializedName("devEUI")
    val devEUI: String,
    
    @SerializedName("name")
    val name: String,
    
    @SerializedName("description")
    val description: String,
    
    @SerializedName("deviceProfileID")
    val deviceProfileID: String,
    
    @SerializedName("lastSeenAt")
    val lastSeenAt: String?
) 