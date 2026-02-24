package com.example.habitflow.domain.repository

import com.example.habitflow.domain.entities.goals.Goal
import kotlinx.coroutines.flow.Flow

interface GoalRepository {

    suspend fun addGoal(goal: Goal)

    suspend fun editGoal(goal: Goal)

    suspend fun removeGoal(goalId: Int)

    suspend fun completeGoal(goalId: Int)

    suspend fun deleteMilestone(milestoneId: Int)

    suspend fun getGoalById(goalId: Int) : Goal

    fun getGoals() : Flow<List<Goal>>
}