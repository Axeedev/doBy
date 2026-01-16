package com.example.habitflow.data.local.tasks

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.habitflow.presentation.Priority

@Entity(tableName = "Tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val title: String,
    val date: String,
    val note: String,
    val category: String,
    val startTime: String,
    val endTime: String,
    val priority: Priority,
    val isCompleted: Boolean
)
