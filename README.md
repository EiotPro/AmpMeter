# AmpMeter - LoRaWAN Current Sensor Monitor



AmpMeter is an Android application designed to monitor current sensor readings from IoT devices using LoRaWAN and ChirpStack. The app provides real-time monitoring, historical data logging, and comprehensive configuration options.

## Current Status

- ✅ Basic UI implementation with three main screens: Dashboard, Logs, and Settings
- ✅ Settings screen fully functional with device configuration options
- ✅ Navigation between screens working properly
- ✅ Data models and repositories implemented
- ⏳ Dashboard screen shows placeholder data until connected to ChirpStack server
- ⏳ Logs screen shows "No readings available" until data is received

## Features

- **Real-time Current Monitoring**: Display live current readings with auto-refresh
- **Historical Data Logging**: View and analyze historical readings with filtering
- **Offline Support**: Full functionality even without network connectivity
- **Configurable Settings**: Customize server connections, refresh intervals, and more

## Project Structure

The project follows Clean Architecture principles:

```
app/src/main/java/com/example/ampmeter/
├── data/                 # Data layer: repositories, data sources
├── domain/               # Domain layer: use cases, business models
├── presentation/         # Presentation layer: UI, ViewModels
├── di/                   # Dependency injection
└── util/                 # Utility classes
```

## Development Setup

### Prerequisites

- Android Studio Flamingo (2022.2.1) or newer
- Android SDK 24+
- Kotlin 1.9.0+
- Access to a ChirpStack LoRaWAN server
- Supabase account (optional, for cloud sync)

### Building the Project

1. Clone the repository:
   ```bash
   git clone https://github.com/yourusername/AmpMeter.git
   ```

2. Open the project in Android Studio

3. Build the project:
   ```bash
   ./gradlew assembleDebug
   ```

4. Run the application on an emulator or physical device:
   ```bash
   ./gradlew installDebug
   ```

### Configuration

Before running the app, you need to configure:

1. **ChirpStack Server**: Set up your ChirpStack server URL and API key in the Settings screen
2. **Device ID**: Configure the device EUI in the Settings screen
3. **Refresh Interval**: Set how often the app should fetch new data

## Next Steps for Development

1. **Connect to ChirpStack Server**:
   - Implement API connection with the provided server URL and API key
   - Test connection and fetch real device data

2. **Enhance Dashboard Display**:
   - Show real-time current readings from the device
   - Implement proper error handling for connection issues
   - Add visual indicators for values outside normal ranges

3. **Populate Logs Screen**:
   - Fetch and display historical readings
   - Implement pagination for large datasets
   - Add filtering and sorting options

4. **Implement Data Synchronization**:
   - Set up Supabase integration for cloud storage
   - Create background sync service
   - Add offline support with local caching

5. **Add Notifications**:
   - Implement alert system for readings outside thresholds
   - Create notification channels for different alert types
   - Add background monitoring service

## Maintenance

To keep the project clean and organized:

1. Run the cleanup script:
   ```bash
   ./cleanup-project.ps1
   ```

2. Set up Git hooks:
   ```bash
   ./setup-hooks.ps1
   ```

3. Follow the [Maintenance Guidelines](AmpMeter/docs/MaintenanceGuidelines.md)

## Implementation Plan

For details on the implementation approach, see [Implementation Plan](AmpMeter/docs/ImplementationPlan.md).

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request. 