package com.example.habitflow.domain.repository

import com.example.habitflow.domain.entities.Goal
import com.example.habitflow.domain.entities.Milestone
import kotlinx.coroutines.flow.Flow

interface GoalRepository {

    suspend fun addGoal(goal: Goal)

    suspend fun editGoal(goal: Goal)

    suspend fun saveGoalAsDraft(goal: Goal)

    suspend fun removeGoal(goalId: Int)

    suspend fun changeActiveGoalState(goalId: Int)

    suspend fun deleteMilestone(milestoneId: Int)

    suspend fun getGoalById(goalId: Int) : Goal


    fun getGoals() : Flow<List<Goal>>

}