package com.example.habitflow.domain.entities.tasks

import com.example.habitflow.domain.entities.Category

data class CompletedTask(
    val id: Int,
    val title: String,
    val deadlineMillis: Long?,
    val note: String,
    val category: Category,
    val priority: Priority,
    val isCompleted: Boolean = true,
    val completionDate: Long
)
