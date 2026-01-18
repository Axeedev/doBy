package com.example.habitflow.presentation.screens.tasks.creation

import com.example.habitflow.domain.entities.Priority
import com.example.habitflow.domain.entities.TaskCategory

data class CreateTaskScreenState(
    val title: String = "",
    val date: String = "",
    val startTime: Long = System.currentTimeMillis(),
    val endTime: Long = System.currentTimeMillis(),
    val description: String = "",
    val priority: Priority = Priority.REGULAR,
    val taskCategory: TaskCategory = TaskCategory.CODING
){
    val isButtonEnabled
        get() = title.isNotEmpty() && date.isNotEmpty()
}
