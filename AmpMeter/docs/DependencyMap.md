# AmpMeter Dependency Map

This document outlines all the dependencies required for the AmpMeter Android application, categorized by functionality.

## Core Dependencies

### Android Core
```kotlin
// Core Android components
implementation(libs.androidx.core.ktx)
implementation(libs.androidx.appcompat)
implementation(libs.material)
implementation(libs.androidx.constraintlayout)
```

### Architecture Components
```kotlin
// Lifecycle components
implementation(libs.androidx.lifecycle.livedata.ktx)
implementation(libs.androidx.lifecycle.viewmodel.ktx)
implementation(libs.androidx.lifecycle.runtime.ktx)

// Navigation
implementation(libs.androidx.navigation.fragment.ktx)
implementation(libs.androidx.navigation.ui.ktx)
```

## Dependency Injection

### Hilt
```kotlin
// Hilt for dependency injection
implementation(libs.hilt.android)
kapt(libs.hilt.compiler)

// Hilt with ViewModel integration
implementation(libs.androidx.hilt.navigation.fragment)
```

## Networking

### Retrofit & OkHttp
```kotlin
// Retrofit for API communication
implementation(libs.retrofit)
implementation(libs.retrofit.converter.gson)

// OkHttp for HTTP client
implementation(libs.okhttp)
implementation(libs.okhttp.logging.interceptor)

// Gson for JSON parsing
implementation(libs.gson)
```

### Supabase
```kotlin
// Supabase for cloud database
implementation(libs.supabase.postgrest)
implementation(libs.supabase.realtime)
implementation(libs.supabase.storage)
```

## Local Storage

### Room
```kotlin
// Room for local database
implementation(libs.androidx.room.runtime)
implementation(libs.androidx.room.ktx)
kapt(libs.androidx.room.compiler)
```

### DataStore
```kotlin
// DataStore for preferences
implementation(libs.androidx.datastore.preferences)
```

## Async Programming

### Coroutines
```kotlin
// Coroutines for asynchronous programming
implementation(libs.kotlinx.coroutines.core)
implementation(libs.kotlinx.coroutines.android)
```

## UI Components

### Material Design
```kotlin
// Material Design components
implementation(libs.material)

// Material Dialogs
implementation(libs.material.dialogs.core)
implementation(libs.material.dialogs.datetime)
```

### RecyclerView
```kotlin
// RecyclerView for lists
implementation(libs.androidx.recyclerview)

// Paging for large datasets
implementation(libs.androidx.paging.runtime.ktx)
```

### Image Loading
```kotlin
// Glide for image loading
implementation(libs.glide)
kapt(libs.glide.compiler)
```

### Charts
```kotlin
// MPAndroidChart for data visualization
implementation(libs.mpandroidchart)
```

## Utility Libraries

### Logging
```kotlin
// Timber for logging
implementation(libs.timber)
```

### Time
```kotlin
// ThreeTenABP for date/time handling
implementation(libs.threetenabp)
```

### Work Manager
```kotlin
// WorkManager for background tasks
implementation(libs.androidx.work.runtime.ktx)
```

## Security
```kotlin
// Android Security for encrypting preferences
implementation(libs.androidx.security.crypto)
```

## Testing Dependencies

### Unit Testing
```kotlin
// JUnit for unit tests
testImplementation(libs.junit)

// Mockito for mocking
testImplementation(libs.mockito.core)
testImplementation(libs.mockito.kotlin)

// Coroutines test
testImplementation(libs.kotlinx.coroutines.test)

// Assertion library
testImplementation(libs.truth)

// Architecture Components testing
testImplementation(libs.androidx.arch.core.testing)
```

### UI Testing
```kotlin
// Espresso for UI tests
androidTestImplementation(libs.androidx.espresso.core)
androidTestImplementation(libs.androidx.espresso.contrib)

// UI Automator
androidTestImplementation(libs.androidx.test.uiautomator)

// Testing navigation
androidTestImplementation(libs.androidx.navigation.testing)
```

## Complete Version Catalog (libs.versions.toml)

```toml
[versions]
# Core
coreKtx = "1.16.0"
appcompat = "1.7.0"
material = "1.12.0"
constraintlayout = "2.2.1"

# Architecture Components
lifecycleVersion = "2.9.1"
navigationVersion = "2.9.0"
roomVersion = "2.6.1"

# Dependency Injection
hiltVersion = "2.50"
hiltNavigationVersion = "1.2.0"

# Networking
retrofitVersion = "2.9.0"
okhttpVersion = "4.11.0"
gsonVersion = "2.10.1"
supabaseVersion = "2.0.0"

# Async
coroutinesVersion = "1.8.0"

# UI
materialDialogsVersion = "3.3.0"
recyclerviewVersion = "1.3.2"
pagingVersion = "3.3.0"
glideVersion = "4.16.0"
mpandroidchartVersion = "3.1.0"

# Storage
datastoreVersion = "1.0.0"

# Security
securityVersion = "1.1.0"

# Utility
timberVersion = "5.0.1"
threetenabpVersion = "1.4.6"
workVersion = "2.9.0"

# Testing
junitVersion = "4.13.2"
mockitoVersion = "5.2.0"
mockitoKotlinVersion = "5.2.1"
truthVersion = "1.4.1"
archTestingVersion = "2.2.0"
espressoVersion = "3.6.1"
uiautomatorVersion = "2.3.0"

[libraries]
# Core
androidx-core-ktx = { group = "androidx.core", name = "core-ktx", version.ref = "coreKtx" }
androidx-appcompat = { group = "androidx.appcompat", name = "appcompat", version.ref = "appcompat" }
material = { group = "com.google.android.material", name = "material", version.ref = "material" }
androidx-constraintlayout = { group = "androidx.constraintlayout", name = "constraintlayout", version.ref = "constraintlayout" }

# Architecture Components
androidx-lifecycle-livedata-ktx = { group = "androidx.lifecycle", name = "lifecycle-livedata-ktx", version.ref = "lifecycleVersion" }
androidx-lifecycle-viewmodel-ktx = { group = "androidx.lifecycle", name = "lifecycle-viewmodel-ktx", version.ref = "lifecycleVersion" }
androidx-lifecycle-runtime-ktx = { group = "androidx.lifecycle", name = "lifecycle-runtime-ktx", version.ref = "lifecycleVersion" }
androidx-navigation-fragment-ktx = { group = "androidx.navigation", name = "navigation-fragment-ktx", version.ref = "navigationVersion" }
androidx-navigation-ui-ktx = { group = "androidx.navigation", name = "navigation-ui-ktx", version.ref = "navigationVersion" }
androidx-room-runtime = { group = "androidx.room", name = "room-runtime", version.ref = "roomVersion" }
androidx-room-ktx = { group = "androidx.room", name = "room-ktx", version.ref = "roomVersion" }
androidx-room-compiler = { group = "androidx.room", name = "room-compiler", version.ref = "roomVersion" }

# Dependency Injection
hilt-android = { group = "com.google.dagger", name = "hilt-android", version.ref = "hiltVersion" }
hilt-compiler = { group = "com.google.dagger", name = "hilt-android-compiler", version.ref = "hiltVersion" }
androidx-hilt-navigation-fragment = { group = "androidx.hilt", name = "hilt-navigation-fragment", version.ref = "hiltNavigationVersion" }

# Networking
retrofit = { group = "com.squareup.retrofit2", name = "retrofit", version.ref = "retrofitVersion" }
retrofit-converter-gson = { group = "com.squareup.retrofit2", name = "converter-gson", version.ref = "retrofitVersion" }
okhttp = { group = "com.squareup.okhttp3", name = "okhttp", version.ref = "okhttpVersion" }
okhttp-logging-interceptor = { group = "com.squareup.okhttp3", name = "logging-interceptor", version.ref = "okhttpVersion" }
gson = { group = "com.google.code.gson", name = "gson", version.ref = "gsonVersion" }
supabase-postgrest = { group = "io.github.jan-tennert.supabase", name = "postgrest-kt", version.ref = "supabaseVersion" }
supabase-realtime = { group = "io.github.jan-tennert.supabase", name = "realtime-kt", version.ref = "supabaseVersion" }
supabase-storage = { group = "io.github.jan-tennert.supabase", name = "storage-kt", version.ref = "supabaseVersion" }

# Async
kotlinx-coroutines-core = { group = "org.jetbrains.kotlinx", name = "kotlinx-coroutines-core", version.ref = "coroutinesVersion" }
kotlinx-coroutines-android = { group = "org.jetbrains.kotlinx", name = "kotlinx-coroutines-android", version.ref = "coroutinesVersion" }

# UI
material-dialogs-core = { group = "com.afollestad.material-dialogs", name = "core", version.ref = "materialDialogsVersion" }
material-dialogs-datetime = { group = "com.afollestad.material-dialogs", name = "datetime", version.ref = "materialDialogsVersion" }
androidx-recyclerview = { group = "androidx.recyclerview", name = "recyclerview", version.ref = "recyclerviewVersion" }
androidx-paging-runtime-ktx = { group = "androidx.paging", name = "paging-runtime-ktx", version.ref = "pagingVersion" }
glide = { group = "com.github.bumptech.glide", name = "glide", version.ref = "glideVersion" }
glide-compiler = { group = "com.github.bumptech.glide", name = "compiler", version.ref = "glideVersion" }
mpandroidchart = { group = "com.github.PhilJay", name = "MPAndroidChart", version.ref = "mpandroidchartVersion" }

# Storage
androidx-datastore-preferences = { group = "androidx.datastore", name = "datastore-preferences", version.ref = "datastoreVersion" }

# Security
androidx-security-crypto = { group = "androidx.security", name = "security-crypto", version.ref = "securityVersion" }

# Utility
timber = { group = "com.jakewharton.timber", name = "timber", version.ref = "timberVersion" }
threetenabp = { group = "com.jakewharton.threetenabp", name = "threetenabp", version.ref = "threetenabpVersion" }
androidx-work-runtime-ktx = { group = "androidx.work", name = "work-runtime-ktx", version.ref = "workVersion" }

# Testing
junit = { group = "junit", name = "junit", version.ref = "junitVersion" }
mockito-core = { group = "org.mockito", name = "mockito-core", version.ref = "mockitoVersion" }
mockito-kotlin = { group = "org.mockito.kotlin", name = "mockito-kotlin", version.ref = "mockitoKotlinVersion" }
truth = { group = "com.google.truth", name = "truth", version.ref = "truthVersion" }
androidx-arch-core-testing = { group = "androidx.arch.core", name = "core-testing", version.ref = "archTestingVersion" }
kotlinx-coroutines-test = { group = "org.jetbrains.kotlinx", name = "kotlinx-coroutines-test", version.ref = "coroutinesVersion" }
androidx-espresso-core = { group = "androidx.test.espresso", name = "espresso-core", version.ref = "espressoVersion" }
androidx-espresso-contrib = { group = "androidx.test.espresso", name = "espresso-contrib", version.ref = "espressoVersion" }
androidx-test-uiautomator = { group = "androidx.test.uiautomator", name = "uiautomator", version.ref = "uiautomatorVersion" }
androidx-navigation-testing = { group = "androidx.navigation", name = "navigation-testing", version.ref = "navigationVersion" }

[plugins]
android-application = { id = "com.android.application", version = "8.10.0" }
android-library = { id = "com.android.library", version = "8.10.0" }
kotlin-android = { id = "org.jetbrains.kotlin.android", version = "2.0.21" }
kotlin-kapt = { id = "org.jetbrains.kotlin.kapt", version = "2.0.21" }
hilt-android = { id = "com.google.dagger.hilt.android", version = "2.50" }
```

## Installation Instructions

To add these dependencies to your project:

1. Replace the contents of your `gradle/libs.versions.toml` file with the complete version catalog above.

2. Update your app-level `build.gradle.kts` file to include these dependencies:

```kotlin
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.kapt)
    alias(libs.plugins.hilt.android)
}

android {
    // Existing configuration...
    
    // Enable ViewBinding
    buildFeatures {
        viewBinding = true
    }
    
    // Specify Java compatibility
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {
    // Core dependencies
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)
    
    // Architecture components
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    
    // Dependency injection
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)
    implementation(libs.androidx.hilt.navigation.fragment)
    
    // Networking
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.gson)
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging.interceptor)
    implementation(libs.gson)
    
    // Supabase
    implementation(libs.supabase.postgrest)
    implementation(libs.supabase.realtime)
    implementation(libs.supabase.storage)
    
    // Room
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    kapt(libs.androidx.room.compiler)
    
    // DataStore
    implementation(libs.androidx.datastore.preferences)
    
    // Coroutines
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)
    
    // UI components
    implementation(libs.material.dialogs.core)
    implementation(libs.material.dialogs.datetime)
    implementation(libs.androidx.recyclerview)
    implementation(libs.androidx.paging.runtime.ktx)
    implementation(libs.glide)
    kapt(libs.glide.compiler)
    implementation(libs.mpandroidchart)
    
    // Security
    implementation(libs.androidx.security.crypto)
    
    // Utilities
    implementation(libs.timber)
    implementation(libs.threetenabp)
    implementation(libs.androidx.work.runtime.ktx)
    
    // Testing
    testImplementation(libs.junit)
    testImplementation(libs.mockito.core)
    testImplementation(libs.mockito.kotlin)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.truth)
    testImplementation(libs.androidx.arch.core.testing)
    
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.espresso.contrib)
    androidTestImplementation(libs.androidx.test.uiautomator)
    androidTestImplementation(libs.androidx.navigation.testing)
}
```

3. Update your project-level `build.gradle.kts` file:

```kotlin
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.kapt) apply false
    alias(libs.plugins.hilt.android) apply false
}
```

4. Add the Google Maven repository to your `settings.gradle.kts` file if it's not already there:

```kotlin
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") } // For MPAndroidChart
    }
}
```

5. Sync your project with Gradle files to download all dependencies.

## Optional Dependencies

Depending on your implementation needs, you might also want to consider:

1. **Firebase** - For analytics, crash reporting, or push notifications
2. **Biometric** - For biometric authentication
3. **Camera** - For QR code scanning or photo capabilities
4. **Maps** - For location features 