package com.example.habitflow.presentation.screens.tasks.all


sealed interface TasksCommand {

    data class InputTaskTitle(val taskTitle: String) : TasksCommand

    data class InputTaskNote(val taskNote: String) : TasksCommand

    data object ChangePriority : TasksCommand

    data object AddTask : TasksCommand

    data object DeleteTask : TasksCommand

}