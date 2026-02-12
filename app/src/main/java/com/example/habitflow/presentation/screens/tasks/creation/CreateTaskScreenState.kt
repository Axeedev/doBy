package com.example.habitflow.presentation.screens.tasks.creation

import com.example.habitflow.domain.entities.tasks.Priority
import com.example.habitflow.domain.entities.tasks.TaskCategory

data class CreateTaskScreenState(
    val title: String = "",
    val date: String = "",
    val remindAtMinutesOfDay: TimeEntity? = null,
    val description: String = "",
    val priority: Priority = Priority.LOW,
    val taskCategory: TaskCategory = TaskCategory.CODING
){
    val isButtonEnabled
        get() = title.isNotEmpty() && date.isNotEmpty()
}
