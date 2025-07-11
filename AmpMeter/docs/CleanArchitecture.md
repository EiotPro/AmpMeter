# Clean Architecture for AmpMeter

This document outlines the Clean Architecture principles applied to the AmpMeter Android application.

## Architecture Overview

The AmpMeter application follows the MVVM (Model-View-ViewModel) pattern with Clean Architecture principles to create a maintainable, testable, and scalable codebase.

The architecture is divided into three main layers:

```
┌───────────────────────────┐
│ Presentation Layer        │
│ (UI, ViewModels)          │
└───────────┬───────────────┘
            │
            ▼
┌───────────────────────────┐
│ Domain Layer              │
│ (Use Cases, Models)       │
└───────────┬───────────────┘
            │
            ▼
┌───────────────────────────┐
│ Data Layer                │
│ (Repositories, Data       │
│  Sources)                 │
└───────────────────────────┘
```

## Presentation Layer

The presentation layer contains the UI components and ViewModels. It is responsible for displaying data to the user and handling user interactions.

### Key Components

#### Fragments
- **DashboardFragment**: Displays real-time sensor readings
- **LogsFragment**: Shows historical data
- **SettingsFragment**: Provides configuration options

#### ViewModels
- **DashboardViewModel**: Manages dashboard UI state and data
- **LogsViewModel**: Handles log data retrieval and filtering
- **SettingsViewModel**: Manages settings and configuration

### Implementation Principles

1. **ViewModels as State Holders**
   - Use StateFlow/LiveData to expose UI state
   - Handle UI events through functions
   - Manage lifecycle-aware operations

```kotlin
@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val getLatestReadingUseCase: GetLatestReadingUseCase,
    private val settingsRepository: SettingsRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()
    
    private val refreshIntervalFlow = settingsRepository.getRefreshIntervalFlow()
    private var refreshJob: Job? = null
    
    init {
        viewModelScope.launch {
            refreshIntervalFlow.collectLatest { interval ->
                refreshJob?.cancel()
                refreshJob = startPeriodicRefresh(interval)
            }
        }
        refreshData()
    }
    
    fun refreshData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            val deviceId = settingsRepository.getDeviceId()
            val result = getLatestReadingUseCase(deviceId)
            
            _uiState.update { state ->
                when (result) {
                    is Resource.Success -> state.copy(
                        isLoading = false,
                        deviceReading = result.data,
                        connectionStatus = determineConnectionStatus(result.data),
                        lastUpdated = result.data.timestamp,
                        error = null
                    )
                    is Resource.Error -> state.copy(
                        isLoading = false,
                        error = result.message
                    )
                    is Resource.Loading -> state.copy(
                        isLoading = true
                    )
                }
            }
        }
    }
    
    private fun startPeriodicRefresh(intervalSeconds: Int): Job {
        return viewModelScope.launch {
            while (isActive) {
                delay(intervalSeconds * 1000L)
                refreshData()
            }
        }
    }
    
    private fun determineConnectionStatus(reading: DeviceReading?): ConnectionStatus {
        if (reading == null) return ConnectionStatus.OFFLINE
        
        val timeDiff = System.currentTimeMillis() - reading.timestamp
        return if (timeDiff < 5 * 60 * 1000) { // 5 minutes
            ConnectionStatus.ONLINE
        } else {
            ConnectionStatus.OFFLINE
        }
    }
}

data class DashboardUiState(
    val isLoading: Boolean = false,
    val deviceReading: DeviceReading? = null,
    val connectionStatus: ConnectionStatus = ConnectionStatus.UNKNOWN,
    val lastUpdated: Long = 0,
    val error: String? = null
)
```

## Domain Layer

The domain layer contains the business logic and defines the core functionality of the application. It is independent of the Android framework and other external dependencies.

### Key Components

#### Use Cases
- **GetLatestReadingUseCase**: Retrieves the latest sensor reading
- **GetDeviceReadingsUseCase**: Gets a list of historical readings
- **SyncDataUseCase**: Synchronizes local and remote data

#### Domain Models
- **DeviceReading**: Contains sensor reading data
- **ConnectionStatus**: Enum representing device connection state
- **Resource**: Wrapper for data loading states

### Implementation Principles

1. **Single Responsibility**
   - Each use case performs one specific operation
   - Use cases are easily testable

2. **Framework Independence**
   - No Android-specific dependencies
   - Models are pure Kotlin data classes

```kotlin
class GetLatestReadingUseCase @Inject constructor(
    private val deviceRepository: DeviceRepository
) {
    suspend operator fun invoke(deviceId: String): Resource<DeviceReading> {
        return deviceRepository.getLatestReading(deviceId)
    }
}

data class DeviceReading(
    val deviceId: String,
    val current: Double,
    val voltage: Double,
    val batteryLevel: Int,
    val rssi: Int? = null,
    val snr: Double? = null,
    val timestamp: Long,
    val frameCount: Int? = null,
    val gatewayId: String? = null,
    val status: String? = null,
    val rawData: String? = null
)

enum class ConnectionStatus {
    ONLINE,
    OFFLINE,
    UNKNOWN
}

sealed class Resource<T> {
    data class Success<T>(val data: T) : Resource<T>()
    data class Error<T>(val message: String, val data: T? = null) : Resource<T>()
    class Loading<T> : Resource<T>()
}
```

## Data Layer

The data layer provides data to the application. It includes repositories, which are the single source of truth for the application data, and data sources (local and remote).

### Key Components

#### Repositories
- **DeviceRepository**: Interface defining data operations
- **SettingsRepository**: Interface for app settings

#### Data Sources
- **Local**: Room database for persistent storage
- **Remote**: Retrofit for API communication
- **Preferences**: DataStore for settings

### Implementation Principles

1. **Repository Pattern**
   - Repositories abstract data sources
   - Provide clean API for domain layer
   - Handle data caching and synchronization

2. **Single Source of Truth**
   - Room database as the primary data source
   - Remote data synchronized to local storage

```kotlin
interface DeviceRepository {
    suspend fun getLatestReading(deviceId: String): Resource<DeviceReading>
    suspend fun getDeviceReadings(deviceId: String, limit: Int, offset: Int): Resource<List<DeviceReading>>
    suspend fun syncWithCloud(): Resource<Unit>
    fun getDeviceReadingsFlow(deviceId: String): Flow<List<DeviceReading>>
}

class DeviceRepositoryImpl @Inject constructor(
    private val chirpStackApi: ChirpStackApi,
    private val supabaseApi: SupabaseApi,
    private val localDatabase: DeviceReadingDao,
    private val networkManager: NetworkManager
) : DeviceRepository {
    
    override suspend fun getLatestReading(deviceId: String): Resource<DeviceReading> {
        return try {
            if (networkManager.isConnected()) {
                // Attempt to fetch from network
                val response = chirpStackApi.getDeviceFrames(deviceId, 1)
                if (response.isSuccessful && response.body() != null) {
                    val frames = response.body()!!.result
                    if (frames.isNotEmpty()) {
                        val frame = frames[0]
                        val reading = PayloadDecoder.decodePayload(frame.data).copy(
                            deviceId = deviceId,
                            frameCount = frame.fCnt,
                            timestamp = parseTimestamp(frame.time)
                        )
                        
                        // Cache in local database
                        localDatabase.insertReading(reading.toEntity())
                        
                        Resource.Success(reading)
                    } else {
                        getFallbackData(deviceId)
                    }
                } else {
                    getFallbackData(deviceId)
                }
            } else {
                getFallbackData(deviceId)
            }
        } catch (e: Exception) {
            Timber.e(e, "Error fetching latest reading")
            getFallbackData(deviceId)
        }
    }
    
    private suspend fun getFallbackData(deviceId: String): Resource<DeviceReading> {
        val cachedReading = localDatabase.getLatestReading(deviceId)
        return if (cachedReading != null) {
            Resource.Success(cachedReading.toDomain())
        } else {
            Resource.Error("No data available")
        }
    }
    
    // Other implementations...
}
```

## Dependency Injection

Dependency Injection is implemented using Hilt, which provides a standard way to incorporate dependency injection into an Android application.

### Key Components

#### Modules
- **NetworkModule**: Provides networking components
- **DatabaseModule**: Provides database components
- **RepositoryModule**: Provides repository implementations
- **UseCaseModule**: Provides use case implementations

### Implementation Principles

1. **Interface Abstraction**
   - Inject interfaces rather than concrete implementations
   - Makes testing easier through mocking

2. **Scoped Instances**
   - Use appropriate scoping for dependencies
   - Singleton for database, repositories
   - Activity/Fragment scope for ViewModels

```kotlin
@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    
    @Provides
    @Singleton
    fun provideDeviceRepository(
        chirpStackApi: ChirpStackApi,
        supabaseApi: SupabaseApi,
        deviceReadingDao: DeviceReadingDao,
        networkManager: NetworkManager
    ): DeviceRepository {
        return DeviceRepositoryImpl(
            chirpStackApi,
            supabaseApi,
            deviceReadingDao,
            networkManager
        )
    }
    
    @Provides
    @Singleton
    fun provideSettingsRepository(
        dataStore: DataStore<Preferences>,
        encryptedSharedPreferences: SharedPreferences
    ): SettingsRepository {
        return SettingsRepositoryImpl(dataStore, encryptedSharedPreferences)
    }
}
```

## Testing Strategy

The Clean Architecture approach makes the application highly testable by separating concerns and reducing dependencies.

### Key Testing Areas

1. **Unit Tests**
   - Test use cases with mocked repositories
   - Test repositories with mocked data sources
   - Test ViewModels with mocked use cases

2. **Integration Tests**
   - Test repository implementations with real data sources
   - Test database operations with real Room database

3. **UI Tests**
   - Test fragments with mocked ViewModels
   - Test end-to-end flows with Espresso

### Testing Examples

```kotlin
// ViewModel Test
@RunWith(MockitoJUnitRunner::class)
class DashboardViewModelTest {
    
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()
    
    @get:Rule
    val coroutineRule = MainCoroutineRule()
    
    @Mock
    private lateinit var getLatestReadingUseCase: GetLatestReadingUseCase
    
    @Mock
    private lateinit var settingsRepository: SettingsRepository
    
    private lateinit var viewModel: DashboardViewModel
    
    @Before
    fun setup() {
        `when`(settingsRepository.getRefreshIntervalFlow()).thenReturn(flowOf(30))
        
        viewModel = DashboardViewModel(
            getLatestReadingUseCase,
            settingsRepository
        )
    }
    
    @Test
    fun `refreshData should update state with success when use case returns success`() = runTest {
        // Given
        val deviceId = "device123"
        val reading = DeviceReading(
            deviceId = deviceId,
            current = 2.5,
            voltage = 12.0,
            batteryLevel = 85,
            timestamp = System.currentTimeMillis()
        )
        
        `when`(settingsRepository.getDeviceId()).thenReturn(deviceId)
        `when`(getLatestReadingUseCase(deviceId)).thenReturn(Resource.Success(reading))
        
        // When
        viewModel.refreshData()
        
        // Then
        val state = viewModel.uiState.value
        assertThat(state.isLoading).isFalse()
        assertThat(state.deviceReading).isEqualTo(reading)
        assertThat(state.connectionStatus).isEqualTo(ConnectionStatus.ONLINE)
        assertThat(state.error).isNull()
    }
    
    @Test
    fun `refreshData should update state with error when use case returns error`() = runTest {
        // Given
        val deviceId = "device123"
        val errorMessage = "Network error"
        
        `when`(settingsRepository.getDeviceId()).thenReturn(deviceId)
        `when`(getLatestReadingUseCase(deviceId)).thenReturn(Resource.Error(errorMessage))
        
        // When
        viewModel.refreshData()
        
        // Then
        val state = viewModel.uiState.value
        assertThat(state.isLoading).isFalse()
        assertThat(state.error).isEqualTo(errorMessage)
    }
}
```

## Best Practices

### Code Organization

1. **Package by Feature**
   - Group related classes together
   - Makes codebase more navigable

2. **Consistent Naming**
   - Clear and descriptive class names
   - Consistent method naming patterns

### Error Handling

1. **Resource Wrapper**
   - Use Resource class for all data operations
   - Handle loading, success, and error states

2. **User-Friendly Errors**
   - Convert technical errors to user-friendly messages
   - Provide actionable error messages

### Performance

1. **Efficient Data Loading**
   - Implement pagination for large datasets
   - Use appropriate threading for database operations

2. **Battery Optimization**
   - Adaptive refresh intervals
   - Batch network requests
   - Use WorkManager for background tasks 