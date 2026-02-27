package com.example.habitflow.di

import com.example.habitflow.data.remote.Auth
import com.example.habitflow.data.remote.Speech
import com.example.habitflow.data.remote.voice.SaluteAuthService
import com.example.habitflow.data.remote.voice.SaluteSpeechApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.create
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
interface SpeechToTextModule {

    companion object{

        @Singleton
        @Provides
        fun provideSaluteSpeechAuthService(@Auth retrofit: Retrofit) =
            retrofit.create<SaluteAuthService>()

        @Singleton
        @Provides
        fun provideSaluteSpeechApi(
            @Speech retrofit: Retrofit
        ): SaluteSpeechApiService = retrofit.create()

    }
}