# AmpMeter App Testing Plan

This document outlines a comprehensive testing plan for the AmpMeter Android application, focusing on its integration with ChirpStack and current features.

## Prerequisites

- Android device or emulator running Android 8.0 (API level 26) or higher
- Access to a ChirpStack server with at least one configured LoRaWAN device
- Valid ChirpStack API key with read/write permissions
- Enthutech gateway properly configured and online
- LoRaWAN device with current sensor (e.g., WCS6800 current sensor with RAK3172 module)

## 1. Settings Configuration Testing

### 1.1 Basic Settings Validation
- [ ] Verify all settings fields can be populated and saved
- [ ] Verify validation for required fields (Device ID)
- [ ] Test settings persistence after app restart

### 1.2 Connection Testing
- [ ] Test the "Test Connection" functionality with:
  - [ ] Valid device ID and server settings
  - [ ] Invalid device ID
  - [ ] Invalid server URL
  - [ ] Invalid API key
  - [ ] Server unavailable (e.g., airplane mode)

### 1.3 Settings Range Testing
- [ ] Test minimum and maximum values for refresh interval
- [ ] Test with very long device IDs and names
- [ ] Test with special characters in device ID and name

## 2. ChirpStack Integration Testing

### 2.1 Authentication Testing
- [ ] Verify API key authentication works correctly
- [ ] Test behavior when API key expires or is revoked
- [ ] Verify proper error handling for authentication failures

### 2.2 Device Data Retrieval
- [ ] Verify the app can retrieve device frames from ChirpStack
- [ ] Test with multiple frames available
- [ ] Test with no frames available
- [ ] Verify proper handling of malformed frame data

### 2.3 Payload Decoding
- [ ] Test payload decoder with valid data
- [ ] Test with different payload formats:
  - [ ] Standard format (current, voltage, battery)
  - [ ] Extended format (with RSSI, SNR)
  - [ ] Malformed or corrupted payloads
- [ ] Verify correct unit conversions (mA to A, cV to V)

### 2.4 Offline Functionality
- [ ] Test app behavior when network connection is lost
- [ ] Verify cached data is displayed when offline
- [ ] Verify smooth transition between online and offline modes
- [ ] Test reconnection behavior when network becomes available again

## 3. Dashboard Feature Testing

### 3.1 Real-time Data Display
- [ ] Verify current readings are displayed correctly
- [ ] Test auto-refresh functionality with different intervals
- [ ] Verify timestamp of last update is accurate
- [ ] Test manual refresh button

### 3.2 Alert Functionality
- [ ] Test threshold alerts when current exceeds configured threshold
- [ ] Verify alert dismissal works correctly
- [ ] Test alert persistence across screen rotations
- [ ] Test with different threshold values

### 3.3 Connection Status Indicator
- [ ] Verify connection status indicator changes based on data freshness
- [ ] Test different status states (connected, warning, disconnected)
- [ ] Verify correct timestamp-based status determination

## 4. Logs Feature Testing

### 4.1 Historical Data Display
- [ ] Verify historical readings are displayed correctly
- [ ] Test pagination functionality
- [ ] Verify correct ordering of readings (newest first)
- [ ] Test with large datasets (100+ readings)

### 4.2 Data Persistence
- [ ] Verify readings are stored in local database
- [ ] Test database query performance with large datasets
- [ ] Verify data survives app restarts

## 5. Integration Testing with Enthutech Gateway

### 5.1 End-to-End Testing
- [ ] Configure a physical device with the Enthutech gateway
- [ ] Verify uplink messages are received and decoded correctly
- [ ] Test with varying signal strengths and distances
- [ ] Verify RSSI and SNR values are displayed correctly when available

### 5.2 Real-world Scenario Testing
- [ ] Test with actual current loads (e.g., connect to a circuit with known load)
- [ ] Verify readings match expected values from physical measurements
- [ ] Test behavior with rapidly changing current values
- [ ] Test long-term stability (run for 24+ hours)

## 6. Performance Testing

### 6.1 Resource Usage
- [ ] Monitor CPU usage during normal operation
- [ ] Monitor memory usage with large datasets
- [ ] Test battery consumption with different refresh intervals
- [ ] Verify network bandwidth usage is optimized

### 6.2 Responsiveness
- [ ] Test UI responsiveness during data retrieval
- [ ] Verify smooth scrolling in logs view with large datasets
- [ ] Test app behavior during configuration changes (e.g., rotation)
- [ ] Measure app startup time with cold and warm starts

## 7. Error Handling Testing

### 7.1 Network Errors
- [ ] Test behavior with intermittent network connectivity
- [ ] Verify appropriate error messages for network failures
- [ ] Test recovery from network errors

### 7.2 Server Errors
- [ ] Test with server returning 4xx errors
- [ ] Test with server returning 5xx errors
- [ ] Verify appropriate error messages for server failures

### 7.3 Data Errors
- [ ] Test with corrupted or invalid data from server
- [ ] Verify app doesn't crash with unexpected data formats
- [ ] Test with empty response bodies

## 8. Compatibility Testing

### 8.1 Device Compatibility
- [ ] Test on multiple Android versions (8.0 through latest)
- [ ] Test on different screen sizes and densities
- [ ] Test on devices with different hardware capabilities

### 8.2 ChirpStack Version Compatibility
- [ ] Test with different ChirpStack server versions
- [ ] Verify compatibility with API changes between versions

## 9. Security Testing

### 9.1 Data Security
- [ ] Verify API keys are stored securely
- [ ] Test for sensitive data exposure in logs
- [ ] Verify network communications use HTTPS

### 9.2 Input Validation
- [ ] Test with malicious inputs in all fields
- [ ] Verify proper sanitization of user inputs
- [ ] Test with extremely long inputs

## Test Execution Checklist

For each test case:
1. Document the test environment (device, OS version, app version)
2. Record the expected behavior
3. Record the actual behavior
4. Document any discrepancies
5. Capture screenshots or logs for failures
6. Track issues in issue tracker with reproduction steps

## Automated Testing

Consider implementing the following automated tests:
- Unit tests for the PayloadDecoder
- Integration tests for the DeviceRepository
- UI tests for basic app navigation and settings input

## Regression Testing

After any significant changes:
1. Re-run all critical path tests
2. Verify no new issues were introduced
3. Confirm all fixed issues remain resolved 