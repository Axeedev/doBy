package com.example.habitflow.domain.entities

data class Task(
    val id: Int,
    val title: String,
    val date: Long?,
    val note: String,
    val category: GoalCategory,
    val priority: Priority,
    val remindAtMinutesOfDay: Int? = null,
    val isCompleted: Boolean = false
)
