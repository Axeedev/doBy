package com.example.habitflow.presentation.screens.goals.all

import com.example.habitflow.domain.entities.GoalCategory

sealed interface CreateGoalCommand {

    data class InputTitle(val title: String) : CreateGoalCommand

    data class ChangeGoalCategory(val category: GoalCategory) : CreateGoalCommand

    data class ChooseEndDate(val endDate: String) : CreateGoalCommand

    data class InputDescription(val description: String) : CreateGoalCommand

    data object ClickAddMilestone : CreateGoalCommand

    data class InputMilestoneTitle(val title: String) : CreateGoalCommand

    data object ClickCreateGoal : CreateGoalCommand
}