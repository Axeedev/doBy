package com.example.habitflow.domain.repository

import com.example.habitflow.domain.entities.achievements.Achievement
import kotlinx.coroutines.flow.Flow

interface AchievementRepository {

    fun getAchievements() : Flow<List<Achievement>>

    fun getUnlockedAchievements() : Flow<List<Achievement>>

    fun getLockedAchievements() : Flow<List<Achievement>>

    suspend fun onTaskCompleted() : List<Achievement>

}