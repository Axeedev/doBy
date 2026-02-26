package com.example.habitflow.data.remote.voice.dtos

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class Emotion(
    @SerialName("negative")
    val negative: Double,
    @SerialName("neutral")
    val neutral: Double,
    @SerialName("positive")
    val positive: Double
)