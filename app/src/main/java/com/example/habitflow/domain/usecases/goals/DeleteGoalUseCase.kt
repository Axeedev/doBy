package com.example.habitflow.domain.usecases.goals

import com.example.habitflow.domain.repository.GoalRepository
import javax.inject.Inject

class DeleteGoalUseCase @Inject constructor(
    private val repository: GoalRepository
) {
    suspend operator fun invoke(goalId: Int) = repository.removeGoal(goalId)
}