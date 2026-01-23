package com.example.habitflow.data.repository

import android.util.Log
import com.example.habitflow.data.local.InternalStorageManager
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
    private val goalsDao: GoalsDao,
    private val internalStorageManager: InternalStorageManager
) : GoalRepository {
    override suspend fun addGoal(goal: Goal) {
        val internalPath  = goal.coverUri?.let {
            internalStorageManager.addToInternal(goal.coverUri)
        }
        val goalId = goalsDao.addGoal(
            goal.toGoalEntity(internalPath)
        )
        goalsDao.addMilestones(
            goal.milestones.map { it.toMilestoneEntity(goalId) }
        )
    }

    override suspend fun editGoal(goal: Goal) {
        Log.d("editGoal", goal.toString())
        val id = goalsDao.addGoal(goal.toGoalEntity(goal.coverUri))
        Log.d("editGoal", id.toString())
        goalsDao.updateMilestones(goal.milestones.map { it.toMilestoneEntity(id)})
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