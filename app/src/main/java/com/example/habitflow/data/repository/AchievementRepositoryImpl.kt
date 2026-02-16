package com.example.habitflow.data.repository

import com.example.habitflow.data.StreakManager
import com.example.habitflow.data.local.achievements.AchievementCodes
import com.example.habitflow.data.local.achievements.AchievementsDao
import com.example.habitflow.data.local.tasks.CompletedTasksDao
import com.example.habitflow.data.mappers.toAchievement
import com.example.habitflow.domain.entities.achievements.Achievement
import com.example.habitflow.domain.entities.achievements.AchievementType
import com.example.habitflow.domain.repository.AchievementRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import javax.inject.Inject

class AchievementRepositoryImpl @Inject constructor(
    private val achievementsDao: AchievementsDao,
    private val streakManager: StreakManager,
    private val tasksDao: CompletedTasksDao
) : AchievementRepository {

    override suspend fun onTaskCompleted(): Boolean {

        val lockedAchievementsTasksCompleted =
            achievementsDao.getLockedAchievementsByType(AchievementType.TASKS_COMPLETED)
        val unlockedNow = mutableListOf<Boolean>()
        unlockedNow.add(updateDayStreak())

        streakManager.updateStreak()

        lockedAchievementsTasksCompleted.forEach { achievementEntity ->

            val newProgress = achievementEntity.currentProgress + 1
            val isAchievementUnlocked = newProgress == achievementEntity.targetGoal

            achievementsDao.updateAchievement(
                achievementEntity.copy(
                    currentProgress = newProgress,
                    isUnlocked = isAchievementUnlocked,
                    dateOfUnlock = if (isAchievementUnlocked) System.currentTimeMillis() else null,
                )
            )
            if (isAchievementUnlocked) unlockedNow.add(true)
        }

        unlockedNow.add(checkForSpecialAchievements())
        val completedTask = tasksDao.getLastCompletedTask()
        completedTask.deadlineMillis

        unlockedNow.add(
            checkTimeAchievement(
                completedTask.completedAt,
                code = AchievementCodes.SPEEDSTER
            ) {
                it <= 1
            })

        unlockedNow.add(
            checkTimeAchievement(
                completedTask.deadlineMillis,
                code = AchievementCodes.CLUTCH
            ) {
                it <= 5
            })


        return unlockedNow.isNotEmpty()
    }


    override fun getCurrentStreak(): Flow<Int> {
        return streakManager.getCurrentStreak()
    }


    private suspend inline fun checkTimeAchievement(
        predicate: Long?,
        code: String,
        isSuits: (Long) -> Boolean
    ): Boolean {

        val achievement = achievementsDao.getAchievementByCode(code) ?: return false

        predicate ?: return false

        if (achievement.isUnlocked) return false

        val now = LocalDateTime.now()

        val completionTime = LocalDateTime.ofInstant(
            Instant.ofEpochMilli(predicate),
            ZoneId.systemDefault()
        )

        val diffInMinutes = ChronoUnit.MINUTES.between(
            completionTime,
            now
        )

        if (isSuits(diffInMinutes)) {
            val newProgress = achievement.currentProgress + 1
            val isUnlocked = newProgress >= achievement.targetGoal

            val updatedAchievement = achievement.copy(
                currentProgress = newProgress,
                isUnlocked = isUnlocked
            )
            achievementsDao.updateAchievement(updatedAchievement)

            return true
        }
        return false
    }

    private suspend fun checkForSpecialAchievements(): Boolean {

        val completedDateTime = LocalDateTime.ofInstant(
            Instant.ofEpochMilli(System.currentTimeMillis()),
            ZoneId.systemDefault()
        )

        val time = completedDateTime.toLocalTime()
        val date = completedDateTime.toLocalDate()
        val dayOfWeek = completedDateTime.dayOfWeek

        val isEarlyBirdUnlocked = if (time.hour in 5..8) unlockByCode(
            AchievementCodes.EARLY_BIRD
        ) else false

        val isNightOwlUnlocked = if (time.hour in 0..4) unlockByCode(
            code = AchievementCodes.NIGHT_OWL
        ) else false

        val startOfDay = date.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        val endOfDay =
            date.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()

        val todayCompletedCount =
            achievementsDao.getCompletedTasksCountForRange(startOfDay, endOfDay)

        val isMondayWarriorUnlocked =
            dayOfWeek == DayOfWeek.MONDAY && updateDailyProgressForSpecialTasks(
                code = AchievementCodes.MONDAY_WARRIOR,
                dailyCount = todayCompletedCount
            )

        val isMarathonUnlocked = updateDailyProgressForSpecialTasks(
            code = AchievementCodes.MARATHON,
            dailyCount = todayCompletedCount
        )

        return isEarlyBirdUnlocked || isNightOwlUnlocked || isMarathonUnlocked || isMondayWarriorUnlocked
    }

    private suspend fun updateDailyProgressForSpecialTasks(code: String, dailyCount: Int): Boolean {

        val achievement = achievementsDao.getAchievementByCode(code) ?: return false

        if (achievement.isUnlocked) return false

        val isUnlocked = dailyCount >= achievement.targetGoal

        val updatedAchievement = achievement.copy(
            isUnlocked = isUnlocked,
            currentProgress = dailyCount
        )

        achievementsDao.updateAchievement(updatedAchievement)

        return isUnlocked
    }

    private suspend fun unlockByCode(code: String): Boolean {
        val achievement = achievementsDao.getAchievementByCode(code) ?: return false
        if (achievement.isUnlocked) return false

        val unlockedAchievement = achievement.copy(isUnlocked = true, currentProgress = 1)
        achievementsDao.updateAchievement(unlockedAchievement)

        return true
    }

    override suspend fun updateDayStreak(): Boolean {

        val currentDate = LocalDate.now()
        val unlockedNow = mutableListOf<Achievement>()

        val streakAchievements = achievementsDao.getLockedAchievementsByType(AchievementType.STREAK)
        streakAchievements.forEach { achievementEntity ->
            val lastDate = achievementEntity.lastDateOfCompletion?.let {
                LocalDate.parse(it)
            }

            val newProgress = when {
                lastDate == null -> 1

                lastDate == currentDate -> achievementEntity.currentProgress

                lastDate.plusDays(1) == currentDate -> achievementEntity.currentProgress + 1

                else -> 1
            }

            val isUnlocked = newProgress >= achievementEntity.targetGoal

            achievementsDao.updateAchievement(
                achievementEntity.copy(
                    currentProgress = newProgress,
                    isUnlocked = isUnlocked,
                    dateOfUnlock = if (isUnlocked) System.currentTimeMillis() else null,
                    lastDateOfCompletion = currentDate.toString(),
                )
            )
            if (isUnlocked) unlockedNow.add(achievementEntity.toAchievement())
        }

        return unlockedNow.isNotEmpty()
    }

    override fun getAchievements(): Flow<List<Achievement>> {
        return achievementsDao.getAllAchievements().map { list ->
            list.map {
                it.toAchievement()
            }
        }
    }

    override fun getLockedAchievements(): Flow<List<Achievement>> {
        return achievementsDao.getLockedAchievements().map { list ->
            list.map { it.toAchievement() }
        }
    }

    override fun getUnlockedAchievements(): Flow<List<Achievement>> {
        return achievementsDao.getUnlockedAchievements().map { list ->
            list.map { it.toAchievement() }
        }
    }

}