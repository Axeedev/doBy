package com.example.habitflow.presentation.screens.tasks.all

import com.example.habitflow.domain.entities.goals.GoalCategory
import com.example.habitflow.domain.entities.tasks.Priority
import com.example.habitflow.domain.entities.tasks.Task
import com.example.habitflow.presentation.screens.tasks.TaskDeadlineSection
import com.example.habitflow.presentation.screens.tasks.creation.TimeEntity

data class TasksScreenState(
    val taskId: Int? = null,
    val tasksMapSections: Map<TaskDeadlineSection, List<Task>> = mapOf(),
    val title: String = "",
    val date: Long? = null,
    val remindAtMinutesOfDay: TimeEntity? = null,
    val description: String = "",
    val priority: Priority = Priority.LOW,
    val goalCategory: GoalCategory = GoalCategory.EDUCATION,
    val buttonText: String = "Create task"
) {
    val isButtonEnabled
        get() = title.trim().isNotEmpty()
}
