# AmpMeter - LoRaWAN Android Monitoring App

<p align="center">
  <img src="docs/app_icon.png" alt="AmpMeter Logo" width="120" height="120"/>
</p>

AmpMeter is an Android application designed to monitor IoT current sensor readings transmitted via LoRaWAN and stored in Supabase. It provides real-time monitoring, historical data logging, and comprehensive configuration options for industrial IoT monitoring systems.

## Features

- **Real-time Current Monitoring**: Display live sensor readings with auto-refresh
- **Historical Data Logs**: View and analyze historical readings with filtering and export
- **Offline Support**: Full functionality even without network connectivity
- **Configurable Settings**: Customize server connections, refresh intervals, and more
- **Data Synchronization**: Seamless sync between local and cloud storage

## System Architecture

```
[RP2040 Pico W + RAK3172 LoRa] → [ChirpStack LoRaWAN Gateway] → [Android App] ← [Supabase Database]
      (IoT Current Sensor)              (Network Server)         (Mobile UI)    (Cloud Storage)
```

## Technology Stack

- **Language**: Kotlin
- **Architecture**: MVVM + Clean Architecture
- **UI Framework**: XML Layouts + View Binding
- **Networking**: Retrofit2 + OkHttp3
- **Database**: Room (local caching) + Supabase (cloud sync)
- **Async Operations**: Coroutines + StateFlow/LiveData
- **Dependency Injection**: Hilt/Dagger2
- **Data Storage**: DataStore (settings/preferences)

## Screenshots

<p align="center">
  <img src="docs/screenshot_dashboard.png" alt="Dashboard" width="260"/>
  <img src="docs/screenshot_logs.png" alt="Logs" width="260"/>
  <img src="docs/screenshot_settings.png" alt="Settings" width="260"/>
</p>

## Getting Started

### Prerequisites

- Android Studio Flamingo (2022.2.1) or newer
- Android SDK 24+
- Kotlin 1.9.0+
- Access to a ChirpStack LoRaWAN server
- Supabase account (optional, for cloud sync)

### Setup

1. Clone the repository:
   ```bash
   git clone https://github.com/yourusername/AmpMeter.git
   ```

2. Open the project in Android Studio

3. Configure ChirpStack and Supabase:
   - In the app, navigate to Settings
   - Enter your ChirpStack server URL and API key
   - (Optional) Configure Supabase URL and API key

4. Build and run the application

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

For detailed information on the architecture, see [Clean Architecture Guide](docs/CleanArchitecture.md).

## API Integration

- [ChirpStack Integration Guide](docs/ChirpStackIntegration.md)
- [Supabase Integration Guide](docs/SupabaseIntegration.md)

## Implementation Plan

For details on the implementation approach, see [Implementation Plan](docs/ImplementationPlan.md).

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Acknowledgements

- [ChirpStack](https://www.chirpstack.io/) - Open-source LoRaWAN Network Server
- [Supabase](https://supabase.io/) - Open-source Firebase alternative
- [RAK Wireless](https://www.rakwireless.com/) - For the RAK3172 LoRa module 