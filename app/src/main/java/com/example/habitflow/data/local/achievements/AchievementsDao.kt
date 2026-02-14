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

    @Query("""
        SELECT * FROM achievements
        ORDER BY progress DESC
    """)
    fun getAllAchievements() : Flow<List<AchievementEntity>>

    @Query("""
        SELECT * FROM achievements
        WHERE isUnlocked = 1
        ORDER BY dateOfUnlock
    """)
    fun getUnlockedAchievements() : Flow<List<AchievementEntity>>

    @Query("""
        SELECT * FROM achievements
        WHERE isUnlocked = 0
    """)
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

}