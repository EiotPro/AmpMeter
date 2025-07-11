package com.example.ampmeter.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.ampmeter.data.local.database.dao.DeviceReadingDao
import com.example.ampmeter.data.local.database.entity.DeviceReadingEntity

/**
 * Room database for the application.
 */
@Database(
    entities = [DeviceReadingEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    
    /**
     * Gets DeviceReadingDao.
     */
    abstract fun deviceReadingDao(): DeviceReadingDao
    
    companion object {
        private const val DATABASE_NAME = "ampmeter_database"
        
        @Volatile
        private var INSTANCE: AppDatabase? = null
        
        /**
         * Gets the database instance.
         */
        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    DATABASE_NAME
                )
                .fallbackToDestructiveMigration()
                .build()
                
                INSTANCE = instance
                instance
            }
        }
    }
} 