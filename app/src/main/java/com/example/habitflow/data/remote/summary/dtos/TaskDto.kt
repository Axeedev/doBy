package com.example.habitflow.data.remote.summary.dtos

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TaskDto(
    @SerialName("title")
    val title: String,
    @SerialName("deadline")
    val deadline: String?,
    @SerialName("category")
    val category: String
)