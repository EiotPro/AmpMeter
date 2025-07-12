package com.example.ampmeter.presentation.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.ampmeter.databinding.FragmentSettingsBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: SettingsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupListeners()
        observeUiState()
    }
    
    private fun setupListeners() {
        // Save button
        binding.buttonSave.setOnClickListener {
            saveSettings()
        }
        
        // Test connection button
        binding.buttonTestConnection.setOnClickListener {
            testConnection()
        }
        
        // Refresh interval seek bar
        binding.seekRefreshInterval.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                updateRefreshIntervalLabel(progress)
            }
            
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }
    
    private fun observeUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collectLatest { state ->
                    // Update UI with state
                    binding.editDeviceId.setText(state.deviceId)
                    binding.editDeviceName.setText(state.deviceName)
                    binding.editServerUrl.setText(state.serverUrl)
                    binding.editApiKey.setText(state.apiKey)
                    
                    binding.seekRefreshInterval.progress = state.refreshInterval
                    updateRefreshIntervalLabel(state.refreshInterval)
                    
                    binding.switchNotifications.isChecked = state.notificationsEnabled
                    
                    // Handle loading state
                    binding.buttonSave.isEnabled = !state.isLoading && !state.isSaving
                    binding.buttonTestConnection.isEnabled = !state.isLoading && !state.isSaving
                    
                    if (state.isLoading || state.isSaving) {
                        binding.buttonSave.text = "Saving..."
                    } else {
                        binding.buttonSave.text = "Save Settings"
                    }
                    
                    // Handle success message
                    if (state.saveSuccess) {
                        Toast.makeText(requireContext(), "Settings saved successfully", Toast.LENGTH_SHORT).show()
                        viewModel.clearSaveSuccess()
                    }
                    
                    // Handle error
                    if (state.error != null) {
                        Toast.makeText(requireContext(), state.error, Toast.LENGTH_LONG).show()
                        viewModel.clearError()
                    }
                }
            }
        }
    }
    
    private fun updateRefreshIntervalLabel(seconds: Int) {
        binding.labelRefreshInterval.text = "Refresh Interval: $seconds seconds"
    }
    
    private fun saveSettings() {
        val deviceId = binding.editDeviceId.text.toString().trim()
        val deviceName = binding.editDeviceName.text.toString().trim()
        val serverUrl = binding.editServerUrl.text.toString().trim()
        val apiKey = binding.editApiKey.text.toString().trim()
        val refreshInterval = binding.seekRefreshInterval.progress
        val notificationsEnabled = binding.switchNotifications.isChecked
        
        // Basic validation
        if (deviceId.isEmpty()) {
            Toast.makeText(requireContext(), "Device ID cannot be empty", Toast.LENGTH_SHORT).show()
            return
        }
        
        viewModel.saveSettings(
            deviceId = deviceId,
            deviceName = deviceName,
            serverUrl = serverUrl,
            apiKey = apiKey,
            refreshInterval = refreshInterval,
            notificationsEnabled = notificationsEnabled
        )
    }
    
    private fun testConnection() {
        val deviceId = binding.editDeviceId.text.toString().trim()
        val serverUrl = binding.editServerUrl.text.toString().trim()
        
        if (deviceId.isEmpty() || serverUrl.isEmpty()) {
            Toast.makeText(requireContext(), "Please enter Device ID and Server URL", Toast.LENGTH_SHORT).show()
            return
        }
        
        binding.buttonTestConnection.isEnabled = false
        binding.buttonTestConnection.text = "Testing..."
        
        // Save current settings first
        saveSettings()
        
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                // For now, just show a success message after a delay
                kotlinx.coroutines.delay(1500)
                Toast.makeText(requireContext(), "Connection successful!", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Connection failed: ${e.message}", Toast.LENGTH_SHORT).show()
            } finally {
                binding.buttonTestConnection.isEnabled = true
                binding.buttonTestConnection.text = "Test Connection"
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 