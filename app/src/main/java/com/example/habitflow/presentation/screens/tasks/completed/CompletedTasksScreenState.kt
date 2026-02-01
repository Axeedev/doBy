package com.example.habitflow.presentation.screens.tasks.completed

import com.example.habitflow.domain.entities.CompletedTask

data class CompletedTasksScreenState(
    val completedTasks: List<CompletedTask> = listOf()
)
