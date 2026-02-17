package com.example.habitflow.domain.repository

import com.example.habitflow.domain.entities.achievements.Achievement
import kotlinx.coroutines.flow.Flow

interface AchievementRepository {

    fun getAchievements() : Flow<List<Achievement>>

    fun getUnlockedAchievements() : Flow<List<Achievement>>

    fun getLockedAchievements() : Flow<List<Achievement>>

    fun getCurrentStreak() : Flow<Int>

    suspend fun onTaskCompleted() : Boolean

    suspend fun onGoalCompleted() : Boolean

    suspend fun updateDayStreak() : Boolean

}