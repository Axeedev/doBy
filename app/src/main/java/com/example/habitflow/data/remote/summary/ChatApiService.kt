package com.example.habitflow.data.remote.summary

import com.example.habitflow.data.remote.summary.dtos.ChatRequest
import com.example.habitflow.data.remote.summary.dtos.ChatResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

interface ChatApiService {

    @Headers(
        "Content-Type: application/json",
        "Accept: application/json"
    )
    @POST("chat/completions")
    suspend fun getSummary(
        @Header("Authorization") authorization: String,
        @Body chatRequest: ChatRequest
    ) : Response<ChatResponse>
}










