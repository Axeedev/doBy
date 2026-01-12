package com.example.habitflow.presentation.screens.tasks.creation

import com.example.habitflow.presentation.screens.tasks.Category

data class CreateTaskScreenState(
    val title: String = "",
    val date: String = "",
    val startTime: String = "",
    val endTime: String = "",
    val description: String = "",
    val category: Category = Category.CODING
)
