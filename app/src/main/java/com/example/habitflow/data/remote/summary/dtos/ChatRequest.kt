package com.example.habitflow.data.remote.summary.dtos

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ChatRequest(
    @SerialName("model")
    val model: String,
    @SerialName("messages")
    val messages: List<Message>,
    @SerialName("n")
    val n: Int = 1,
    @SerialName("stream")
    val stream: Boolean = false,
    @SerialName("max_tokens")
    val maxTokens: Int = 512,
    @SerialName("repetition_penalty")
    val repetitionPenalty: Double = 1.0,
    @SerialName("update_interval")
    val updateInterval: Int = 0
)