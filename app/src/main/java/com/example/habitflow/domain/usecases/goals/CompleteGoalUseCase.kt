package com.example.habitflow.domain.usecases.goals

import com.example.habitflow.domain.repository.GoalRepository
import javax.inject.Inject

class CompleteGoalUseCase @Inject constructor(
    private val repository: GoalRepository
) {
    suspend operator fun invoke(goalId: Int) = repository.completeGoal(goalId)
}