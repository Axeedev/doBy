package com.example.habitflow.data.remote.voice

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class SaluteTokenResponse(
    @SerialName("access_token")
    val accessToken: String,
    @SerialName("expires_at")
    val expiresAt: Long
)