package com.example.habitflow.data.remote.voice.dtos

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

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
