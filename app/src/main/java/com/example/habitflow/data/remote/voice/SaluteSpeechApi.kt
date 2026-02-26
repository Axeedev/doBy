package com.example.habitflow.data.remote.voice

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

interface SaluteSpeechApi {

    @Headers(
        "Accept: application/json",
        "Content-Type: audio/mpeg"
    )
    @POST("rest/v1/speech:recognize")
    suspend fun recognizeSpeech(
        @Header("Authorization") authorization: String,
        @Body audio: RequestBody,
    ): Response<SaluteRecognitionResponse>
}



@Serializable
data class SaluteRecognitionResponse(
    @SerialName("result")
    val result: List<String>,
    @SerialName("emotions")
    val emotions: List<Emotion>,
    @SerialName("person_identity")
    val personIdentity: PersonIdentity,
    @SerialName("status")
    val status: Int
)


@Serializable
data class Emotion(
    @SerialName("negative")
    val negative: Double,
    @SerialName("neutral")
    val neutral: Double,
    @SerialName("positive")
    val positive: Double
)

@Serializable
data class PersonIdentity(
    @SerialName("age")
    val age: String,
    @SerialName("gender")
    val gender: String,
    @SerialName("age_score")
    val age_score: Int,
    @SerialName("gender_score")
    val gender_score: Int
)