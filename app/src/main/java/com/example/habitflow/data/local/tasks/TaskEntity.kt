package com.example.habitflow.data.local.tasks

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.habitflow.domain.entities.goals.GoalCategory

@Entity(tableName = "Tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val title: String,
    val deadlineMillis: Long?,
    val note: String,
    val category: GoalCategory,
    val priority: String,
    val isCompleted: Boolean,
    val isReturned: Boolean
)
