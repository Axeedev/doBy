package com.example.habitflow.presentation.screens.tasks.all

import com.example.habitflow.domain.entities.goals.GoalCategory
import com.example.habitflow.domain.entities.tasks.Priority
import com.example.habitflow.domain.entities.tasks.Task
import com.example.habitflow.presentation.screens.tasks.TaskDeadlineSection
import com.example.habitflow.presentation.screens.tasks.TimeEntity

data class TasksScreenState(
    val taskId: Int? = null,
    val tasksMapSections: Map<TaskDeadlineSection, List<Task>> = mapOf(),
    val title: String = "",
    val date: Long? = null,
    val remindAtMinutesOfDay: TimeEntity? = null,
    val description: String = "",
    val priority: Priority = Priority.LOW,
    val goalCategory: GoalCategory = GoalCategory.defaultCategories.first(),
    val buttonText: String = "Create task",
    val isRefreshLoading: Boolean = false,
    val categories: List<GoalCategory> = GoalCategory.defaultCategories,
    val newCategoryName: String = ""
) {
    val isButtonEnabled
        get() = title.trim().isNotEmpty()

    val isAddCategoryButtonEnabled
        get() = newCategoryName.trim().isNotEmpty()
}
