package com.example.habitflow.data.local.achievements

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.habitflow.domain.entities.achievements.AchievementType

@Entity(
    tableName = "achievements"
)
data class AchievementEntity(
    @PrimaryKey
    val id: Int = 0,
    val title: String,
    val description: String,
    val targetGoal: Int,
    val currentProgress: Int = 0,
    val achievementType: AchievementType,
    val isUnlocked: Boolean = false,
    val iconId: Int,
    val dateOfUnlock: Long? = null,
    val progress: Int = (currentProgress.toFloat() / targetGoal.toFloat()).toInt()
)