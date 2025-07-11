package com.example.ampmeter.data.remote.api

import com.example.ampmeter.data.remote.dto.chirpstack.DeviceResponse
import com.example.ampmeter.data.remote.dto.chirpstack.DownlinkRequest
import com.example.ampmeter.data.remote.dto.chirpstack.FramesResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Retrofit interface for ChirpStack API.
 */
interface ChirpStackApi {
    /**
     * Get recent uplink frames for a device.
     *
     * @param deviceEui The device EUI (16 hex characters)
     * @param limit Maximum number of frames to retrieve
     * @param offset Offset for pagination
     * @return Response containing FramesResponse
     */
    @GET("api/devices/{devEUI}/frames")
    suspend fun getDeviceFrames(
        @Path("devEUI") deviceEui: String,
        @Query("limit") limit: Int = 100,
        @Query("offset") offset: Int = 0
    ): Response<FramesResponse>
    
    /**
     * Get device information.
     *
     * @param deviceEui The device EUI (16 hex characters)
     * @return Response containing DeviceResponse
     */
    @GET("api/devices/{devEUI}")
    suspend fun getDeviceInfo(
        @Path("devEUI") deviceEui: String
    ): Response<DeviceResponse>
    
    /**
     * Queue a downlink message for the device.
     *
     * @param deviceEui The device EUI (16 hex characters)
     * @param request The downlink request
     * @return Response indicating success
     */
    @POST("api/devices/{devEUI}/queue")
    suspend fun queueDownlink(
        @Path("devEUI") deviceEui: String,
        @Body request: DownlinkRequest
    ): Response<Unit>
} 