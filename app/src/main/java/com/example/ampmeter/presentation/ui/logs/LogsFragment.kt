package com.example.ampmeter.presentation.ui.logs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ampmeter.databinding.FragmentLogsBinding
import com.example.ampmeter.domain.model.DeviceReading
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LogsFragment : Fragment() {

    private var _binding: FragmentLogsBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: LogsViewModel by viewModels()
    
    private lateinit var readingsAdapter: DeviceReadingsAdapter

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
        observeUiState()
    }
    
    private fun setupRecyclerView() {
        readingsAdapter = DeviceReadingsAdapter { reading ->
            // Handle item click
            showReadingDetails(reading)
        }
        
        binding.recyclerLogs.apply {
            adapter = readingsAdapter
            addItemDecoration(DividerItemDecoration(context, LinearLayoutManager.VERTICAL))
            
            // Add scroll listener for pagination
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    
                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                    val visibleItemCount = layoutManager.childCount
                    val totalItemCount = layoutManager.itemCount
                    val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
                    
                    // Load more when reaching near the end of the list
                    if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount - 5
                        && firstVisibleItemPosition >= 0) {
                        viewModel.loadMore()
                    }
                }
            })
        }
    }
    
    private fun setupSwipeRefresh() {
        binding.swipeRefresh.setOnRefreshListener {
            viewModel.refresh()
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
    
    private fun observeUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collectLatest { state ->
                    // Update loading state
                    binding.progressBar.visibility = if (state.isLoading) View.VISIBLE else View.GONE
                    binding.swipeRefresh.isRefreshing = state.isLoading
                    
                    // Update readings list
                    readingsAdapter.submitList(state.readings)
                    
                    // Show empty view if no readings and not loading
                    binding.textEmpty.visibility = if (state.readings.isEmpty() && !state.isLoading) {
                        View.VISIBLE
                    } else {
                        View.GONE
                    }
                    
                    binding.recyclerLogs.visibility = if (state.readings.isNotEmpty()) {
                        View.VISIBLE
                    } else {
                        View.GONE
                    }
                    
                    // Handle error
                    if (state.error != null) {
                        binding.textEmpty.text = state.error
                        binding.textEmpty.visibility = View.VISIBLE
                        Toast.makeText(requireContext(), state.error, Toast.LENGTH_SHORT).show()
                        viewModel.clearError()
                    } else if (state.readings.isEmpty() && !state.isLoading) {
                        binding.textEmpty.text = "No readings available"
                    }
                }
            }
        }
    }
    
    private fun showReadingDetails(reading: DeviceReading) {
        // In a real app, navigate to a detail screen or show a dialog
        Toast.makeText(
            requireContext(),
            "Reading: ${reading.current}A at ${reading.timestamp}",
            Toast.LENGTH_SHORT
        ).show()
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