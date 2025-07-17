package com.example.ampmeter.presentation.ui.settings

import android.Manifest
import android.bluetooth.BluetoothDevice
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ampmeter.databinding.FragmentSettingsBinding
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import com.example.ampmeter.R

@AndroidEntryPoint
class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: SettingsViewModel by viewModels()
    
    private val bleDevicesAdapter = BleDevicesAdapter { device ->
        connectToBleDevice(device)
    }
    
    // Permission launcher for Bluetooth scan
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.entries.all { it.value }
        if (allGranted) {
            viewModel.startBleScan()
        } else {
            Toast.makeText(
                requireContext(),
                "Bluetooth scanning requires location permissions",
                Toast.LENGTH_LONG
            ).show()
        }
    }

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
        
        setupConnectivityTabs()
        setupBleDevicesList()
        setupListeners()
        setupDropdowns()
        observeUiState()
    }
    
    private fun setupDropdowns() {
        // Setup LoRaWAN Region Dropdown
        val lorawanRegions = arrayOf("EU868", "US915", "AU915", "IN865", "AS923")
        val regionAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, lorawanRegions)
        binding.dropdownLorawanRegion.setAdapter(regionAdapter)
        binding.dropdownLorawanRegion.setText(lorawanRegions[3], false)  // Default to IN865 (index 3)
        
        // Setup Data Format Dropdown
        val dataFormats = arrayOf("JSON", "COMPACT")
        val dataFormatAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, dataFormats)
        binding.dropdownDataFormat.setAdapter(dataFormatAdapter)
        binding.dropdownDataFormat.setText(dataFormats[0], false)  // Default to JSON
        
        // Setup Debug Level Dropdown
        val debugLevels = arrayOf("OFF", "ERROR", "INFO", "DEBUG", "VERBOSE")
        val debugLevelAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, debugLevels)
        binding.dropdownDebugLevel.setAdapter(debugLevelAdapter)
        binding.dropdownDebugLevel.setText(debugLevels[2], false)  // Default to INFO (2)
    }
    
    private fun setupConnectivityTabs() {
        // Set the first tab as selected
        binding.connectivityTabs.selectTab(binding.connectivityTabs.getTabAt(0))
        
        binding.connectivityTabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                // Animate visibility changes
                when (tab.position) {
                    0 -> { // ChirpStack
                        showTabContent(binding.chirpstackSettings, binding.mqttSettings, binding.bluetoothSettings, 
                            binding.lorawanSettings, binding.wifiSettings, binding.otaSettings, binding.advancedSettings)
                    }
                    1 -> { // MQTT
                        showTabContent(binding.mqttSettings, binding.chirpstackSettings, binding.bluetoothSettings,
                            binding.lorawanSettings, binding.wifiSettings, binding.otaSettings, binding.advancedSettings)
                    }
                    2 -> { // Bluetooth
                        showTabContent(binding.bluetoothSettings, binding.chirpstackSettings, binding.mqttSettings,
                            binding.lorawanSettings, binding.wifiSettings, binding.otaSettings, binding.advancedSettings)
                    }
                    3 -> { // LoRaWAN
                        showTabContent(binding.lorawanSettings, binding.chirpstackSettings, binding.mqttSettings, 
                            binding.bluetoothSettings, binding.wifiSettings, binding.otaSettings, binding.advancedSettings)
                    }
                    4 -> { // WiFi
                        showTabContent(binding.wifiSettings, binding.chirpstackSettings, binding.mqttSettings,
                            binding.bluetoothSettings, binding.lorawanSettings, binding.otaSettings, binding.advancedSettings)
                    }
                    5 -> { // OTA
                        showTabContent(binding.otaSettings, binding.chirpstackSettings, binding.mqttSettings,
                            binding.bluetoothSettings, binding.lorawanSettings, binding.wifiSettings, binding.advancedSettings)
                    }
                    6 -> { // Advanced
                        showTabContent(binding.advancedSettings, binding.chirpstackSettings, binding.mqttSettings,
                            binding.bluetoothSettings, binding.lorawanSettings, binding.wifiSettings, binding.otaSettings)
                    }
                }
            }
            
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }
    
    private fun showTabContent(showView: View, vararg hideViews: View) {
        // First hide all other views with animation
        hideViews.forEach { view ->
            if (view.visibility == View.VISIBLE) {
                view.animate()
                    .alpha(0f)
                    .setDuration(150)
                    .withEndAction {
                        view.visibility = View.GONE
                        view.alpha = 1f
                    }
                    .start()
            }
        }
        
        // Then show the selected view with animation
        if (showView.visibility != View.VISIBLE) {
            showView.alpha = 0f
            showView.visibility = View.VISIBLE
            showView.animate()
                .alpha(1f)
                .setDuration(150)
                .start()
        }
    }
    
    private fun setupBleDevicesList() {
        binding.recyclerBleDevices.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = bleDevicesAdapter
        }
    }
    
    private fun setupListeners() {
        // Save button
        binding.buttonSave.setOnClickListener {
            saveSettings()
        }
        
        // Test ChirpStack connection button
        binding.buttonTestConnection.setOnClickListener {
            testChirpStackConnection()
        }
        
        // Connect to MQTT broker button
        binding.buttonConnectMqtt.setOnClickListener {
            connectToMqttBroker()
        }
        
        // Scan for BLE devices button
        binding.buttonScanBle.setOnClickListener {
            checkBluetoothPermissions()
        }
        
        // Disconnect BLE button
        binding.buttonDisconnectBle.setOnClickListener {
            viewModel.disconnectBle()
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
                launch {
                    viewModel.uiState.collectLatest { state ->
                        // Update UI with state
                        // Device settings
                        binding.editDeviceId.setText(state.deviceId)
                        binding.editDeviceName.setText(state.deviceName)
                        
                        // ChirpStack settings
                        binding.editServerUrl.setText(state.serverUrl)
                        binding.editApiKey.setText(state.apiKey)
                        
                        // MQTT settings
                        binding.editMqttBroker.setText(state.mqttBrokerUrl)
                        binding.editMqttUsername.setText(state.mqttUsername)
                        binding.editMqttPassword.setText(state.mqttPassword)
                        binding.editMqttTopic.setText(state.mqttTopic)
                        
                        // LoRaWAN settings
                        binding.editLorawanDevAddr.setText(state.lorawanDevAddr)
                        binding.editLorawanNwksKey.setText(state.lorawanNwksKey)
                        binding.editLorawanAppsKey.setText(state.lorawanAppsKey)
                        // TODO: Set up region dropdown
                        binding.editTxInterval.setText(state.txInterval.toString())
                        
                        // WiFi settings
                        binding.switchWifiEnabled.isChecked = state.wifiEnabled
                        binding.editWifiSsid.setText(state.wifiSsid)
                        binding.editWifiPassword.setText(state.wifiPassword)
                        binding.editWifiTxInterval.setText(state.wifiTxInterval.toString())
                        
                        // OTA settings
                        binding.switchOtaEnabled.isChecked = state.otaEnabled
                        binding.editOtaServerUrl.setText(state.otaServerUrl)
                        binding.editOtaUsername.setText(state.otaHttpUsername)
                        binding.editOtaPassword.setText(state.otaHttpPassword)
                        
                        // Sensor settings
                        binding.editWcs6800Sensitivity.setText(state.wcs6800Sensitivity.toString())
                        binding.editWcs6800OffsetVoltage.setText(state.wcs6800OffsetVoltage.toString())
                        binding.editAdcRefVoltage.setText(state.adcRefVoltage.toString())
                        
                        // Advanced settings
                        binding.switchAdaptiveSampling.isChecked = state.useAdaptiveSamplingRate
                        binding.editMeasurementInterval.setText(state.measurementInterval.toString())
                        // TODO: Set up data format dropdown
                        // TODO: Set up debug level dropdown
                        
                        // App settings
                        binding.seekRefreshInterval.progress = state.refreshInterval
                        updateRefreshIntervalLabel(state.refreshInterval)
                        binding.switchNotifications.isChecked = state.notificationsEnabled
                        
                        // Handle loading state
                        binding.buttonSave.isEnabled = !state.isLoading && !state.isSaving && !state.isTestingConnection
                        binding.buttonTestConnection.isEnabled = !state.isLoading && !state.isSaving && !state.isTestingConnection
                        binding.buttonConnectMqtt.isEnabled = !state.isConnectingMqtt
                        binding.buttonScanBle.isEnabled = !state.isScanningBle
                        
                        if (state.isLoading || state.isSaving) {
                            binding.buttonSave.text = "Saving..."
                        } else {
                            binding.buttonSave.text = "Save Settings"
                        }
                        
                        if (state.isTestingConnection) {
                            binding.buttonTestConnection.text = "Testing..."
                        } else {
                            binding.buttonTestConnection.text = "Test Connection"
                        }
                        
                        if (state.isConnectingMqtt) {
                            binding.buttonConnectMqtt.text = "Connecting..."
                        } else if (state.isMqttConnected) {
                            binding.buttonConnectMqtt.text = "Disconnect"
                        } else {
                            binding.buttonConnectMqtt.text = "Connect to MQTT Broker"
                        }
                        
                        if (state.isScanningBle) {
                            binding.buttonScanBle.text = "Scanning..."
                        } else {
                            binding.buttonScanBle.text = "Scan for BLE Devices"
                        }
                        
                        // Update BLE connection status
                        binding.textBleStatus.text = when (state.bleConnectionState) {
                            0 -> "Disconnected"
                            1 -> "Connecting..."
                            2 -> "Connected"
                            else -> "Unknown"
                        }
                        
                        // Enable/disable disconnect button based on BLE state
                        binding.buttonDisconnectBle.isEnabled = state.bleConnectionState > 0
                        
                        // Handle success message
                        if (state.saveSuccess) {
                            Toast.makeText(requireContext(), "Settings saved successfully", Toast.LENGTH_SHORT).show()
                            viewModel.clearSaveSuccess()
                        }
                        
                        // Handle connection test result
                        if (state.connectionTestResult != null) {
                            showConnectionTestResult(state.connectionTestResult)
                            viewModel.clearConnectionTestResult()
                        }
                        
                        // Handle MQTT connection result
                        if (state.mqttConnectionResult != null) {
                            showMqttConnectionResult(state.mqttConnectionResult)
                            viewModel.clearMqttConnectionResult()
                        }
                        
                        // Handle error
                        if (state.error != null) {
                            Toast.makeText(requireContext(), state.error, Toast.LENGTH_LONG).show()
                            viewModel.clearError()
                        }
                    }
                }
                
                // Observe BLE devices
                launch {
                    viewModel.bleDevices.collectLatest { devices ->
                        bleDevicesAdapter.updateDevices(devices)
                    }
                }
            }
        }
    }
    
    private fun updateRefreshIntervalLabel(seconds: Int) {
        binding.labelRefreshInterval.text = "Refresh Interval: $seconds seconds"
    }
    
    private fun saveSettings() {
        // Device settings
        val deviceId = binding.editDeviceId.text.toString().trim()
        val deviceName = binding.editDeviceName.text.toString().trim()
        
        // ChirpStack settings
        val serverUrl = binding.editServerUrl.text.toString().trim()
        val apiKey = binding.editApiKey.text.toString().trim()
        
        // MQTT settings
        val mqttBrokerUrl = binding.editMqttBroker.text.toString().trim()
        val mqttUsername = binding.editMqttUsername.text.toString().trim()
        val mqttPassword = binding.editMqttPassword.text.toString().trim()
        val mqttTopic = binding.editMqttTopic.text.toString().trim()
        val mqttPort = 1883  // Default MQTT port
        val mqttEnabled = mqttBrokerUrl.isNotEmpty()
        
        // LoRaWAN settings
        val lorawanDevAddr = binding.editLorawanDevAddr.text.toString().trim()
        val lorawanNwksKey = binding.editLorawanNwksKey.text.toString().trim()
        val lorawanAppsKey = binding.editLorawanAppsKey.text.toString().trim()
        val lorawanRegion = when (binding.dropdownLorawanRegion.text.toString()) {
            "EU868" -> 0
            "US915" -> 1
            "AU915" -> 2
            "IN865" -> 3
            "AS923" -> 4
            else -> 3  // Default to IN865
        }
        val txInterval = binding.editTxInterval.text.toString().takeIf { it.isNotEmpty() }?.toIntOrNull() ?: 60000
        
        // WiFi settings
        val wifiEnabled = binding.switchWifiEnabled.isChecked
        val wifiSsid = binding.editWifiSsid.text.toString().trim()
        val wifiPassword = binding.editWifiPassword.text.toString().trim()
        val wifiTxInterval = binding.editWifiTxInterval.text.toString().takeIf { it.isNotEmpty() }?.toIntOrNull() ?: 10000
        
        // BLE settings
        val bleEnabled = true  // Default BLE to enabled
        val bleDeviceName = deviceName.takeIf { it.isNotEmpty() } ?: "AmpMeter_Device"
        val bleTxInterval = 5000  // Default BLE transmission interval
        
        // OTA settings
        val otaEnabled = binding.switchOtaEnabled.isChecked
        val otaServerUrl = binding.editOtaServerUrl.text.toString().trim()
        val otaHttpUsername = binding.editOtaUsername.text.toString().trim()
        val otaHttpPassword = binding.editOtaPassword.text.toString().trim()
        
        // Sensor calibration
        val wcs6800Sensitivity = binding.editWcs6800Sensitivity.text.toString().takeIf { it.isNotEmpty() }?.toFloatOrNull() ?: 0.0429f
        val wcs6800OffsetVoltage = binding.editWcs6800OffsetVoltage.text.toString().takeIf { it.isNotEmpty() }?.toFloatOrNull() ?: 1.65f
        val adcRefVoltage = binding.editAdcRefVoltage.text.toString().takeIf { it.isNotEmpty() }?.toFloatOrNull() ?: 3.3f
        
        // Advanced settings
        val debugLevel = when (binding.dropdownDebugLevel.text.toString()) {
            "OFF" -> 0
            "ERROR" -> 1
            "INFO" -> 2
            "DEBUG" -> 3
            "VERBOSE" -> 4
            else -> 2  // Default to INFO
        }
        val useAdaptiveSamplingRate = binding.switchAdaptiveSampling.isChecked
        val measurementInterval = binding.editMeasurementInterval.text.toString().takeIf { it.isNotEmpty() }?.toIntOrNull() ?: 1000
        val dataFormat = binding.dropdownDataFormat.text.toString()
        
        // App settings
        val refreshInterval = binding.seekRefreshInterval.progress
        val notificationsEnabled = binding.switchNotifications.isChecked
        
        // Basic validation
        if (deviceId.isEmpty()) {
            Toast.makeText(requireContext(), "Device ID cannot be empty", Toast.LENGTH_SHORT).show()
            return
        }
        
        // Show hint about server URL format, but still allow saving
        if (serverUrl.isNotEmpty() && (serverUrl.startsWith("http://") || serverUrl.startsWith("https://"))) {
            Toast.makeText(requireContext(), "Info: URL scheme will be handled automatically, you can just enter IP:port format", Toast.LENGTH_LONG).show()
        }
        
        viewModel.saveSettings(
            // Device settings
            deviceId = deviceId,
            deviceName = deviceName,
            
            // ChirpStack settings
            serverUrl = serverUrl,
            apiKey = apiKey,
            
            // MQTT settings
            mqttBrokerUrl = mqttBrokerUrl,
            mqttPort = mqttPort,
            mqttUsername = mqttUsername.takeIf { it.isNotEmpty() },
            mqttPassword = mqttPassword.takeIf { it.isNotEmpty() },
            mqttTopic = mqttTopic,
            mqttEnabled = mqttEnabled,
            
            // WiFi settings
            wifiEnabled = wifiEnabled,
            wifiSsid = wifiSsid,
            wifiPassword = wifiPassword,
            wifiTxInterval = wifiTxInterval,
            
            // BLE settings
            bleEnabled = bleEnabled,
            bleDeviceName = bleDeviceName,
            bleTxInterval = bleTxInterval,
            
            // LoRaWAN settings
            lorawanDevAddr = lorawanDevAddr,
            lorawanNwksKey = lorawanNwksKey,
            lorawanAppsKey = lorawanAppsKey,
            lorawanRegion = lorawanRegion,
            txInterval = txInterval,
            
            // OTA settings
            otaEnabled = otaEnabled,
            otaServerUrl = otaServerUrl,
            otaHttpUsername = otaHttpUsername,
            otaHttpPassword = otaHttpPassword,
            
            // Sensor configuration
            wcs6800Sensitivity = wcs6800Sensitivity,
            wcs6800OffsetVoltage = wcs6800OffsetVoltage,
            adcRefVoltage = adcRefVoltage,
            
            // Advanced settings
            debugLevel = debugLevel,
            useAdaptiveSamplingRate = useAdaptiveSamplingRate,
            measurementInterval = measurementInterval,
            dataFormat = dataFormat,
            
            // App settings
            refreshInterval = refreshInterval,
            notificationsEnabled = notificationsEnabled
        )
    }
    
    private fun testChirpStackConnection() {
        // Save settings first to ensure we're testing with the latest values
        saveSettings()
        
        // Then test the connection
        viewModel.testConnection()
    }
    
    private fun connectToMqttBroker() {
        val state = viewModel.uiState.value
        
        if (state.isMqttConnected) {
            // If already connected, disconnect
            viewModel.disconnectMqtt()
        } else {
            // Otherwise connect
            val brokerUrl = binding.editMqttBroker.text.toString().trim()
            val username = binding.editMqttUsername.text.toString().trim().takeIf { it.isNotEmpty() }
            val password = binding.editMqttPassword.text.toString().trim().takeIf { it.isNotEmpty() }
            val topic = binding.editMqttTopic.text.toString().trim()
            
            if (brokerUrl.isEmpty()) {
                Toast.makeText(requireContext(), "MQTT Broker URL cannot be empty", Toast.LENGTH_SHORT).show()
                return
            }
            
            viewModel.connectToMqttBroker(brokerUrl, username, password, topic)
        }
    }
    
    private fun checkBluetoothPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val bluetoothPermissions = arrayOf(
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.BLUETOOTH_CONNECT,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            
            val hasPermissions = bluetoothPermissions.all {
                ContextCompat.checkSelfPermission(requireContext(), it) == PackageManager.PERMISSION_GRANTED
            }
            
            if (hasPermissions) {
                viewModel.startBleScan()
            } else {
                requestPermissionLauncher.launch(bluetoothPermissions)
            }
        } else {
            // For older Android versions
            val bluetoothPermissions = arrayOf(
                Manifest.permission.BLUETOOTH,
                Manifest.permission.BLUETOOTH_ADMIN,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            
            val hasPermissions = bluetoothPermissions.all {
                ContextCompat.checkSelfPermission(requireContext(), it) == PackageManager.PERMISSION_GRANTED
            }
            
            if (hasPermissions) {
                viewModel.startBleScan()
            } else {
                requestPermissionLauncher.launch(bluetoothPermissions)
            }
        }
    }
    
    private fun connectToBleDevice(device: BluetoothDevice) {
        viewModel.connectToBleDevice(device)
    }
    
    private fun showConnectionTestResult(result: String) {
        Snackbar.make(binding.root, result, Snackbar.LENGTH_LONG).show()
    }
    
    private fun showMqttConnectionResult(result: String) {
        Snackbar.make(binding.root, result, Snackbar.LENGTH_LONG).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 

/**
 * Adapter for displaying Bluetooth devices in a RecyclerView.
 */
class BleDevicesAdapter(
    private val onDeviceClick: (BluetoothDevice) -> Unit
) : androidx.recyclerview.widget.RecyclerView.Adapter<BleDevicesAdapter.ViewHolder>() {
    
    private val devices = mutableListOf<BluetoothDevice>()
    
    fun updateDevices(newDevices: Map<String, BluetoothDevice>) {
        devices.clear()
        devices.addAll(newDevices.values)
        notifyDataSetChanged()
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.item_ble_device, parent, false
        )
        return ViewHolder(view, onDeviceClick)
    }
    
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val device = devices[position]
        holder.bind(device)
    }
    
    override fun getItemCount(): Int = devices.size
    
    class ViewHolder(view: View, private val onDeviceClick: (BluetoothDevice) -> Unit) : androidx.recyclerview.widget.RecyclerView.ViewHolder(view) {
        private val deviceName: TextView = view.findViewById(R.id.text_device_name)
        private val deviceAddress: TextView = view.findViewById(R.id.text_device_address)
        private val connectButton: View = view.findViewById(R.id.button_connect)
        private lateinit var device: BluetoothDevice
        
        init {
            // Set click listener on the connect button
            connectButton.setOnClickListener {
                if (::device.isInitialized) {
                    onDeviceClick(device)
                }
            }
            
            // Set click listener on the whole item view
            itemView.setOnClickListener {
                if (::device.isInitialized) {
                    onDeviceClick(device)
                }
            }
        }
        
        fun bind(device: BluetoothDevice) {
            this.device = device
            deviceName.text = device.name ?: "Unknown Device"
            deviceAddress.text = device.address
        }
    }
} 