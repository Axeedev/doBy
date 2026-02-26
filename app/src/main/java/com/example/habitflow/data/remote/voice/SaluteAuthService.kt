package com.example.habitflow.data.remote.voice

import com.example.habitflow.BuildConfig
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
        @Header("Authorization") authorization: String = "Basic ${BuildConfig.AUTH_KEY}",
        @Header("RqUID") rqUid: String = BuildConfig.UID,
        @Field("scope") scope: String = "SALUTE_SPEECH_PERS"
    ): Response<SaluteTokenResponse>
}

