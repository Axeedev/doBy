package com.example.habitflow.presentation.screens.tasks.creation

import com.example.habitflow.presentation.screens.tasks.Category
import java.util.Calendar

data class CreateTaskScreenState(
    val title: String = "",
    val date: String = "",
    val startTime: String = "${Calendar.HOUR_OF_DAY}:${Calendar.MINUTE}",
    val endTime: String = "${Calendar.HOUR_OF_DAY}:${Calendar.MINUTE}",
    val description: String = "",
    val category: Category = Category.CODING
)
