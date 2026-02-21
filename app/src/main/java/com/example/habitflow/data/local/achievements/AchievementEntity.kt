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
    val titleId: Int,
    val descriptionId: Int,
    val targetGoal: Int,
    val currentProgress: Int = 0,
    val achievementType: AchievementType,
    val achievementCode: String = AchievementCodes.BASIC_ACHIEVEMENT,
    val isUnlocked: Boolean = false,
    val iconId: Int,
    val dateOfUnlock: Long? = null,
    val lastDateOfCompletion: String? = null
)