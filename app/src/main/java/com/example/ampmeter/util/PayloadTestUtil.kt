package com.example.ampmeter.util

import android.util.Base64
import timber.log.Timber
import java.nio.ByteBuffer
import java.nio.ByteOrder

/**
 * Utility for testing payload encoding and decoding.
 * This class helps generate test payloads and verify decoder functionality.
 */
object PayloadTestUtil {
    
    /**
     * Generates a test payload with the specified values.
     *
     * @param current Current in Amperes (will be converted to mA)
     * @param voltage Voltage in Volts (will be converted to cV)
     * @param battery Battery level percentage (0-100)
     * @param rssi Optional RSSI value in dBm (will be converted to positive value)
     * @param snr Optional SNR value in dB (will be converted to 0.1 dB units)
     * @return Base64 encoded payload string
     */
    fun generateTestPayload(
        current: Double,
        voltage: Double,
        battery: Int,
        rssi: Int? = null,
        snr: Double? = null
    ): String {
        try {
            // Convert to raw values
            val currentRaw = (current * 1000).toInt() // Convert to mA
            val voltageRaw = (voltage * 100).toInt() // Convert to cV
            val batteryRaw = battery.coerceIn(0, 100)
            
            // Determine buffer size based on included values
            val bufferSize = if (rssi != null && snr != null) 7 else if (rssi != null) 6 else 5
            
            val buffer = ByteBuffer.allocate(bufferSize).order(ByteOrder.BIG_ENDIAN)
            
            // Add current (signed 16-bit)
            buffer.putShort(currentRaw.toShort())
            
            // Add voltage (unsigned 16-bit)
            buffer.putShort((voltageRaw and 0xFFFF).toShort())
            
            // Add battery level
            buffer.put(batteryRaw.toByte())
            
            // Add RSSI if provided
            if (rssi != null) {
                val rssiRaw = (-rssi) and 0xFF // Convert to positive value
                buffer.put(rssiRaw.toByte())
                
                // Add SNR if provided
                if (snr != null) {
                    val snrRaw = (snr * 10).toInt() // Convert to 0.1 dB units
                    buffer.put(snrRaw.toByte())
                }
            }
            
            // Convert to Base64
            return Base64.encodeToString(buffer.array(), Base64.DEFAULT)
        } catch (e: Exception) {
            Timber.e(e, "Error generating test payload")
            throw IllegalArgumentException("Failed to generate test payload", e)
        }
    }
    
    /**
     * Verifies the payload decoder by encoding and then decoding values.
     *
     * @param current Current in Amperes
     * @param voltage Voltage in Volts
     * @param battery Battery level percentage (0-100)
     * @param rssi Optional RSSI value in dBm
     * @param snr Optional SNR value in dB
     * @return True if the decoded values match the input values within tolerance
     */
    fun verifyPayloadDecoder(
        current: Double,
        voltage: Double,
        battery: Int,
        rssi: Int? = null,
        snr: Double? = null
    ): Boolean {
        try {
            // Generate payload
            val payload = generateTestPayload(current, voltage, battery, rssi, snr)
            
            // Decode payload
            val decoded = PayloadDecoder.decodePayload(payload)
            
            // Verify values (with small tolerance for floating point)
            val currentMatch = Math.abs(decoded.current - current) < 0.001
            val voltageMatch = Math.abs(decoded.voltage - voltage) < 0.01
            val batteryMatch = decoded.batteryLevel == battery
            val rssiMatch = rssi == null || decoded.rssi == rssi
            val snrMatch = snr == null || Math.abs(decoded.snr!! - snr) < 0.1
            
            return currentMatch && voltageMatch && batteryMatch && rssiMatch && snrMatch
        } catch (e: Exception) {
            Timber.e(e, "Error verifying payload decoder")
            return false
        }
    }
    
    /**
     * Runs a series of tests on the payload decoder and returns results.
     *
     * @return Map of test names to boolean results
     */
    fun runDecoderTests(): Map<String, Boolean> {
        val results = mutableMapOf<String, Boolean>()
        
        // Test 1: Standard payload (current, voltage, battery)
        results["Standard Payload"] = verifyPayloadDecoder(
            current = 2.5,
            voltage = 12.0,
            battery = 75
        )
        
        // Test 2: Zero values
        results["Zero Values"] = verifyPayloadDecoder(
            current = 0.0,
            voltage = 0.0,
            battery = 0
        )
        
        // Test 3: Negative current
        results["Negative Current"] = verifyPayloadDecoder(
            current = -1.5,
            voltage = 12.0,
            battery = 80
        )
        
        // Test 4: Max values
        results["Max Values"] = verifyPayloadDecoder(
            current = 32.767, // Max signed 16-bit in Amperes
            voltage = 655.35, // Max unsigned 16-bit in Volts
            battery = 100
        )
        
        // Test 5: With RSSI
        results["With RSSI"] = verifyPayloadDecoder(
            current = 1.0,
            voltage = 5.0,
            battery = 50,
            rssi = -75
        )
        
        // Test 6: With RSSI and SNR
        results["With RSSI and SNR"] = verifyPayloadDecoder(
            current = 1.0,
            voltage = 5.0,
            battery = 50,
            rssi = -80,
            snr = 5.5
        )
        
        // Test 7: Negative SNR
        results["Negative SNR"] = verifyPayloadDecoder(
            current = 1.0,
            voltage = 5.0,
            battery = 50,
            rssi = -85,
            snr = -2.5
        )
        
        return results
    }
} 