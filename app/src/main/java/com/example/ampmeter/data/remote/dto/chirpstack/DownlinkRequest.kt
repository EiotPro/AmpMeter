package com.example.ampmeter.data.remote.dto.chirpstack

import com.google.gson.annotations.SerializedName

/**
 * Data Transfer Object for ChirpStack downlink request.
 */
data class DownlinkRequest(
    @SerializedName("devEUI")
    val devEUI: String,
    
    @SerializedName("confirmed")
    val confirmed: Boolean,
    
    @SerializedName("fPort")
    val fPort: Int,
    
    @SerializedName("data")
    val data: String  // Base64 encoded downlink data
) 