package com.example.ampmeter.presentation.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.ampmeter.R
import com.example.ampmeter.databinding.FragmentDashboardBinding
import com.example.ampmeter.domain.model.ConnectionStatus
import com.example.ampmeter.domain.model.DeviceReading
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    private val viewModel: DashboardViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupSwipeRefresh()
        observeUiState()
    }
    
    private fun setupSwipeRefresh() {
        binding.swipeRefresh.setOnRefreshListener {
            viewModel.refreshData()
        }
    }
    
    private fun observeUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collectLatest { state ->
                    binding.textDeviceName.text = state.deviceName
                    
                    updateConnectionStatus(state.connectionStatus)
                    updateLastUpdatedTime(state.lastUpdated)
                    updateDeviceReading(state.deviceReading)
                    
                    // Handle loading state
                    binding.progressBar.visibility = if (state.isLoading) View.VISIBLE else View.GONE
                    binding.swipeRefresh.isRefreshing = state.isLoading
                    
                    // Handle error state
                    binding.textError.apply {
                        visibility = if (state.error != null) View.VISIBLE else View.GONE
                        text = state.error
                    }
                }
            }
        }
    }
    
    private fun updateConnectionStatus(status: ConnectionStatus) {
        val (color, text) = when (status) {
            ConnectionStatus.ONLINE -> {
                Pair(R.color.green, "Online")
            }
            ConnectionStatus.OFFLINE -> {
                Pair(R.color.red, "Offline")
            }
            ConnectionStatus.UNKNOWN -> {
                Pair(R.color.gray, "Unknown")
            }
        }
        
        binding.viewStatusIndicator.backgroundTintList = 
            ContextCompat.getColorStateList(requireContext(), color)
        binding.textConnectionStatus.text = text
    }
    
    private fun updateLastUpdatedTime(timestamp: Long) {
        if (timestamp == 0L) {
            binding.textLastUpdated.visibility = View.GONE
            return
        }
        
        binding.textLastUpdated.visibility = View.VISIBLE
        
        val now = System.currentTimeMillis()
        val diff = now - timestamp
        
        val text = when {
            diff < TimeUnit.MINUTES.toMillis(1) -> "Last updated: Just now"
            diff < TimeUnit.HOURS.toMillis(1) -> {
                val minutes = TimeUnit.MILLISECONDS.toMinutes(diff)
                "Last updated: $minutes ${if (minutes == 1L) "minute" else "minutes"} ago"
            }
            diff < TimeUnit.DAYS.toMillis(1) -> {
                val hours = TimeUnit.MILLISECONDS.toHours(diff)
                "Last updated: $hours ${if (hours == 1L) "hour" else "hours"} ago"
            }
            else -> {
                val formatter = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
                "Last updated: ${formatter.format(Date(timestamp))}"
            }
        }
        
        binding.textLastUpdated.text = text
    }
    
    private fun updateDeviceReading(reading: DeviceReading?) {
        if (reading == null) {
            binding.textCurrentValue.text = "-- A"
            binding.textVoltageValue.text = "-- V"
            binding.textBatteryValue.text = "--%"
            return
        }
        
        // Format current with 1 decimal place
        val currentText = String.format("%.1f A", reading.current)
        binding.textCurrentValue.text = currentText
        
        // Format voltage with 1 decimal place
        val voltageText = String.format("%.1f V", reading.voltage)
        binding.textVoltageValue.text = voltageText
        
        // Format battery percentage
        val batteryText = String.format("%d%%", reading.batteryLevel)
        binding.textBatteryValue.text = batteryText
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 