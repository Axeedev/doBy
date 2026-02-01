package com.example.habitflow.data.local.tasks

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(
    tableName = "completedTasks"
)
data class CompletedTaskEntity(
    @PrimaryKey
    val id: Int,
    val title: String,
    val deadlineMillis: Long?,
    val note: String,
    val category: String,
    val priority: String,
    val isCompleted: Boolean = true,
    val completedAt: Long
)
