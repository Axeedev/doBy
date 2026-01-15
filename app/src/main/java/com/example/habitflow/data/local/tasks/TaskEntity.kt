package com.example.habitflow.data.local.tasks

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Tasks")
data class TaskEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val date: String,
    val note: String,
    val category: String,
    val startTime: String,
    val endTime: String,
    val isCompleted: Boolean
)
