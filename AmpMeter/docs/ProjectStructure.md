# AmpMeter - LoRaWAN Android Monitoring App

## Project Overview

AmpMeter is an Android application designed to monitor IoT current sensor readings transmitted via LoRaWAN and stored in Supabase. It provides real-time monitoring of current sensor values, historical data logging, and comprehensive configuration options.

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

## Project Structure

```
app/src/main/java/com/example/ampmeter/
├── data/
│   ├── local/
│   │   ├── database/         # Room database
│   │   ├── datastore/        # Settings storage
│   │   └── entities/         # Local data models
│   ├── remote/
│   │   ├── api/              # Retrofit interfaces
│   │   ├── dto/              # API response models
│   │   └── interceptors/     # Network interceptors
│   └── repository/           # Data repository implementations
├── domain/
│   ├── model/                # Business models
│   ├── repository/           # Repository interfaces
│   └── usecase/              # Business logic
├── presentation/
│   ├── ui/
│   │   ├── dashboard/        # Dashboard fragment + ViewModel
│   │   ├── logs/             # Logs fragment + ViewModel
│   │   ├── settings/         # Settings fragment + ViewModel
│   │   └── common/           # Shared UI components
│   └── adapter/              # RecyclerView adapters
├── di/                       # Dependency injection
└── util/                     # Utility classes
```

## Key Components

### Main Screens

1. **Dashboard Fragment**: Real-time monitoring of current sensor readings
   - Large current display
   - Connection status indicator
   - Last updated timestamp
   - Device info cards
   - Auto-refresh functionality

2. **Logs Fragment**: Display historical sensor readings and system logs
   - Recyclerview with custom adapter
   - Pull-to-refresh
   - Infinite scrolling
   - Search/filter functionality
   - Export logs feature

3. **Settings Fragment**: App configuration and device management
   - ChirpStack configuration
   - Supabase configuration
   - App preferences
   - Device management

### Data Flow

1. **Primary Data Source**: ChirpStack REST API
   - Endpoint: `GET /api/devices/{devEUI}/frames`
   - Parse base64 encoded payload to extract current value

2. **Fallback Data Source**: Local Room database cache

3. **Historical Data**: Supabase cloud database

## Implementation Phases

### Phase 1: Foundation
- Project setup
- Data models
- Basic UI structure

### Phase 2: Core Features
- ChirpStack API integration
- Local database setup
- Repository layer implementation

### Phase 3: UI Implementation
- Dashboard fragment
- Logs fragment
- Settings fragment

### Phase 4: Advanced Features
- Supabase integration
- Error handling & UX
- Performance optimization

### Phase 5: Testing & Polish
- Unit and UI testing
- Performance & security optimization
- Documentation

## Key Implementation Challenges

1. **Payload Decoding**: Robust decoder for base64 encoded binary data
2. **Offline Functionality**: Local caching with Room and smart sync
3. **Real-time Updates**: Combining polling with WebSocket/SSE
4. **Battery Optimization**: Adaptive refresh intervals
5. **Error Handling**: Comprehensive error handling with retry logic 