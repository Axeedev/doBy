package com.example.habitflow.data.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AdviceDto(
    @SerialName("advice")
    val advice: String
)
