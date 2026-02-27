package com.example.habitflow.di

import com.example.habitflow.data.remote.Speech
import com.example.habitflow.data.remote.advice.ApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.create
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface AppModule {

    companion object {


        private const val X_API_KEY = "X-Api-Key"
        private const val BASE_URL = "https://api.api-ninjas.com/v1/"

        @Provides
        @Singleton
        fun provideOkHttpClient(
            interceptor: HttpLoggingInterceptor
        ): OkHttpClient {
            return OkHttpClient
                .Builder()
                .addInterceptor(interceptor)
                .addInterceptor { chain ->
                    val request = chain.request()
                    val newRequest = request.newBuilder()
                        .addHeader(X_API_KEY, "0zoR+3VH8MaKmZxowuX+UA==yZxeFcZfj4Av9V7V")
                        .build()
                    chain.proceed(newRequest)
                }
                .build()
        }

        @Provides
        @Singleton
        fun provideApiService(@Speech retrofit: Retrofit): ApiService {
            return retrofit.create()
        }
    }
}