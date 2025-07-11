plugins {
    id("com.android.application")
    id("kotlin-android")
}

android {
    namespace = "com.example.ampmeter"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.ampmeter"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        debug {
            isMinifyEnabled = false
        }
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    
    // Disable unnecessary build features
    packagingOptions {
        resources.excludes.add("META-INF/*")
    }
}

// Disable unnecessary tasks
tasks.configureEach {
    if (name.contains("lint") || 
        name.contains("test") || 
        name.contains("androidTest")) {
        enabled = false
    }
}

dependencies {
    // Core Android components - absolute minimal set
    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
}