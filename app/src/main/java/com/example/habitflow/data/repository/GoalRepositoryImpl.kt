package com.example.habitflow.data.repository

import com.example.habitflow.domain.entities.Goal
import com.example.habitflow.domain.repository.GoalRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GoalRepositoryImpl @Inject constructor() : GoalRepository {
    override suspend fun addGoal(goal: Goal) {
        TODO("Not yet implemented")
    }

    override suspend fun editGoal(goal: Goal) {
        TODO("Not yet implemented")
    }

    override suspend fun saveGoalAsDraft(goal: Goal) {
        TODO("Not yet implemented")
    }

    override suspend fun removeGoal(goalId: String) {
        TODO("Not yet implemented")
    }

    override suspend fun changeActiveGoalState(goalId: String) {
        TODO("Not yet implemented")
    }

    override fun getGoals(query: String): Flow<List<Goal>> {
        TODO("Not yet implemented")
    }
}