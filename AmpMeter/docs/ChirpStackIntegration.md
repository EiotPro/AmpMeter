# ChirpStack API Integration Guide

This document outlines the integration of the ChirpStack LoRaWAN Network Server with the AmpMeter Android application for retrieving real-time sensor data.

## Overview

ChirpStack is an open-source LoRaWAN Network Server that manages the communication between LoRaWAN gateways and end-devices (sensors). The AmpMeter app uses the ChirpStack REST API to retrieve current sensor readings transmitted by the RP2040 Pico W with RAK3172 LoRa module.

## API Configuration

### Dependencies

Add the following dependencies to the app's build.gradle.kts file:

```kotlin
// Networking libraries
implementation("com.squareup.retrofit2:retrofit:2.9.0")
implementation("com.squareup.retrofit2:converter-gson:2.9.0")
implementation("com.squareup.okhttp3:okhttp:4.11.0")
implementation("com.squareup.okhttp3:logging-interceptor:4.11.0")
```

### Retrofit Interface

```kotlin
interface ChirpStackApi {
    @GET("api/devices/{devEUI}/frames")
    suspend fun getDeviceFrames(
        @Path("devEUI") deviceEui: String,
        @Query("limit") limit: Int = 100,
        @Query("offset") offset: Int = 0
    ): Response<FramesResponse>
    
    @GET("api/devices/{devEUI}")
    suspend fun getDeviceInfo(
        @Path("devEUI") deviceEui: String
    ): Response<DeviceResponse>
    
    @POST("api/devices/{devEUI}/queue")
    suspend fun queueDownlink(
        @Path("devEUI") deviceEui: String,
        @Body request: DownlinkRequest
    ): Response<Unit>
}
```

### API Response DTOs

```kotlin
data class FramesResponse(
    val totalCount: Int,
    val result: List<Frame>
)

data class Frame(
    val phyPayload: String,
    val data: String,  // Base64 encoded sensor data
    val fCnt: Int,
    val fPort: Int,
    val time: String,
    val rxInfo: List<RxInfo>?
)

data class RxInfo(
    val gatewayId: String,
    val rssi: Int,
    val snr: Double,
    val time: String
)

data class DeviceResponse(
    val device: Device
)

data class Device(
    val devEUI: String,
    val name: String,
    val description: String,
    val deviceProfileID: String,
    val lastSeenAt: String?
)

data class DownlinkRequest(
    val devEUI: String,
    val confirmed: Boolean,
    val fPort: Int,
    val data: String  // Base64 encoded downlink data
)
```

### Retrofit Setup with Authentication

```kotlin
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(
        settingsRepository: SettingsRepository
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor { chain ->
                val originalRequest = chain.request()
                val apiKey = settingsRepository.getChirpStackApiKey()
                
                val newRequest = originalRequest.newBuilder()
                    .header("Authorization", "Bearer $apiKey")
                    .build()
                    
                chain.proceed(newRequest)
            }
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = if (BuildConfig.DEBUG) {
                    HttpLoggingInterceptor.Level.BODY
                } else {
                    HttpLoggingInterceptor.Level.NONE
                }
            })
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }
    
    @Provides
    @Singleton
    fun provideRetrofit(
        okHttpClient: OkHttpClient,
        settingsRepository: SettingsRepository
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(settingsRepository.getChirpStackServerUrl())
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    
    @Provides
    @Singleton
    fun provideChirpStackApi(retrofit: Retrofit): ChirpStackApi {
        return retrofit.create(ChirpStackApi::class.java)
    }
}
```

## Payload Decoding

The WCS6800 current sensor sends data in a specific format that needs to be decoded:

```kotlin
/**
 * Decodes the sensor payload from base64 encoded string.
 * Payload format:
 * - Byte 0-1: Current value (signed 16-bit)
 * - Byte 2-3: Voltage (unsigned 16-bit)
 * - Byte 4: Battery level (0-100)
 */
object PayloadDecoder {
    fun decodePayload(base64Data: String): SensorReading {
        val bytes = Base64.decode(base64Data, Base64.DEFAULT)
        
        // Extract current value (signed 16-bit, in mA)
        val currentRaw = ((bytes[0].toInt() and 0xFF) shl 8) or (bytes[1].toInt() and 0xFF)
        // Convert to signed value
        val currentSigned = if (currentRaw > 32767) currentRaw - 65536 else currentRaw
        val current = currentSigned * 0.001 // Convert to Amperes
        
        // Extract voltage (unsigned 16-bit, in cV)
        val voltageRaw = ((bytes[2].toInt() and 0xFF) shl 8) or (bytes[3].toInt() and 0xFF)
        val voltage = voltageRaw * 0.01 // Convert to Volts
        
        // Extract battery level (0-100%)
        val battery = bytes[4].toInt() and 0xFF
        
        // Extract RSSI and SNR if available
        var rssi: Int? = null
        var snr: Double? = null
        if (bytes.size > 5) {
            rssi = -1 * (bytes[5].toInt() and 0xFF) // Convert to negative dBm
            
            if (bytes.size > 6) {
                // SNR is a signed value in 0.1 dB units
                val snrRaw = bytes[6].toInt()
                snr = (if (snrRaw > 127) snrRaw - 256 else snrRaw) * 0.1
            }
        }
        
        return SensorReading(
            current = current,
            voltage = voltage,
            batteryLevel = battery,
            rssi = rssi,
            snr = snr,
            timestamp = System.currentTimeMillis()
        )
    }
}
```

## Repository Implementation

```kotlin
class ChirpStackRepository @Inject constructor(
    private val chirpStackApi: ChirpStackApi,
    private val localDatabase: DeviceReadingDao,
    private val networkManager: NetworkManager
) {
    suspend fun getLatestReading(deviceId: String): Resource<DeviceReading> {
        return try {
            if (networkManager.isConnected()) {
                val response = chirpStackApi.getDeviceFrames(deviceId, 1)
                if (response.isSuccessful && response.body() != null) {
                    val frames = response.body()!!.result
                    if (frames.isNotEmpty()) {
                        val frame = frames[0]
                        val reading = PayloadDecoder.decodePayload(frame.data).copy(
                            deviceId = deviceId,
                            frameCount = frame.fCnt,
                            gatewayId = frame.rxInfo?.firstOrNull()?.gatewayId,
                            timestamp = parseChirpStackTimestamp(frame.time),
                            rawData = frame.data
                        )
                        
                        // Cache the reading locally
                        localDatabase.insertReading(reading.toEntity())
                        
                        Resource.Success(reading)
                    } else {
                        getFallbackData(deviceId)
                    }
                } else {
                    Timber.e("API Error: ${response.errorBody()?.string()}")
                    getFallbackData(deviceId)
                }
            } else {
                Timber.d("No network connection, using cached data")
                getFallbackData(deviceId)
            }
        } catch (e: Exception) {
            Timber.e(e, "Error fetching latest reading")
            getFallbackData(deviceId)
        }
    }
    
    private suspend fun getFallbackData(deviceId: String): Resource<DeviceReading> {
        val cachedReading = localDatabase.getLatestReading(deviceId)
        return if (cachedReading != null) {
            Resource.Success(cachedReading.toDomain())
        } else {
            Resource.Error("No data available")
        }
    }
    
    suspend fun getDeviceReadings(
        deviceId: String, 
        limit: Int, 
        offset: Int
    ): Resource<List<DeviceReading>> {
        return try {
            if (networkManager.isConnected()) {
                val response = chirpStackApi.getDeviceFrames(deviceId, limit, offset)
                if (response.isSuccessful && response.body() != null) {
                    val frames = response.body()!!.result
                    val readings = frames.map { frame ->
                        PayloadDecoder.decodePayload(frame.data).copy(
                            deviceId = deviceId,
                            frameCount = frame.fCnt,
                            gatewayId = frame.rxInfo?.firstOrNull()?.gatewayId,
                            timestamp = parseChirpStackTimestamp(frame.time),
                            rawData = frame.data
                        )
                    }
                    
                    // Cache the readings locally
                    localDatabase.insertReadings(readings.map { it.toEntity() })
                    
                    Resource.Success(readings)
                } else {
                    Timber.e("API Error: ${response.errorBody()?.string()}")
                    getLocalReadings(deviceId, limit, offset)
                }
            } else {
                Timber.d("No network connection, using cached data")
                getLocalReadings(deviceId, limit, offset)
            }
        } catch (e: Exception) {
            Timber.e(e, "Error fetching device readings")
            getLocalReadings(deviceId, limit, offset)
        }
    }
    
    private suspend fun getLocalReadings(
        deviceId: String, 
        limit: Int, 
        offset: Int
    ): Resource<List<DeviceReading>> {
        val cachedReadings = localDatabase.getReadings(deviceId, limit, offset)
        return if (cachedReadings.isNotEmpty()) {
            Resource.Success(cachedReadings.map { it.toDomain() })
        } else {
            Resource.Error("No data available")
        }
    }
    
    private fun parseChirpStackTimestamp(timestampString: String): Long {
        return try {
            val instant = Instant.parse(timestampString)
            instant.toEpochMilli()
        } catch (e: Exception) {
            Timber.e(e, "Error parsing timestamp: $timestampString")
            System.currentTimeMillis()
        }
    }
}
```

## Error Handling

1. **Network Errors**: Fallback to local database when network is unavailable
2. **API Errors**: Log error details and use cached data
3. **Parsing Errors**: Handle malformed data gracefully
4. **Authentication Errors**: Prompt user to update API key

## Optimization Strategies

1. **Caching**: Store all retrieved data in local database
2. **Request Throttling**: Limit frequency of API requests to reduce battery usage
3. **Payload Size**: Request only necessary data fields to minimize bandwidth
4. **Connection Pooling**: Reuse HTTP connections for multiple requests

## Testing

1. **Unit Tests**: Test payload decoder with sample data
2. **Mock Tests**: Use MockWebServer to test API responses
3. **Integration Tests**: Test repository with real API (using test server)
4. **Error Handling Tests**: Verify fallback behavior works correctly 