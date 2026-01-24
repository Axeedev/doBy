package com.example.habitflow.data.local.tasks

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.habitflow.domain.entities.Priority

@Entity(tableName = "Tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val title: String,
    val date: Long?,
    val note: String,
    val category: String,
    val remindAtMinutesOfDay: Int? = null,
    val priority: String,
    val isCompleted: Boolean
)
