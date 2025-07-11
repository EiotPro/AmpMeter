package com.example.ampmeter.util.network

import kotlinx.coroutines.flow.Flow

/**
 * Interface for network connectivity management.
 */
interface NetworkManager {
    /**
     * Checks if the device is connected to a network.
     * 
     * @return true if connected, false otherwise
     */
    fun isConnected(): Boolean
    
    /**
     * Gets a Flow that emits the current connection state.
     * 
     * @return Flow of connection state (true if connected, false otherwise)
     */
    fun observeConnectivity(): Flow<Boolean>
} 