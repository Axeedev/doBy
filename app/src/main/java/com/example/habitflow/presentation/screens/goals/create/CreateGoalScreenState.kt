package com.example.habitflow.presentation.screens.goals.create

import com.example.habitflow.domain.entities.Milestone
import com.example.habitflow.presentation.screens.goals.GoalCategory


data class CreateGoalScreenState(
    val title: String = "",
    val goalCategory: GoalCategory = GoalCategory.SPORTS,
    val description: String = "",
    val milestones: List<Milestone> = listOf()
){
    val isSaveButtonEnabled: Boolean
        get() = title.isNotEmpty()
}
