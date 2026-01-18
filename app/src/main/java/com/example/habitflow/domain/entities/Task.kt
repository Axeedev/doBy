package com.example.habitflow.domain.entities

data class Task(
    val id: Int,
    val title: String,
    val date: String,
    val note: String,
    val startTime: Long,
    val endTime: Long,
    val category: TaskCategory,
    val priority: Priority,
    val isCompleted: Boolean = false
)
