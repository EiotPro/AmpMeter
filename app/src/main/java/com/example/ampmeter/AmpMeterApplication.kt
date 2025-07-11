package com.example.ampmeter

import android.app.Application
import android.util.Log

class AmpMeterApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
        
        // Initialize logging
        Log.d("AmpMeter", "Application started")
    }
} 