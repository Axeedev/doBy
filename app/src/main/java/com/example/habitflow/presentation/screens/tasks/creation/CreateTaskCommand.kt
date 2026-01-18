package com.example.habitflow.presentation.screens.tasks.creation

import com.example.habitflow.domain.entities.TaskCategory

sealed interface CreateTaskCommand{

    data class InputTitle(val title: String) : CreateTaskCommand

    data class InputDate(val date: Long) : CreateTaskCommand

    data class InputStartTime(val startTime: Long) : CreateTaskCommand

    data class InputEndTime(val endTime: Long) : CreateTaskCommand

    data class InputDescription(val description: String) : CreateTaskCommand

    data class ChangeCategory(val taskCategory: TaskCategory) : CreateTaskCommand

    data object ChangePriority : CreateTaskCommand

    data object AddTask : CreateTaskCommand
}