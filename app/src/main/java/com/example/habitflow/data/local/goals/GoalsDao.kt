package com.example.habitflow.data.local.goals

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.habitflow.domain.entities.Milestone
import kotlinx.coroutines.flow.Flow

@Dao
interface GoalsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addGoal(goalEntity: GoalEntity)

    @Query("""
        SELECT * FROM goals
    """)
    @Transaction
    fun gelGoals() : Flow<List<GoalWithMilestoneEntity>>

    @Query(
        """
            DELETE FROM Goals
            WHERE id == :goalId
        """
    )
    suspend fun deleteGoal(goalId: Int)

    @Query("""
        SELECT * FROM goals
        WHERE id == :goalId
    """)
    @Transaction
    suspend fun getGoalById(goalId: Int) : GoalWithMilestoneEntity

    @Insert
    suspend fun addMilestones(milestones: List<MilestoneEntity>)

    @Query("""
        DELETE FROM milestones
        WHERE id == :milestoneId
    """)
    suspend fun deleteMilestoneById(milestoneId: Int)
}