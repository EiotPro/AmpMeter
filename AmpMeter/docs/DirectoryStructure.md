# AmpMeter Directory Structure

This document outlines the detailed directory structure of the AmpMeter Android application, following Clean Architecture principles.

## Project Organization

```
AmpMeter/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   │   └── com/
│   │   │   │       └── example/
│   │   │   │           └── ampmeter/
│   │   │   │               ├── AmpMeterApplication.kt       # Hilt Application class
│   │   │   │               ├── MainActivity.kt              # Main activity with navigation
│   │   │   │               ├── data/                        # Data Layer
│   │   │   │               │   ├── local/                   # Local data sources
│   │   │   │               │   │   ├── database/            # Room database
│   │   │   │               │   │   │   ├── AppDatabase.kt   # Database configuration
│   │   │   │               │   │   │   ├── dao/             # Data Access Objects
│   │   │   │               │   │   │   │   ├── DeviceReadingDao.kt
│   │   │   │               │   │   │   │   └── DeviceLogDao.kt
│   │   │   │               │   │   │   └── entity/          # Database entities
│   │   │   │               │   │   │       ├── DeviceReadingEntity.kt
│   │   │   │               │   │   │       └── DeviceLogEntity.kt
│   │   │   │               │   │   ├── datastore/           # DataStore for preferences
│   │   │   │               │   │   │   ├── SettingsDataStore.kt
│   │   │   │               │   │   │   └── PreferencesKeys.kt
│   │   │   │               │   │   └── mapper/              # Entity to domain mappers
│   │   │   │               │   │       ├── DeviceReadingMapper.kt
│   │   │   │               │   │       └── DeviceLogMapper.kt
│   │   │   │               │   ├── remote/                  # Remote data sources
│   │   │   │               │   │   ├── api/                 # API interfaces
│   │   │   │               │   │   │   ├── ChirpStackApi.kt
│   │   │   │               │   │   │   └── SupabaseApi.kt
│   │   │   │               │   │   ├── dto/                 # Data Transfer Objects
│   │   │   │               │   │   │   ├── chirpstack/
│   │   │   │               │   │   │   │   ├── FramesResponse.kt
│   │   │   │               │   │   │   │   ├── DeviceResponse.kt
│   │   │   │               │   │   │   │   └── DownlinkRequest.kt
│   │   │   │               │   │   │   └── supabase/
│   │   │   │               │   │   │       └── DeviceLogDto.kt
│   │   │   │               │   │   ├── interceptor/         # Network interceptors
│   │   │   │               │   │   │   ├── AuthInterceptor.kt
│   │   │   │               │   │   │   └── LoggingInterceptor.kt
│   │   │   │               │   │   └── mapper/              # DTO to domain mappers
│   │   │   │               │   │       ├── ChirpStackMapper.kt
│   │   │   │               │   │       └── SupabaseMapper.kt
│   │   │   │               │   └── repository/              # Repository implementations
│   │   │   │               │       ├── DeviceRepositoryImpl.kt
│   │   │   │               │       └── SettingsRepositoryImpl.kt
│   │   │   │               ├── di/                          # Dependency Injection
│   │   │   │               │   ├── module/                  # Hilt modules
│   │   │   │               │   │   ├── NetworkModule.kt
│   │   │   │               │   │   ├── DatabaseModule.kt
│   │   │   │               │   │   ├── RepositoryModule.kt
│   │   │   │               │   │   └── UseCaseModule.kt
│   │   │   │               │   └── qualifier/               # Custom qualifiers
│   │   │   │               │       └── Qualifiers.kt
│   │   │   │               ├── domain/                      # Domain Layer
│   │   │   │               │   ├── model/                   # Domain models
│   │   │   │               │   │   ├── DeviceReading.kt
│   │   │   │               │   │   ├── DeviceLog.kt
│   │   │   │               │   │   ├── ConnectionStatus.kt
│   │   │   │               │   │   └── Resource.kt
│   │   │   │               │   ├── repository/              # Repository interfaces
│   │   │   │               │   │   ├── DeviceRepository.kt
│   │   │   │               │   │   └── SettingsRepository.kt
│   │   │   │               │   └── usecase/                 # Business logic
│   │   │   │               │       ├── device/
│   │   │   │               │       │   ├── GetLatestReadingUseCase.kt
│   │   │   │               │       │   ├── GetDeviceReadingsUseCase.kt
│   │   │   │               │       │   └── SyncDataUseCase.kt
│   │   │   │               │       └── settings/
│   │   │   │               │           ├── GetSettingsUseCase.kt
│   │   │   │               │           └── UpdateSettingsUseCase.kt
│   │   │   │               ├── presentation/                # Presentation Layer
│   │   │   │               │   ├── adapter/                 # RecyclerView adapters
│   │   │   │               │   │   ├── LogsAdapter.kt
│   │   │   │               │   │   └── DeviceInfoAdapter.kt
│   │   │   │               │   ├── ui/                      # UI components
│   │   │   │               │   │   ├── common/              # Shared UI components
│   │   │   │               │   │   │   ├── view/
│   │   │   │               │   │   │   │   ├── CurrentDisplayView.kt
│   │   │   │               │   │   │   │   └── ConnectionStatusView.kt
│   │   │   │               │   │   │   └── extension/
│   │   │   │               │   │   │       └── ViewExtensions.kt
│   │   │   │               │   │   ├── dashboard/           # Dashboard screen
│   │   │   │               │   │   │   ├── DashboardFragment.kt
│   │   │   │               │   │   │   ├── DashboardViewModel.kt
│   │   │   │               │   │   │   └── DashboardUiState.kt
│   │   │   │               │   │   ├── logs/                # Logs screen
│   │   │   │               │   │   │   ├── LogsFragment.kt
│   │   │   │               │   │   │   ├── LogsViewModel.kt
│   │   │   │               │   │   │   └── LogsUiState.kt
│   │   │   │               │   │   └── settings/            # Settings screen
│   │   │   │               │   │       ├── SettingsFragment.kt
│   │   │   │               │   │       ├── SettingsViewModel.kt
│   │   │   │               │   │       └── SettingsUiState.kt
│   │   │   │               │   └── util/                    # UI utilities
│   │   │   │               │       ├── FormatterUtil.kt
│   │   │   │               │       └── UiExtensions.kt
│   │   │   │               └── util/                        # Utility classes
│   │   │   │                   ├── network/
│   │   │   │                   │   ├── NetworkManager.kt
│   │   │   │                   │   └── NetworkManagerImpl.kt
│   │   │   │                   ├── PayloadDecoder.kt
│   │   │   │                   ├── DateTimeUtil.kt
│   │   │   │                   └── Constants.kt
│   │   │   ├── res/                                        # Resources
│   │   │   │   ├── drawable/                               # Drawable resources
│   │   │   │   ├── layout/                                 # Layout files
│   │   │   │   │   ├── activity_main.xml
│   │   │   │   │   ├── fragment_dashboard.xml
│   │   │   │   │   ├── fragment_logs.xml
│   │   │   │   │   ├── fragment_settings.xml
│   │   │   │   │   ├── item_log.xml
│   │   │   │   │   └── view_current_display.xml
│   │   │   │   ├── menu/                                   # Menu resources
│   │   │   │   │   └── bottom_nav_menu.xml
│   │   │   │   ├── navigation/                             # Navigation graph
│   │   │   │   │   └── mobile_navigation.xml
│   │   │   │   ├── values/                                 # Values resources
│   │   │   │   │   ├── colors.xml
│   │   │   │   │   ├── dimens.xml
│   │   │   │   │   ├── strings.xml
│   │   │   │   │   └── themes.xml
│   │   │   │   └── values-night/                           # Night mode resources
│   │   │   │       └── themes.xml
│   │   │   └── AndroidManifest.xml                         # App manifest
│   │   ├── test/                                           # Unit tests
│   │   │   └── java/
│   │   │       └── com/
│   │   │           └── example/
│   │   │               └── ampmeter/
│   │   │                   ├── data/                        # Data layer tests
│   │   │                   │   ├── repository/
│   │   │                   │   └── local/
│   │   │                   ├── domain/                      # Domain layer tests
│   │   │                   │   └── usecase/
│   │   │                   └── presentation/                # Presentation layer tests
│   │   │                       └── ui/
│   │   └── androidTest/                                    # UI/Integration tests
│   │       └── java/
│   │           └── com/
│   │               └── example/
│   │                   └── ampmeter/
│   ├── build.gradle.kts                                    # App module build file
│   └── proguard-rules.pro                                  # ProGuard rules
├── build.gradle.kts                                        # Project build file
├── gradle/                                                 # Gradle configuration
│   └── libs.versions.toml                                  # Version catalog
├── docs/                                                   # Documentation
│   ├── ProjectStructure.md
│   ├── ImplementationPlan.md
│   ├── SupabaseIntegration.md
│   ├── ChirpStackIntegration.md
│   └── CleanArchitecture.md
├── gradle.properties                                       # Gradle properties
├── settings.gradle.kts                                     # Settings file
└── README.md                                               # Project README
```

## Package Organization

The codebase follows a package-by-feature approach within each architectural layer. This makes the codebase more navigable and maintainable.

### Key Package Principles

1. **Layer-First Organization**: Primary division by architectural layer (data, domain, presentation)
2. **Feature-Based Subpackages**: Within each layer, organized by feature
3. **Clear Responsibilities**: Each package has a distinct responsibility
4. **Consistent Naming**: Follows Android naming conventions

### Import Statement Order Conventions

For consistent code styling, the import statements should be organized in the following order:

1. Android imports
2. Third-party library imports
3. Java/Kotlin standard library imports
4. Same-package imports

## Resource Organization

### Layout Naming Conventions

- `activity_*.xml`: Activity layouts
- `fragment_*.xml`: Fragment layouts
- `item_*.xml`: RecyclerView item layouts
- `view_*.xml`: Custom view layouts
- `dialog_*.xml`: Dialog layouts

### Drawable Naming Conventions

- `ic_*`: Icons
- `bg_*`: Backgrounds
- `divider_*`: Dividers
- `selector_*`: Selectors
- `shape_*`: Shape drawables

## Code Style Guidelines

For detailed code style guidelines, refer to the [Clean Architecture Guide](CleanArchitecture.md). 