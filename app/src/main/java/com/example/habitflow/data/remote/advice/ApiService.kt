package com.example.habitflow.data.remote.advice

import retrofit2.http.GET

interface ApiService {

    @GET("advice")
    suspend fun getRandomAdvice(): AdviceDto
}