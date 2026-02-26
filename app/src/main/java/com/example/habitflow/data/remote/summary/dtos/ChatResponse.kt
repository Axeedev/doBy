package com.example.habitflow.data.remote.summary.dtos

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ChatResponse(
    @SerialName("choices")
    val choices: List<Choice>
)