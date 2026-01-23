package com.example.habitflow.presentation.screens.tasks.creation

import com.example.habitflow.domain.entities.Priority
import com.example.habitflow.domain.entities.TaskCategory

sealed interface CreateTaskCommand{

    data class InputTitle(val title: String) : CreateTaskCommand

    data class InputDate(val date: Long) : CreateTaskCommand

    data class InputDeadline(val deadline: TimeEntity) : CreateTaskCommand

    data class InputDescription(val description: String) : CreateTaskCommand

    data class ChangeCategory(val taskCategory: TaskCategory) : CreateTaskCommand

    data class ChangePriority(val priority: Priority) : CreateTaskCommand

    data object AddTask : CreateTaskCommand
}