package com.example.ampmeter.util

import android.util.Base64
import com.example.ampmeter.domain.model.DeviceReading
import timber.log.Timber
import java.util.UUID

/**
 * Utility for decoding sensor payload data.
 */
object PayloadDecoder {
    
    /**
     * Decodes the sensor payload from base64 encoded string.
     * Payload format:
     * - Byte 0-1: Current value (signed 16-bit)
     * - Byte 2-3: Voltage (unsigned 16-bit)
     * - Byte 4: Battery level (0-100)
     *
     * @param base64Data Base64 encoded binary data
     * @param deviceId Optional device identifier
     * @return DeviceReading with decoded values
     */
    fun decodePayload(base64Data: String, deviceId: String = ""): DeviceReading {
        try {
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
            
            return DeviceReading(
                id = UUID.randomUUID().toString(),
                deviceId = deviceId,
                current = current,
                voltage = voltage,
                batteryLevel = battery,
                rssi = rssi,
                snr = snr,
                timestamp = System.currentTimeMillis(),
                rawData = base64Data,
                isSynced = false
            )
        } catch (e: Exception) {
            Timber.e(e, "Error decoding payload: %s", base64Data)
            throw IllegalArgumentException("Invalid payload format", e)
        }
    }
} 