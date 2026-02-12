package com.example.habitflow.domain.usecases.goals

import com.example.habitflow.domain.entities.goals.Goal
import com.example.habitflow.domain.repository.GoalRepository
import javax.inject.Inject

class UpdateGoalUseCase @Inject constructor(
    private val repository: GoalRepository
){
    suspend operator fun invoke(goal: Goal) = repository.editGoal(goal)

}