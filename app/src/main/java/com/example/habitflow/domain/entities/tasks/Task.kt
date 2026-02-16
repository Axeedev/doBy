package com.example.habitflow.domain.entities.tasks

import com.example.habitflow.domain.entities.goals.GoalCategory

data class Task(
    val id: Int,
    val title: String,
    val deadlineMillis: Long?,
    val note: String,
    val category: GoalCategory,
    val priority: Priority,
    val isCompleted: Boolean = false,
    val isReturned: Boolean = false
)
