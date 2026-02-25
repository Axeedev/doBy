package com.example.habitflow.data.local.goals

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.habitflow.domain.entities.goals.GoalCategory

@Entity(
    tableName = "Goals"
)
data class GoalEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val title: String,
    val category: String,
    val startDate: Long,
    val endDate: Long,
    val description: String,
    val coverUri: String? = null
)
