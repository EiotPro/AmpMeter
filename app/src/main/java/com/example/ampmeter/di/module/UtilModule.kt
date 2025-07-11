package com.example.ampmeter.di.module

import android.content.Context
import com.example.ampmeter.util.network.NetworkManager
import com.example.ampmeter.util.network.NetworkManagerImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UtilModule {
    
    @Provides
    @Singleton
    fun provideNetworkManager(@ApplicationContext context: Context): NetworkManager {
        return NetworkManagerImpl(context)
    }
} 