package com.example.habitflow.domain.usecases.goals

import com.example.habitflow.domain.repository.GoalRepository
import javax.inject.Inject

class GetGoalsUseCase @Inject constructor(
    private val repository: GoalRepository
) {
    operator fun invoke() = repository.getGoals()
}