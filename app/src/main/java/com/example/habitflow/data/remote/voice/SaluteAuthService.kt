package com.example.habitflow.data.remote.voice

import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

interface SaluteAuthService {
    @Headers(
        "Content-Type: application/x-www-form-urlencoded",
        "Accept: application/json"
    )
    @FormUrlEncoded
    @POST("api/v2/oauth")
    suspend fun getToken(
        @Header("Authorization") authorization: String,
        @Header("RqUID") rqUid: String,
        @Field("scope") scope: String = "SALUTE_SPEECH_PERS"
    ): Response<SaluteTokenResponse>
}