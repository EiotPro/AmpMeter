package com.example.ampmeter.data.remote.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Base64
import com.example.ampmeter.domain.model.DeviceReading
import com.example.ampmeter.util.PayloadDecoder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import org.eclipse.paho.client.mqttv3.*
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence
import timber.log.Timber
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Service for MQTT client functionality to receive data directly from the transmitter via WiFi.
 * This implementation uses the direct Paho MQTT Client without the AndroidService wrapper to avoid
 * dependency issues with AndroidX.
 */
class MqttClientService : Service() {
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val binder = MqttBinder()
    
    // MQTT client
    private var mqttClient: MqttClient? = null
    private val isConnected = AtomicBoolean(false)
    
    // Connection parameters
    private var brokerUrl = ""
    private var clientId = ""
    private var username: String? = null
    private var password: String? = null
    private var subscriptionTopics = listOf<String>()
    
    // Reading data flow
    private val _deviceReadings = MutableSharedFlow<DeviceReading>(replay = 0)
    val deviceReadings = _deviceReadings.asSharedFlow()
    
    override fun onCreate() {
        super.onCreate()
        Timber.d("MQTT Service created")
    }
    
    override fun onBind(intent: Intent?): IBinder {
        return binder
    }
    
    /**
     * Connect to MQTT broker with the given parameters.
     */
    fun connect(
        brokerUrl: String,
        clientId: String = "AmpMeter_" + UUID.randomUUID().toString().substring(0, 8),
        username: String? = null,
        password: String? = null
    ) {
        if (isConnected.get()) {
            Timber.d("Already connected to MQTT broker")
            return
        }
        
        this.brokerUrl = brokerUrl
        this.clientId = clientId
        this.username = username
        this.password = password
        
        serviceScope.launch(Dispatchers.IO) {
            try {
                // Format the broker URL properly
                var formattedBrokerUrl = brokerUrl
                if (!formattedBrokerUrl.startsWith("tcp://") && !formattedBrokerUrl.startsWith("ssl://")) {
                    formattedBrokerUrl = "tcp://$formattedBrokerUrl"
                }
                
                // Add port if not specified
                if (!formattedBrokerUrl.contains(":")) {
                    formattedBrokerUrl = "$formattedBrokerUrl:1883"  // Default MQTT port
                }
                
                Timber.d("Connecting to MQTT broker: $formattedBrokerUrl")
                
                // Use MemoryPersistence for temporary client
                val persistence = MemoryPersistence()
                mqttClient = MqttClient(formattedBrokerUrl, clientId, persistence)
                
                val options = MqttConnectOptions().apply {
                    isCleanSession = true
                    isAutomaticReconnect = true
                    connectionTimeout = 10
                    
                    // Set credentials if provided
                    if (!username.isNullOrEmpty() && !password.isNullOrEmpty()) {
                        this.userName = username
                        this.password = password.toCharArray()
                    }
                }
                
                mqttClient?.setCallback(object : MqttCallbackExtended {
                    override fun connectComplete(reconnect: Boolean, serverURI: String) {
                        Timber.d("Connection complete to $serverURI, reconnect: $reconnect")
                        isConnected.set(true)
                        
                        // Subscribe to topics after connection
                        if (subscriptionTopics.isNotEmpty()) {
                            serviceScope.launch(Dispatchers.IO) {
                                subscribeToTopics(subscriptionTopics)
                            }
                        }
                    }
                    
                    override fun connectionLost(cause: Throwable?) {
                        Timber.e(cause, "MQTT connection lost")
                        isConnected.set(false)
                    }
                    
                    override fun messageArrived(topic: String, message: MqttMessage) {
                        Timber.d("Message arrived on topic $topic: ${message.payload}")
                        
                        try {
                            val payload = String(message.payload)
                            
                            // Check if payload is JSON or binary data
                            if (payload.startsWith("{") && payload.endsWith("}")) {
                                // Process JSON data (expected format from WiFi transmission)
                                processJsonPayload(payload, topic)
                            } else {
                                // Process as binary payload (same format as LoRaWAN)
                                processBinaryPayload(message.payload, topic)
                            }
                        } catch (e: Exception) {
                            Timber.e(e, "Error processing MQTT message")
                        }
                    }
                    
                    override fun deliveryComplete(token: IMqttDeliveryToken?) {
                        Timber.d("Message delivery complete")
                    }
                })
                
                // Connect to broker
                mqttClient?.connect(options)
                
                Timber.d("Connected to MQTT broker")
                
            } catch (e: MqttException) {
                Timber.e(e, "MQTT Exception: ${e.reasonCode}, ${e.message}")
            } catch (e: Exception) {
                Timber.e(e, "Error connecting to MQTT broker")
            }
        }
    }
    
    /**
     * Subscribe to the specified topics.
     */
    fun subscribeToTopics(topics: List<String>) {
        subscriptionTopics = topics
        
        if (!isConnected.get() || mqttClient == null) {
            Timber.d("Not connected, topics will be subscribed upon connection")
            return
        }
        
        serviceScope.launch(Dispatchers.IO) {
            topics.forEach { topic ->
                try {
                    mqttClient?.subscribe(topic, 1)
                    Timber.d("Subscribed to topic: $topic")
                } catch (e: MqttException) {
                    Timber.e(e, "MQTT Exception when subscribing to topic: $topic, reason: ${e.reasonCode}")
                } catch (e: Exception) {
                    Timber.e(e, "Error subscribing to topic: $topic")
                }
            }
        }
    }
    
    /**
     * Process JSON payload from WiFi/MQTT transmission.
     */
    private fun processJsonPayload(jsonPayload: String, topic: String) {
        serviceScope.launch {
            try {
                // Extract device ID from topic, e.g., "ampmeter/device/123456/data"
                val deviceId = extractDeviceIdFromTopic(topic)
                
                // Parse JSON data
                // This is a simplified implementation. In a real app, you'd use a proper JSON parser
                val current = extractDoubleValue(jsonPayload, "\"current\":")
                val voltage = extractDoubleValue(jsonPayload, "\"voltage\":")
                val battery = extractIntValue(jsonPayload, "\"battery\":")
                val timestamp = System.currentTimeMillis()
                
                if (current != null && voltage != null && battery != null) {
                    val deviceReading = DeviceReading(
                        id = UUID.randomUUID().toString(),
                        deviceId = deviceId,
                        current = current,
                        voltage = voltage,
                        batteryLevel = battery,
                        timestamp = timestamp,
                        rawData = jsonPayload,
                        isSynced = true
                    )
                    
                    _deviceReadings.emit(deviceReading)
                }
            } catch (e: Exception) {
                Timber.e(e, "Error processing JSON payload")
            }
        }
    }
    
    /**
     * Process binary payload (same format as LoRaWAN).
     */
    private fun processBinaryPayload(payload: ByteArray, topic: String) {
        serviceScope.launch {
            try {
                // Extract device ID from topic
                val deviceId = extractDeviceIdFromTopic(topic)
                
                // Convert binary to base64 for PayloadDecoder
                val base64Data = Base64.encodeToString(payload, Base64.DEFAULT)
                
                // Use existing PayloadDecoder to parse the data
                val deviceReading = PayloadDecoder.decodePayload(base64Data, deviceId)
                
                _deviceReadings.emit(deviceReading)
            } catch (e: Exception) {
                Timber.e(e, "Error processing binary payload")
            }
        }
    }
    
    /**
     * Extract device ID from MQTT topic.
     * Expected format: "ampmeter/device/{deviceId}/data"
     */
    private fun extractDeviceIdFromTopic(topic: String): String {
        return try {
            val parts = topic.split("/")
            if (parts.size >= 3) parts[2] else "unknown"
        } catch (e: Exception) {
            "unknown"
        }
    }
    
    /**
     * Helper method to extract double value from simple JSON string.
     */
    private fun extractDoubleValue(json: String, key: String): Double? {
        return try {
            val startIndex = json.indexOf(key)
            if (startIndex != -1) {
                val valueStart = startIndex + key.length
                val valueEnd = json.indexOf(",", valueStart).takeIf { it != -1 } ?: json.indexOf("}", valueStart)
                if (valueEnd != -1) {
                    json.substring(valueStart, valueEnd).trim().toDouble()
                } else null
            } else null
        } catch (e: Exception) {
            null
        }
    }
    
    /**
     * Helper method to extract integer value from simple JSON string.
     */
    private fun extractIntValue(json: String, key: String): Int? {
        return try {
            extractDoubleValue(json, key)?.toInt()
        } catch (e: Exception) {
            null
        }
    }
    
    /**
     * Disconnect from the MQTT broker.
     */
    fun disconnect() {
        serviceScope.launch(Dispatchers.IO) {
            try {
                mqttClient?.disconnect()
                Timber.d("Disconnected from MQTT broker")
                isConnected.set(false)
                mqttClient = null
            } catch (e: MqttException) {
                Timber.e(e, "MQTT Exception when disconnecting: ${e.reasonCode}")
            } catch (e: Exception) {
                Timber.e(e, "Error disconnecting from MQTT broker")
            }
        }
    }
    
    override fun onDestroy() {
        disconnect()
        super.onDestroy()
    }
    
    inner class MqttBinder : Binder() {
        fun getService(): MqttClientService = this@MqttClientService
    }
    
    companion object {
        // Helper method to start the service
        fun startService(context: Context) {
            val startIntent = Intent(context, MqttClientService::class.java)
            context.startService(startIntent)
        }
        
        // Helper method to stop the service
        fun stopService(context: Context) {
            val stopIntent = Intent(context, MqttClientService::class.java)
            context.stopService(stopIntent)
        }
    }
} 