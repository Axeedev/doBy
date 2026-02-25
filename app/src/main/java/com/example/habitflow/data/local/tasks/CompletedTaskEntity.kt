package com.example.habitflow.data.local.tasks

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.habitflow.domain.entities.goals.GoalCategory


@Entity(
    tableName = "completedTasks"
)
data class CompletedTaskEntity(
    @PrimaryKey
    val id: Int,
    val remoteId: String? = null,
    val title: String,
    val deadlineMillis: Long?,
    val note: String,
    val category: String,
    val priority: String,
    val isCompleted: Boolean = true,
    val completedAt: Long,
    val updatedAt: Long = System.currentTimeMillis(),
    val isSynced: Boolean = false,
)
