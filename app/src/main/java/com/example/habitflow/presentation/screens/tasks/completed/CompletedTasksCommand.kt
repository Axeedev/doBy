package com.example.habitflow.presentation.screens.tasks.completed

sealed interface CompletedTasksCommand {

    data class ClickReturnTask(val taskId: Int) : CompletedTasksCommand

}