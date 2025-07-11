package com.example.ampmeter.di.module

import com.example.ampmeter.BuildConfig
import com.example.ampmeter.data.remote.api.ChirpStackApi
import com.example.ampmeter.domain.repository.SettingsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    
    @Provides
    @Singleton
    fun provideOkHttpClient(
        settingsRepository: SettingsRepository
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor { chain ->
                val originalRequest = chain.request()
                
                // Run in a non-blocking way
                val apiKey = runCatching {
                    kotlinx.coroutines.runBlocking { settingsRepository.getChirpStackApiKey() }
                }.getOrDefault("")
                
                val newRequest = originalRequest.newBuilder()
                    .apply {
                        if (apiKey.isNotEmpty()) {
                            header("Authorization", "Bearer $apiKey")
                        }
                    }
                    .build()
                    
                chain.proceed(newRequest)
            }
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = if (BuildConfig.DEBUG) {
                    HttpLoggingInterceptor.Level.BODY
                } else {
                    HttpLoggingInterceptor.Level.NONE
                }
            })
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }
    
    @Provides
    @Singleton
    fun provideRetrofit(
        okHttpClient: OkHttpClient,
        settingsRepository: SettingsRepository
    ): Retrofit {
        // Default URL to use before settings are loaded
        val defaultUrl = "https://example.com/"
        
        // Get server URL from settings, or use default
        val serverUrl = runCatching {
            kotlinx.coroutines.runBlocking { settingsRepository.getChirpStackServerUrl() }
        }.getOrDefault(defaultUrl)
        
        return Retrofit.Builder()
            .baseUrl(serverUrl.ifEmpty { defaultUrl })
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    
    @Provides
    @Singleton
    fun provideChirpStackApi(retrofit: Retrofit): ChirpStackApi {
        return retrofit.create(ChirpStackApi::class.java)
    }
} 