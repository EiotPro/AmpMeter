# ChirpStack Integration Test Script

This script provides step-by-step instructions for testing the ChirpStack integration in the AmpMeter app.

## Prerequisites

1. ChirpStack server accessible via network
2. At least one LoRaWAN device configured in ChirpStack
3. Valid API key with read/write permissions
4. Enthutech gateway properly configured and online
5. Android device with AmpMeter app installed

## Test 1: Configuration and Connection Test

### 1.1 Configure ChirpStack Settings

1. Launch the AmpMeter app
2. Navigate to the Settings screen
3. Enter the following information:
   - Device ID: `[Your Device EUI]` (e.g., "a81758fffe05e6fb")
   - Device Name: `[Descriptive Name]` (e.g., "Test Current Sensor")
   - Server URL: `[ChirpStack API URL]` (e.g., "103.161.75.85:8082") - Enter just the IP and port, the app automatically adds http:// prefix
   - API Key: `[Your ChirpStack API Key]`(eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJhdWQiOiJjaGlycHN0YWNrIiwiaXNzIjoiY2hpcnBzdGFjayIsInN1YiI6ImEyMjRlODI1LWRiMzctNGM2ZC1hMTc1LTQwNzYzZmJlMWRmYiIsInR5cCI6ImtleSJ9.XFHU-nt6CXGRU8BGmolcoi2aRc0xMk7mW5SL_14u7T4)
   - Refresh Interval: 30 seconds
   - Enable Notifications: On
4. Tap "Save Settings"
5. **Expected Result**: Toast message "Settings saved successfully"

### 1.2 Test Connection

1. Tap "Test Connection" button
2. **Expected Result**: 
   - If successful: "Connection successful! Device found: [Device Name]"
   - If device not found: Error message indicating device not found
   - If server error: Appropriate error message

## Test 2: Data Retrieval and Display

### 2.1 Dashboard Data Display

1. Navigate to the Dashboard screen
2. Wait for the auto-refresh to trigger or tap the refresh button
3. **Expected Result**:
   - Current reading displayed in Amperes
   - Voltage reading displayed in Volts
   - Battery level displayed as percentage
   - Last updated timestamp shown
   - Connection status indicator showing appropriate status

### 2.2 Verify Data Accuracy

1. Compare the displayed readings with the actual data in ChirpStack
   - Open ChirpStack web interface
   - Navigate to the device details page
   - Check the latest frame data
2. **Expected Result**: Values in app match the decoded values from ChirpStack

### 2.3 Test Auto-Refresh

1. Observe the dashboard for 2-3 refresh cycles
2. **Expected Result**: Data refreshes automatically at the configured interval

## Test 3: Historical Data in Logs

### 3.1 View Historical Data

1. Navigate to the Logs screen
2. **Expected Result**: List of historical readings shown in reverse chronological order

### 3.2 Verify Data Persistence

1. Put device in airplane mode
2. Navigate between Dashboard and Logs screens
3. **Expected Result**: Historical data still visible in Logs screen

## Test 4: Offline Functionality

### 4.1 Test Offline Mode

1. Ensure device is in airplane mode
2. Navigate to Dashboard screen and tap refresh
3. **Expected Result**: 
   - App shows cached data
   - Visual indication that data is from cache
   - No crash or ANR

### 4.2 Test Recovery

1. Turn off airplane mode
2. Wait for next auto-refresh or tap refresh button
3. **Expected Result**: App successfully retrieves fresh data from ChirpStack

## Test 5: Payload Decoding Verification

### 5.1 Verify Standard Payload

1. Send a standard uplink from your device (current, voltage, battery)
2. Refresh the Dashboard
3. **Expected Result**: All values correctly decoded and displayed

### 5.2 Verify Extended Payload

1. Send an extended uplink including RSSI and SNR values
2. Refresh the Dashboard
3. **Expected Result**: All values including RSSI and SNR correctly decoded and displayed

### 5.3 Verify Unit Conversions

1. Note the raw values in ChirpStack (mA, cV)
2. Compare with displayed values in app (A, V)
3. **Expected Result**: Conversion is correct (mA to A, cV to V)

## Test 6: Alert Functionality

### 6.1 Test Current Threshold Alert

1. In Settings, set an alert threshold below your current reading
2. Navigate to Dashboard and refresh
3. **Expected Result**: Alert shown indicating current exceeds threshold

### 6.2 Test Alert Dismissal

1. Dismiss the alert by tapping the dismiss button
2. **Expected Result**: Alert disappears

## Test 7: Integration with Enthutech Gateway

### 7.1 End-to-End Test

1. Configure your LoRaWAN device to send data through the Enthutech gateway
2. Trigger an uplink transmission from the device
3. Refresh the Dashboard in the app
4. **Expected Result**: Data successfully flows from device → gateway → ChirpStack → app

### 7.2 Signal Quality Test

1. Position your LoRaWAN device at different distances from the gateway
2. Trigger uplinks at each position
3. Check the RSSI and SNR values in the app
4. **Expected Result**: RSSI and SNR values reflect the changing signal quality

## Test 8: Error Handling

### 8.1 Invalid Device ID

1. Change the Device ID in Settings to an invalid value
2. Navigate to Dashboard and refresh
3. **Expected Result**: Appropriate error message, no crash

### 8.2 Server Unavailable

1. Configure an incorrect server URL
2. Navigate to Dashboard and refresh
3. **Expected Result**: Appropriate error message, fallback to cached data

### 8.3 Authentication Failure

1. Configure an invalid API key
2. Navigate to Dashboard and refresh
3. **Expected Result**: Authentication error message, fallback to cached data

## Test Results

| Test ID | Test Name | Result | Notes |
|---------|-----------|--------|-------|
| 1.1     | Configure ChirpStack Settings | | |
| 1.2     | Test Connection | | |
| 2.1     | Dashboard Data Display | | |
| 2.2     | Verify Data Accuracy | | |
| 2.3     | Test Auto-Refresh | | |
| 3.1     | View Historical Data | | |
| 3.2     | Verify Data Persistence | | |
| 4.1     | Test Offline Mode | | |
| 4.2     | Test Recovery | | |
| 5.1     | Verify Standard Payload | | |
| 5.2     | Verify Extended Payload | | |
| 5.3     | Verify Unit Conversions | | |
| 6.1     | Test Current Threshold Alert | | |
| 6.2     | Test Alert Dismissal | | |
| 7.1     | End-to-End Test | | |
| 7.2     | Signal Quality Test | | |
| 8.1     | Invalid Device ID | | |
| 8.2     | Server Unavailable | | |
| 8.3     | Authentication Failure | | |

## Issues and Observations

Document any issues encountered during testing here:

1. 
2. 
3. 

## Recommendations

Based on test results, provide recommendations for improvements:

1. 
2. 
3. 