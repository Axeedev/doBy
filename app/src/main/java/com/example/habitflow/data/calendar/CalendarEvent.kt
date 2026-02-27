package com.example.habitflow.data.calendar

import com.example.habitflow.domain.entities.Category
import com.example.habitflow.domain.entities.tasks.Priority
import com.example.habitflow.domain.entities.tasks.Task

data class CalendarEvent(
    val id: Int,
    val title: String,
    val description: String,
    val startMillis: Long,
    val endMillis: Long
)

fun CalendarEvent.toTask() = Task(
    id = id,
    title = title,
    deadlineMillis = startMillis,
    note = description,
    category = Category(Category.CALENDAR_NAME),
    priority = Priority.LOW
)