package com.example.ampmeter.di.module

import android.content.Context
import com.example.ampmeter.data.local.database.AppDatabase
import com.example.ampmeter.data.local.database.dao.DeviceReadingDao
import com.example.ampmeter.data.local.datastore.SettingsDataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    
    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.getInstance(context)
    }
    
    @Provides
    @Singleton
    fun provideDeviceReadingDao(appDatabase: AppDatabase): DeviceReadingDao {
        return appDatabase.deviceReadingDao()
    }
    
    @Provides
    @Singleton
    fun provideSettingsDataStore(@ApplicationContext context: Context): SettingsDataStore {
        return SettingsDataStore(context)
    }
} 