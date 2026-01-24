package com.example.habitflow.presentation.screens.tasks.all

import com.example.habitflow.domain.entities.GoalCategory
import com.example.habitflow.domain.entities.Priority
import com.example.habitflow.domain.entities.Task
import com.example.habitflow.domain.entities.TaskCategory
import com.example.habitflow.presentation.screens.tasks.creation.TimeEntity

data class TasksScreenState(
    val todayTasks: List<Task> = listOf(),
    val tomorrowTasks: List<Task> = listOf(),
    val onThisWeekTasks: List<Task> = listOf(),
    val someWhenTasks: List<Task> = listOf(),
    val title: String = "",
    val date: Long? = null,
    val remindAtMinutesOfDay: TimeEntity? = null,
    val description: String = "",
    val priority: Priority = Priority.LOW,
    val goalCategory: GoalCategory = GoalCategory.EDUCATION
){
    val isButtonEnabled
        get() = title.isNotEmpty()
}
