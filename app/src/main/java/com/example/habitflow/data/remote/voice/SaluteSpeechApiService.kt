package com.example.habitflow.data.remote.voice

import com.example.habitflow.data.remote.voice.dtos.SaluteRecognitionResponse
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

interface SaluteSpeechApiService {

    @Headers(
        "Accept: application/json",
        "Content-Type: audio/x-pcm"
    )
    @POST("rest/v1/speech:recognize")
    suspend fun recognizeSpeech(
        @Header("Authorization") authorization: String,
        @Body audio: RequestBody,
    ): Response<SaluteRecognitionResponse>
}