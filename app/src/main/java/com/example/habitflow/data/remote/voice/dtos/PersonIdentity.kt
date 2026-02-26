package com.example.habitflow.data.remote.voice.dtos

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PersonIdentity(
    @SerialName("age")
    val age: String,
    @SerialName("gender")
    val gender: String,
    @SerialName("age_score")
    val ageScore: Int,
    @SerialName("gender_score")
    val genderScore: Int
)