# AmpMeter Testing Summary

This document summarizes the testing tools and resources created for the AmpMeter Android application.

## Testing Resources

### 1. Comprehensive Testing Documentation

- **TestingPlan.md**: A detailed testing plan covering all aspects of the application, including settings configuration, ChirpStack integration, dashboard functionality, logs functionality, offline operation, and error handling.

- **ChirpStackTestScript.md**: A step-by-step script specifically for testing the ChirpStack integration, including connection setup, data retrieval, payload decoding verification, and error handling.

### 2. Payload Testing Tool

A dedicated tool for testing the LoRaWAN payload encoding and decoding functionality:

- **PayloadTestActivity**: An interactive UI for generating test payloads and verifying decoder functionality.
- **PayloadTestUtil**: A utility class that provides methods for:
  - Generating test payloads with specified values
  - Verifying the payload decoder works correctly
  - Running a comprehensive test suite against the decoder

#### Features of the Payload Testing Tool:

- Generate payloads with custom current, voltage, and battery values
- Optional inclusion of RSSI and SNR values
- Display of Base64 encoded payload
- Display of decoded values
- Automated test suite for common payload scenarios
- Accessible from the main app menu

## How to Use the Testing Tools

### Using the Payload Test Tool

1. Open the AmpMeter app
2. Tap the menu icon in the top-right corner
3. Select "Payload Test Tool"
4. Enter the desired values for current, voltage, and battery level
5. Toggle RSSI and SNR inclusion if needed
6. Tap "Generate and Decode Payload" to see the results
7. Tap "Run Decoder Tests" to verify all decoder functionality

### Following the Test Scripts

1. Use **ChirpStackTestScript.md** as a guide when testing the ChirpStack integration
2. Fill in the test results table as you complete each test
3. Document any issues encountered in the "Issues and Observations" section
4. Provide recommendations for improvements based on your findings

### Executing the Comprehensive Test Plan

1. Follow the test cases outlined in **TestingPlan.md**
2. Use the checklist format to track completion
3. Document the test environment for each test session
4. Record expected vs. actual behavior
5. Capture screenshots or logs for any failures

## Best Practices for Testing

1. **Systematic Approach**: Follow the test plans systematically rather than testing randomly
2. **Document Everything**: Record all test results, even successful ones
3. **Regression Testing**: After fixing issues, re-test to ensure the fix works and doesn't break other functionality
4. **Real-world Testing**: Test with actual hardware when possible, not just simulated data
5. **Edge Cases**: Pay special attention to boundary conditions and error scenarios
6. **Performance Testing**: Monitor app performance during extended testing sessions

## Next Steps for Testing Improvement

1. Implement automated UI tests using Espresso
2. Create more unit tests for the payload decoder and repository implementations
3. Set up a continuous integration pipeline for automated testing
4. Develop a mock ChirpStack server for more reliable integration testing
5. Create a test harness for simulating different network conditions 