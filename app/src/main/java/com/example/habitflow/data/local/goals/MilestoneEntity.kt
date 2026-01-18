package com.example.habitflow.data.local.goals

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "milestones",
    foreignKeys = [
        ForeignKey(
            entity = GoalEntity::class,
            parentColumns = ["id"],
            childColumns = ["goalId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class MilestoneEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val title: String,
    val isCompleted: Boolean,
    val goalId: Long
)
