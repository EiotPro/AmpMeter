package com.example.ampmeter.data.remote.dto.chirpstack

import com.google.gson.annotations.SerializedName

/**
 * Data Transfer Object for ChirpStack frames response.
 */
data class FramesResponse(
    @SerializedName("totalCount")
    val totalCount: Int,
    
    @SerializedName("result")
    val result: List<Frame>
)

/**
 * Data Transfer Object for a single frame.
 */
data class Frame(
    @SerializedName("phyPayload")
    val phyPayload: String,
    
    @SerializedName("data")
    val data: String,  // Base64 encoded sensor data
    
    @SerializedName("fCnt")
    val fCnt: Int,
    
    @SerializedName("fPort")
    val fPort: Int,
    
    @SerializedName("time")
    val time: String,
    
    @SerializedName("rxInfo")
    val rxInfo: List<RxInfo>?
)

/**
 * Data Transfer Object for receiver information.
 */
data class RxInfo(
    @SerializedName("gatewayId")
    val gatewayId: String,
    
    @SerializedName("rssi")
    val rssi: Int,
    
    @SerializedName("snr")
    val snr: Double,
    
    @SerializedName("time")
    val time: String
) 