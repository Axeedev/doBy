package com.example.habitflow.data.repository

import com.example.habitflow.data.local.goals.GoalsDao
import com.example.habitflow.data.mappers.toGoal
import com.example.habitflow.data.mappers.toGoalEntity
import com.example.habitflow.data.mappers.toMilestoneEntity
import com.example.habitflow.domain.entities.Goal
import com.example.habitflow.domain.entities.Milestone
import com.example.habitflow.domain.repository.GoalRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GoalRepositoryImpl @Inject constructor(
    private val goalsDao: GoalsDao
) : GoalRepository {
    override suspend fun addGoal(goal: Goal) {
        goalsDao.addGoal(goal.toGoalEntity())
    }

    override suspend fun editGoal(goal: Goal) {
        goalsDao.addGoal(goal.toGoalEntity())
    }

    override suspend fun saveGoalAsDraft(goal: Goal) {
        TODO()
    }

    override suspend fun removeGoal(goalId: Int) {
        goalsDao.deleteGoal(goalId)
    }

    override suspend fun changeActiveGoalState(goalId: Int) {
        TODO("Not yet implemented")
    }

    override suspend fun addMilestones(milestones: List<Milestone>, goalId: Int) {
        goalsDao.addMilestones(milestones.map { it.toMilestoneEntity(goalId) })
    }

    override suspend fun deleteMilestone(milestoneId: Int) {
        goalsDao.deleteMilestoneById(milestoneId)
    }

    override suspend fun getGoalById(goalId: Int) : Goal{
        return goalsDao.getGoalById(goalId).toGoal()
    }

    override fun getGoals(): Flow<List<Goal>> {
       return goalsDao.gelGoals().map { list ->
           list.map { it.toGoal() }
       }
    }
}