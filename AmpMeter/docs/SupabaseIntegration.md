# Supabase Integration Guide

This document outlines the integration of Supabase with the AmpMeter Android application for storing and retrieving IoT sensor data.

## Overview

Supabase is used as the cloud database for storing historical sensor readings and providing real-time updates. It acts as a synchronized backup of the local Room database and enables features like historical data analysis and cross-device synchronization.

## Setup Requirements

### Dependencies

Add the following dependencies to the app's build.gradle.kts file:

```kotlin
// Supabase client libraries
implementation("io.github.jan-tennert.supabase:postgrest-kt:2.0.0")
implementation("io.github.jan-tennert.supabase:realtime-kt:2.0.0")
implementation("io.github.jan-tennert.supabase:storage-kt:2.0.0") // For file uploads (optional)
```

### Configuration

The Supabase client should be initialized as follows:

```kotlin
val supabase = createSupabaseClient(
    supabaseUrl = "https://your-project.supabase.co",
    supabaseKey = "your-anon-key"
) {
    install(Postgrest)
    install(Realtime)
    install(Storage) // Optional
}
```

This initialization should be done in a dependency injection module to provide the Supabase client throughout the app.

## Database Schema

The Supabase database should have the following schema:

```sql
CREATE TABLE device_logs (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    device_id VARCHAR(50) NOT NULL,
    timestamp TIMESTAMPTZ DEFAULT NOW(),
    current_value DECIMAL(10,3),
    voltage DECIMAL(8,2),
    battery_level INTEGER,
    rssi INTEGER,
    snr DECIMAL(5,2),
    status VARCHAR(20),
    raw_data TEXT,
    frame_count INTEGER,
    gateway_id VARCHAR(50)
);

-- Index for faster queries by device_id and timestamp
CREATE INDEX idx_device_logs_device_id_timestamp ON device_logs(device_id, timestamp);

-- Create a view for the latest readings per device
CREATE VIEW latest_device_readings AS
SELECT DISTINCT ON (device_id) *
FROM device_logs
ORDER BY device_id, timestamp DESC;

-- Row Level Security Policies (if needed)
ALTER TABLE device_logs ENABLE ROW LEVEL SECURITY;

-- Allow read access for all authenticated users
CREATE POLICY "Allow read access for all users" ON device_logs
    FOR SELECT USING (true);

-- Allow insert/update only for specific authenticated users
CREATE POLICY "Allow insert for authenticated users" ON device_logs
    FOR INSERT WITH CHECK (auth.role() = 'authenticated');
```

## Key Operations

### Fetching Data

#### Getting Latest Reading

```kotlin
suspend fun getLatestReading(deviceId: String): DeviceReading? {
    return try {
        val response = supabase
            .from("device_logs")
            .select()
            .eq("device_id", deviceId)
            .order("timestamp", ascending = false)
            .limit(1)
            .single<DeviceLogDto>()
            
        response.toDeviceReading()
    } catch (e: Exception) {
        Timber.e(e, "Error fetching latest reading from Supabase")
        null
    }
}
```

#### Getting Historical Data with Pagination

```kotlin
suspend fun getDeviceReadings(
    deviceId: String, 
    limit: Int, 
    offset: Int
): List<DeviceReading> {
    return try {
        val response = supabase
            .from("device_logs")
            .select()
            .eq("device_id", deviceId)
            .order("timestamp", ascending = false)
            .limit(limit)
            .range(offset, offset + limit - 1)
            .decodeList<DeviceLogDto>()
            
        response.map { it.toDeviceReading() }
    } catch (e: Exception) {
        Timber.e(e, "Error fetching device readings from Supabase")
        emptyList()
    }
}
```

### Inserting Data

```kotlin
suspend fun insertDeviceReading(deviceReading: DeviceReading): Boolean {
    return try {
        val dto = deviceReading.toDeviceLogDto()
        
        supabase
            .from("device_logs")
            .insert(dto)
            .execute()
            
        true
    } catch (e: Exception) {
        Timber.e(e, "Error inserting device reading to Supabase")
        false
    }
}
```

### Real-time Updates

```kotlin
fun subscribeToDeviceUpdates(
    deviceId: String,
    onUpdate: (DeviceReading) -> Unit
): RealtimeChannel {
    return supabase
        .from("device_logs")
        .on(RealtimeChannel.ChannelEventType.INSERT) { payload ->
            try {
                val dto = Json.decodeFromString<DeviceLogDto>(payload.new)
                if (dto.deviceId == deviceId) {
                    onUpdate(dto.toDeviceReading())
                }
            } catch (e: Exception) {
                Timber.e(e, "Error processing real-time update")
            }
        }
        .subscribe()
}
```

## Synchronization Strategy

### Local to Cloud Sync

1. Store data in local Room database first
2. Queue changes for sync to Supabase
3. Sync when network is available
4. Handle conflict resolution if needed

```kotlin
suspend fun syncLocalToCloud(): SyncResult {
    val unsynced = localDatabase.getUnsyncedReadings()
    var successCount = 0
    var errorCount = 0
    
    unsynced.forEach { reading ->
        val success = insertDeviceReading(reading)
        if (success) {
            localDatabase.markAsSynced(reading.id)
            successCount++
        } else {
            errorCount++
        }
    }
    
    return SyncResult(
        totalSynced = successCount,
        totalFailed = errorCount,
        isComplete = errorCount == 0
    )
}
```

### Cloud to Local Sync

1. Fetch latest data from Supabase using timestamp-based sync
2. Insert into local database
3. Resolve conflicts if any

```kotlin
suspend fun syncCloudToLocal(deviceId: String): SyncResult {
    val lastSyncTime = settingsRepository.getLastSyncTime(deviceId)
    
    val remoteReadings = supabase
        .from("device_logs")
        .select()
        .eq("device_id", deviceId)
        .gt("timestamp", lastSyncTime.toString())
        .order("timestamp", ascending = true)
        .decodeList<DeviceLogDto>()
        .map { it.toDeviceReading() }
        
    var successCount = 0
    var errorCount = 0
    
    remoteReadings.forEach { reading ->
        try {
            localDatabase.insertReading(reading)
            successCount++
        } catch (e: Exception) {
            Timber.e(e, "Error syncing remote reading to local")
            errorCount++
        }
    }
    
    if (remoteReadings.isNotEmpty()) {
        val latestTimestamp = remoteReadings.maxByOrNull { it.timestamp }?.timestamp
        latestTimestamp?.let {
            settingsRepository.setLastSyncTime(deviceId, it)
        }
    }
    
    return SyncResult(
        totalSynced = successCount,
        totalFailed = errorCount,
        isComplete = errorCount == 0
    )
}
```

## Error Handling

1. Network errors - Implement retry with exponential backoff
2. Authentication errors - Prompt for re-authentication
3. Data formatting errors - Log and report for debugging

## Security Considerations

1. Store Supabase API key securely (encrypted SharedPreferences or EncryptedSharedPreferences)
2. Use HTTPS for all communications
3. Implement proper authentication if required
4. Use row-level security in Supabase for multi-user scenarios

## Performance Optimization

1. Batch insert operations for multiple records
2. Use pagination for large datasets
3. Implement background synchronization using WorkManager
4. Cache frequently accessed data locally

## Testing

1. Mock Supabase API responses for unit testing
2. Create test database for integration testing
3. Verify offline functionality works correctly 