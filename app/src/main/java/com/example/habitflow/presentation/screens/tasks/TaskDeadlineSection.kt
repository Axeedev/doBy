package com.example.habitflow.presentation.screens.tasks

import com.example.habitflow.R

enum class TaskDeadlineSection(val titleId : Int) {
    TODAY(R.string.tasks_today),
    TOMORROW(R.string.tasks_tomorrow),
    NEXT_WEEK(R.string.tasks_on_a_week),
    LATER(R.string.tasks_later)
}