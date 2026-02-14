package com.example.habitflow.data.repository

import com.example.habitflow.data.local.achievements.AchievementsDao
import com.example.habitflow.data.mappers.toAchievement
import com.example.habitflow.domain.entities.achievements.Achievement
import com.example.habitflow.domain.entities.achievements.AchievementType
import com.example.habitflow.domain.repository.AchievementRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class AchievementRepositoryImpl @Inject constructor(
    private val dao: AchievementsDao
): AchievementRepository{

    override suspend fun onTaskCompleted(): List<Achievement> {

        val lockedAchievements = dao.getLockedAchievementsByType(AchievementType.TASKS_COMPLETED)
        val unlockedNow = mutableListOf<Achievement>()

        lockedAchievements.forEach {achievementEntity ->

            val newProgress = achievementEntity.currentProgress + 1
            val isAchievementUnlocked = newProgress == achievementEntity.targetGoal


            if (isAchievementUnlocked){
                dao.updateAchievement(
                    achievementEntity.copy(
                        currentProgress = newProgress,
                        isUnlocked = true,
                        dateOfUnlock = System.currentTimeMillis(),
                        progress = (newProgress.toFloat() / achievementEntity.targetGoal.toFloat() * 100f).toInt()
                    )
                )

                unlockedNow.add(achievementEntity.toAchievement())
            }else{

                dao.updateAchievement(
                    achievementEntity.copy(
                        currentProgress = newProgress,
                        progress = (newProgress.toFloat() / achievementEntity.targetGoal.toFloat() * 100f).toInt()
                    )
                )

            }
        }
        return unlockedNow

    }

    override fun getAchievements(): Flow<List<Achievement>> {
        return dao.getAllAchievements().map { list ->
            list.map {
                it.toAchievement()
            }
        }
    }

    override fun getLockedAchievements(): Flow<List<Achievement>> {
        return dao.getLockedAchievements().map { list ->
            list.map { it.toAchievement() }
        }
    }

    override fun getUnlockedAchievements(): Flow<List<Achievement>> {
        return dao.getUnlockedAchievements().map { list ->
            list.map { it.toAchievement() }
        }
    }

}