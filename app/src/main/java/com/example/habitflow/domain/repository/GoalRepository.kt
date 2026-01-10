package com.example.habitflow.domain.repository

import com.example.habitflow.domain.entities.Goal
import kotlinx.coroutines.flow.Flow

interface GoalRepository {

    suspend fun addGoal(goal: Goal)

    suspend fun editGoal(goal: Goal)

    suspend fun saveGoalAsDraft(goal: Goal)

    suspend fun removeGoal(goalId: String)

    suspend fun changeActiveGoalState(goalId: String)

    fun getGoals(query: String) : Flow<List<Goal>>

}