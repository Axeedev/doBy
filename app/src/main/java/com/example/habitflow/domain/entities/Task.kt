package com.example.habitflow.domain.entities

data class Task(
    val id: String,
    val title: String,
    val date: String,
    val note: String,
    val isCompleted: Boolean
)
