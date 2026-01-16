package com.example.habitflow.presentation.screens.tasks.all

import com.example.habitflow.domain.entities.Task
import com.example.habitflow.domain.entities.Priority

data class TasksScreenState(
    val todayTasks: List<Task> = listOf(),
    val tomorrowTasks: List<Task> = listOf(),
    val nextWeekTasks: List<Task> = listOf(),
    val laterTasks: List<Task> = listOf(),
    val currentTask: Task? = null,
    val taskTitle: String = "",
    val taskNote: String = "",
    val taskPriority: Priority = Priority.REGULAR
)