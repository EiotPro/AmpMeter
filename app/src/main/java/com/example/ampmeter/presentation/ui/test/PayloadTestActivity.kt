package com.example.ampmeter.presentation.ui.test

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.ampmeter.R
import com.example.ampmeter.domain.model.DeviceReading
import com.example.ampmeter.util.PayloadDecoder
import com.example.ampmeter.util.PayloadTestUtil
import com.google.android.material.switchmaterial.SwitchMaterial
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

/**
 * Activity for testing payload encoding and decoding.
 * This is a development/testing tool and not part of the main app flow.
 */
@AndroidEntryPoint
class PayloadTestActivity : AppCompatActivity() {
    
    private lateinit var editCurrent: EditText
    private lateinit var editVoltage: EditText
    private lateinit var editBattery: EditText
    private lateinit var editRssi: EditText
    private lateinit var editSnr: EditText
    private lateinit var switchIncludeRssi: SwitchMaterial
    private lateinit var switchIncludeSnr: SwitchMaterial
    private lateinit var buttonGenerate: Button
    private lateinit var buttonRunTests: Button
    private lateinit var textPayload: TextView
    private lateinit var textDecoded: TextView
    private lateinit var textTestResults: TextView
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payload_test)
        
        // Initialize views
        editCurrent = findViewById(R.id.edit_current)
        editVoltage = findViewById(R.id.edit_voltage)
        editBattery = findViewById(R.id.edit_battery)
        editRssi = findViewById(R.id.edit_rssi)
        editSnr = findViewById(R.id.edit_snr)
        switchIncludeRssi = findViewById(R.id.switch_include_rssi)
        switchIncludeSnr = findViewById(R.id.switch_include_snr)
        buttonGenerate = findViewById(R.id.button_generate)
        buttonRunTests = findViewById(R.id.button_run_tests)
        textPayload = findViewById(R.id.text_payload)
        textDecoded = findViewById(R.id.text_decoded)
        textTestResults = findViewById(R.id.text_test_results)
        
        // Set up listeners
        setupListeners()
        
        // Set default values
        setDefaultValues()
    }
    
    private fun setupListeners() {
        // Generate payload button
        buttonGenerate.setOnClickListener {
            generateAndDecodePayload()
        }
        
        // Run tests button
        buttonRunTests.setOnClickListener {
            runDecoderTests()
        }
        
        // RSSI switch
        switchIncludeRssi.setOnCheckedChangeListener { _, isChecked ->
            editRssi.isEnabled = isChecked
            if (!isChecked) {
                switchIncludeSnr.isChecked = false
            }
        }
        
        // SNR switch
        switchIncludeSnr.setOnCheckedChangeListener { _, isChecked ->
            editSnr.isEnabled = isChecked
            if (isChecked) {
                switchIncludeRssi.isChecked = true
            }
        }
    }
    
    private fun setDefaultValues() {
        editCurrent.setText("1.5")
        editVoltage.setText("12.0")
        editBattery.setText("75")
        editRssi.setText("-80")
        editSnr.setText("5.0")
        
        switchIncludeRssi.isChecked = false
        switchIncludeSnr.isChecked = false
        
        editRssi.isEnabled = false
        editSnr.isEnabled = false
    }
    
    private fun generateAndDecodePayload() {
        try {
            // Parse input values
            val current = editCurrent.text.toString().toDoubleOrNull() ?: 0.0
            val voltage = editVoltage.text.toString().toDoubleOrNull() ?: 0.0
            val battery = editBattery.text.toString().toIntOrNull() ?: 0
            
            // Parse optional values
            val rssi = if (switchIncludeRssi.isChecked) {
                editRssi.text.toString().toIntOrNull()
            } else null
            
            val snr = if (switchIncludeSnr.isChecked) {
                editSnr.text.toString().toDoubleOrNull()
            } else null
            
            // Generate payload
            val payload = PayloadTestUtil.generateTestPayload(
                current = current,
                voltage = voltage,
                battery = battery,
                rssi = rssi,
                snr = snr
            )
            
            // Display payload
            textPayload.text = "Base64 Payload: $payload"
            
            // Decode payload
            val decoded = PayloadDecoder.decodePayload(payload)
            
            // Display decoded values
            displayDecodedValues(decoded)
            
        } catch (e: Exception) {
            Timber.e(e, "Error generating or decoding payload")
            Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun displayDecodedValues(reading: DeviceReading) {
        val sb = StringBuilder()
        sb.append("Decoded Values:\n")
        sb.append("Current: ${reading.current} A\n")
        sb.append("Voltage: ${reading.voltage} V\n")
        sb.append("Battery: ${reading.batteryLevel}%\n")
        
        if (reading.rssi != null) {
            sb.append("RSSI: ${reading.rssi} dBm\n")
        }
        
        if (reading.snr != null) {
            sb.append("SNR: ${reading.snr} dB\n")
        }
        
        textDecoded.text = sb.toString()
    }
    
    private fun runDecoderTests() {
        try {
            val results = PayloadTestUtil.runDecoderTests()
            
            val sb = StringBuilder()
            sb.append("Test Results:\n")
            
            var passCount = 0
            results.forEach { (name, passed) ->
                sb.append("$name: ${if (passed) "PASS" else "FAIL"}\n")
                if (passed) passCount++
            }
            
            sb.append("\nSummary: $passCount/${results.size} tests passed")
            
            textTestResults.text = sb.toString()
            textTestResults.visibility = View.VISIBLE
            
        } catch (e: Exception) {
            Timber.e(e, "Error running decoder tests")
            Toast.makeText(this, "Error running tests: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
} 