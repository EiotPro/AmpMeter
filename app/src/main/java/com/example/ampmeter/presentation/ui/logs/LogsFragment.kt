package com.example.ampmeter.presentation.ui.logs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ampmeter.databinding.FragmentLogsBinding
import com.example.ampmeter.domain.model.DeviceReading
import com.example.ampmeter.domain.usecase.device.GetDeviceReadingsUseCase
import com.example.ampmeter.domain.usecase.settings.GetSettingsUseCase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class LogsFragment : Fragment() {

    private var _binding: FragmentLogsBinding? = null
    private val binding get() = _binding!!
    
    @Inject
    lateinit var getDeviceReadingsUseCase: GetDeviceReadingsUseCase
    
    @Inject
    lateinit var getSettingsUseCase: GetSettingsUseCase
    
    private lateinit var readingsAdapter: DeviceReadingsAdapter
    private var currentPage = 0
    private val pageSize = 20

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLogsBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupRecyclerView()
        setupSwipeRefresh()
        setupButtons()
        loadReadings()
    }
    
    private fun setupRecyclerView() {
        readingsAdapter = DeviceReadingsAdapter { reading ->
            // Handle item click
            showReadingDetails(reading)
        }
        
        binding.recyclerLogs.apply {
            adapter = readingsAdapter
            addItemDecoration(DividerItemDecoration(context, LinearLayoutManager.VERTICAL))
        }
    }
    
    private fun setupSwipeRefresh() {
        binding.swipeRefresh.setOnRefreshListener {
            currentPage = 0
            loadReadings()
        }
    }
    
    private fun setupButtons() {
        binding.buttonFilter.setOnClickListener {
            showFilterDialog()
        }
        
        binding.fabExport.setOnClickListener {
            exportReadings()
        }
    }
    
    private fun loadReadings() {
        viewLifecycleOwner.lifecycleScope.launch {
            binding.progressBar.visibility = View.VISIBLE
            
            try {
                val deviceId = getSettingsUseCase.getDeviceId()
                
                if (deviceId.isBlank()) {
                    showError("Device ID not set. Please configure in settings.")
                    return@launch
                }
                
                val result = getDeviceReadingsUseCase(deviceId, pageSize, currentPage * pageSize)
                
                result.onSuccess { readings ->
                    if (readings.isEmpty() && currentPage == 0) {
                        binding.textEmpty.visibility = View.VISIBLE
                        binding.recyclerLogs.visibility = View.GONE
                    } else {
                        binding.textEmpty.visibility = View.GONE
                        binding.recyclerLogs.visibility = View.VISIBLE
                        
                        if (currentPage == 0) {
                            readingsAdapter.submitList(readings)
                        } else {
                            val currentList = readingsAdapter.currentList.toMutableList()
                            currentList.addAll(readings)
                            readingsAdapter.submitList(currentList)
                        }
                    }
                }.onError { message, _ ->
                    showError(message)
                }
                
            } catch (e: Exception) {
                showError("Error loading readings: ${e.message}")
            } finally {
                binding.progressBar.visibility = View.GONE
                binding.swipeRefresh.isRefreshing = false
            }
        }
    }
    
    private fun showError(message: String) {
        binding.textEmpty.text = message
        binding.textEmpty.visibility = View.VISIBLE
        binding.recyclerLogs.visibility = View.GONE
    }
    
    private fun showReadingDetails(reading: DeviceReading) {
        // Format timestamp
        val dateFormat = SimpleDateFormat("MMM dd, yyyy HH:mm:ss", Locale.getDefault())
        val dateStr = dateFormat.format(Date(reading.timestamp))
        
        val message = """
            Time: $dateStr
            Current: ${reading.current} A
            Voltage: ${reading.voltage} V
            Battery: ${reading.batteryLevel}%
            RSSI: ${reading.rssi ?: "N/A"}
            SNR: ${reading.snr ?: "N/A"}
            Frame Count: ${reading.frameCount ?: "N/A"}
        """.trimIndent()
        
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
        // In a real app, you'd show a dialog or navigate to a details screen
    }
    
    private fun showFilterDialog() {
        // In a real app, show a dialog with filter options
        Toast.makeText(requireContext(), "Filter functionality coming soon", Toast.LENGTH_SHORT).show()
    }
    
    private fun exportReadings() {
        // In a real app, implement export functionality
        Toast.makeText(requireContext(), "Export functionality coming soon", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 