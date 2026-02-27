package com.example.habitflow.di

import com.example.habitflow.data.remote.Auth
import com.example.habitflow.data.remote.Chat
import com.example.habitflow.data.remote.summary.ChatApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.create
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface ChatNetworkModule {

    companion object{

        private const val CHAT_URL = "https://gigachat.devices.sberbank.ru/api/v1/"

        @Singleton
        @Provides
        @Chat
        fun provideChatRetrofit(
            converter: Converter.Factory,
            @Auth okHttpClient: OkHttpClient
        ) : Retrofit{
            return Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl(CHAT_URL)
                .addConverterFactory(converter)
                .build()
        }

        @Singleton
        @Provides
        fun provideChatApi(
            @Chat retrofit: Retrofit
        ) : ChatApiService = retrofit.create()

    }
}