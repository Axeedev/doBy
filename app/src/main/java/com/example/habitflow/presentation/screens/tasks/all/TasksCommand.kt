package com.example.habitflow.presentation.screens.tasks.all

import com.example.habitflow.domain.entities.goals.GoalCategory
import com.example.habitflow.domain.entities.tasks.Priority
import com.example.habitflow.domain.entities.tasks.Task
import com.example.habitflow.presentation.screens.tasks.TaskDeadlineSection
import com.example.habitflow.presentation.screens.tasks.TimeEntity


sealed interface TasksCommand {

    data class InputTitle(val title: String) : TasksCommand

    data class InputDate(val date: Long) : TasksCommand

    data class InputDeadline(val deadline: TimeEntity) : TasksCommand

    data class InputDescription(val description: String) : TasksCommand

    data class ChangeCategory(val taskCategory: GoalCategory) : TasksCommand

    data class ChangePriority(val priority: Priority) : TasksCommand

    data class ClickCompleteTask(
        val task: Task,
        val taskDeadlineSection: TaskDeadlineSection
    ) : TasksCommand

    data class InputCategoryName(val name: String) : TasksCommand

    data class AddNewCategory(val categoryName: String) : TasksCommand

    data class DeleteTask(
        val taskId: Int
    ) : TasksCommand

    data class ClickTask(
        val task: Task
    ) : TasksCommand

    data object ClickButtonAddTask : TasksCommand

    data object AddTask : TasksCommand

    data object CloseBottomSheet : TasksCommand

}