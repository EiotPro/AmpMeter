package com.example.ampmeter.domain.model

/**
 * Represents the connection status of a device.
 */
enum class ConnectionStatus {
    ONLINE,
    OFFLINE,
    UNKNOWN;
    
    companion object {
        /**
         * Determines connection status based on last seen timestamp.
         * 
         * @param timestamp The timestamp when the device was last seen
         * @param thresholdMillis The threshold in milliseconds (default 5 minutes)
         * @return ConnectionStatus
         */
        fun fromTimestamp(
            timestamp: Long?, 
            thresholdMillis: Long = 5 * 60 * 1000
        ): ConnectionStatus {
            if (timestamp == null) return UNKNOWN
            
            val timeDiff = System.currentTimeMillis() - timestamp
            return if (timeDiff < thresholdMillis) {
                ONLINE
            } else {
                OFFLINE
            }
        }
    }
} 