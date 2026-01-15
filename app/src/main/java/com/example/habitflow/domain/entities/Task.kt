package com.example.habitflow.domain.entities

import java.util.UUID

data class Task(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val date: String,
    val note: String,
    val startTime: String,
    val endTime: String,
    val category: String,
    val isCompleted: Boolean = false
)
