package com.example.habitflow.di

import com.example.habitflow.data.remote.Auth
import com.example.habitflow.data.remote.Speech
import com.example.habitflow.data.remote.voice.getUnsafeOkHttpClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Converter
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface AuthModule {

    companion object{

        private const val AUTH_URL = "https://ngw.devices.sberbank.ru:9443/"
        private const val VOICE_URL = "https://smartspeech.sber.ru/"

        @Auth
        @Provides
        @Singleton
        fun provideVoiceOkHttpClient(
            interceptor: HttpLoggingInterceptor
        ) : OkHttpClient{
            return getUnsafeOkHttpClient()
                .addInterceptor(interceptor)
                .readTimeout(60_000, TimeUnit.SECONDS)
                .build()

        }

        @Speech
        @Provides
        @Singleton
        fun provideRetrofitVoice(
            @Auth okHttpClient: OkHttpClient,
            converterFactory: Converter.Factory
        ): Retrofit {
            return Retrofit.Builder()
                .baseUrl(VOICE_URL)
                .addConverterFactory(converterFactory)
                .client(okHttpClient)
                .build()
        }

        @Auth
        @Provides
        @Singleton
        fun provideRetrofitAuth(
            converter: Converter.Factory,
            @Auth okHttpClient: OkHttpClient,
        ): Retrofit = Retrofit.Builder()
            .baseUrl(AUTH_URL)
            .client(okHttpClient)
            .addConverterFactory(converter)
            .build()

    }
}