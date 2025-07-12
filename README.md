# AmpMeter - LoRaWAN Current Sensor Monitor

AmpMeter is an Android application designed to monitor current sensor readings from IoT devices using LoRaWAN and ChirpStack. The app provides real-time monitoring, historical data logging, and comprehensive configuration options.

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

## Development Workflow

### Adding New Features

1. Create domain models in the `domain/model` package
2. Implement repository interfaces in the `domain/repository` package
3. Create use cases in the `domain/usecase` package
4. Implement repositories in the `data/repository` package
5. Create UI components in the `presentation/ui` package

### Coding Standards

- Follow Kotlin coding conventions
- Use meaningful variable and function names
- Add documentation comments for public APIs
- Write unit tests for business logic

### Testing

Run unit tests:
```bash
./gradlew test
```

Run instrumented tests:
```bash
./gradlew connectedAndroidTest
```

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

## License

This project is licensed under the MIT License - see the LICENSE file for details. 