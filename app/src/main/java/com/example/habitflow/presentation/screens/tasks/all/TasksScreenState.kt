package com.example.habitflow.presentation.screens.tasks.all

import com.example.habitflow.domain.entities.GoalCategory
import com.example.habitflow.domain.entities.Priority
import com.example.habitflow.domain.entities.Task
import com.example.habitflow.domain.entities.TaskCategory
import com.example.habitflow.presentation.screens.tasks.creation.TimeEntity

data class TasksScreenState(
    val todayTasks: List<Task> = listOf(),
    val title: String = "",
    val date: String = "",
    val remindAtMinutesOfDay: TimeEntity? = null,
    val description: String = "",
    val priority: Priority = Priority.LOW,
    val goalCategory: GoalCategory = GoalCategory.EDUCATION
){
    val isButtonEnabled
        get() = title.isNotEmpty() && date.isNotEmpty()
}
