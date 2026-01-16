package com.example.habitflow.domain.usecases.goals

import com.example.habitflow.domain.entities.Milestone
import com.example.habitflow.domain.repository.GoalRepository
import javax.inject.Inject

class AddMilestonesUseCase @Inject constructor(
    private val repository: GoalRepository
) {
    suspend operator fun invoke(
        milestones: List<Milestone>, goalId: Int
    ) = repository.addMilestones(milestones, goalId)
}