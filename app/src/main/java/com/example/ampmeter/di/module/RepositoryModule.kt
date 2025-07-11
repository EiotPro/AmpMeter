package com.example.ampmeter.di.module

import com.example.ampmeter.data.repository.DeviceRepositoryImpl
import com.example.ampmeter.data.repository.SettingsRepositoryImpl
import com.example.ampmeter.domain.repository.DeviceRepository
import com.example.ampmeter.domain.repository.SettingsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    
    @Binds
    @Singleton
    abstract fun bindSettingsRepository(
        settingsRepositoryImpl: SettingsRepositoryImpl
    ): SettingsRepository
    
    @Binds
    @Singleton
    abstract fun bindDeviceRepository(
        deviceRepositoryImpl: DeviceRepositoryImpl
    ): DeviceRepository
} 