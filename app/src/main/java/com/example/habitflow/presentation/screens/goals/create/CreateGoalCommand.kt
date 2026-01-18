package com.example.habitflow.presentation.screens.goals.create

import android.net.Uri
import com.example.habitflow.domain.entities.GoalCategory

sealed interface CreateGoalCommand {

    data class InputTitle(val title: String) : CreateGoalCommand

    data class ChangeGoalCategory(val category: GoalCategory) : CreateGoalCommand

    data class ChooseEndDate(val endDate: Long) : CreateGoalCommand

    data class ChooseStartDate(val startDate: Long) : CreateGoalCommand

    data class InputDescription(val description: String) : CreateGoalCommand

    data object ClickAddMilestone : CreateGoalCommand

    data class InputMilestoneTitle(val title: String, val index: Int) : CreateGoalCommand

    data class ChangeMilestoneCompletedStatusAt(val index: Int) : CreateGoalCommand

    data class RemoveMilestoneAt(val index: Int) : CreateGoalCommand

    data object ClickCreateGoal : CreateGoalCommand

    data object ClickUpdateGoal : CreateGoalCommand

    data class AddPhoto(val uri: Uri) : CreateGoalCommand

    data object ClickDeletePhoto : CreateGoalCommand
}