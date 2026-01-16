package com.example.habitflow.presentation.screens.tasks.creation

import com.example.habitflow.presentation.Priority
import com.example.habitflow.presentation.screens.tasks.TaskCategory
import com.example.habitflow.presentation.utils.DateFormatter

data class CreateTaskScreenState(
    val title: String = "",
    val date: String = "",
    val startTime: String = DateFormatter.getLocalTime(),
    val endTime: String = DateFormatter.getLocalTime(),
    val description: String = "",
    val priority: Priority = Priority.REGULAR,
    val taskCategory: TaskCategory = TaskCategory.CODING
){
    val isButtonEnabled
        get() = title.isNotEmpty() && date.isNotEmpty()
}
