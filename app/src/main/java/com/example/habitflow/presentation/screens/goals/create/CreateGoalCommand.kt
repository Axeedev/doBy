package com.example.habitflow.presentation.screens.goals.create

import android.net.Uri
import com.example.habitflow.domain.entities.Category

sealed interface CreateGoalCommand {

    data class InputTitle(val title: String) : CreateGoalCommand

    data class ChangeGoalCategory(val category: Category) : CreateGoalCommand

    data class ChooseEndDate(val endDate: Long) : CreateGoalCommand

    data class ChooseStartDate(val startDate: Long) : CreateGoalCommand

    data class InputDescription(val description: String) : CreateGoalCommand

    data object ClickAddMilestone : CreateGoalCommand

    data class InputMilestoneTitle(val title: String, val index: Int) : CreateGoalCommand

    data class ChangeMilestoneCompletedStatusAt(val index: Int) : CreateGoalCommand

    data class RemoveMilestoneAt(val index: Int) : CreateGoalCommand

    data object ClickCreateGoal : CreateGoalCommand

    data object ClickUpdateGoal : CreateGoalCommand

    data class ClickCompleteGoal(val goalId: Int): CreateGoalCommand

    data class AddPhoto(val uri: Uri) : CreateGoalCommand

    data object ClickDeletePhoto : CreateGoalCommand

    data class InputCategoryName(val name: String) : CreateGoalCommand

    data class AddNewCategory(val categoryName: String) : CreateGoalCommand

    data object OpenCompleteGoalDialog : CreateGoalCommand

    data object CloseCompleteGoalDialog : CreateGoalCommand

    data object OpenAddCategoryDialog : CreateGoalCommand

    data object CloseAddCategoryDialog : CreateGoalCommand

}