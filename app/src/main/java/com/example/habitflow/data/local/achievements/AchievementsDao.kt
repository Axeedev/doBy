package com.example.habitflow.data.local.achievements

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.habitflow.domain.entities.achievements.AchievementType
import kotlinx.coroutines.flow.Flow

@Dao
interface AchievementsDao {

    @Query(
        """
        SELECT * FROM achievements
        ORDER BY currentProgress DESC
    """
    )
    fun getAllAchievements() : Flow<List<AchievementEntity>>

    @Query("""
        SELECT * FROM achievements
        WHERE isUnlocked = 1
        ORDER BY dateOfUnlock
    """)
    fun getUnlockedAchievements() : Flow<List<AchievementEntity>>

    @Query(
        """
        SELECT * FROM achievements
        WHERE isUnlocked = 0
        ORDER BY currentProgress DESC
    """
    )
    fun getLockedAchievements(): Flow<List<AchievementEntity>>

    @Query("""
        SELECT * FROM achievements
        WHERE achievementType = :type
        AND isUnlocked = 0
    """)
    suspend fun getLockedAchievementsByType(type: AchievementType) : List<AchievementEntity>

    @Update
    suspend fun updateAchievement(achievementEntity: AchievementEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAchievements(achievement: List<AchievementEntity>)

    @Query("""
        SELECT * FROM achievements
        WHERE achievementCode == :code
        LIMIT 1
    """)
    suspend fun getAchievementByCode(code: String) : AchievementEntity?

    @Query("SELECT COUNT(*) FROM completedTasks WHERE isCompleted = 1 AND completedAt BETWEEN :startOfDay AND :endOfDay")
    suspend fun getCompletedTasksCountForRange(startOfDay: Long, endOfDay: Long): Int

}