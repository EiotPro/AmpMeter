package com.example.ampmeter

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.TextView
import android.widget.Toast

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        findViewById<TextView>(R.id.textView).text = "AmpMeter App"
        
        Toast.makeText(this, "App started successfully", Toast.LENGTH_SHORT).show()
    }
}