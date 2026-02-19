package com.example.habitflow.presentation.screens.analytics

import java.time.LocalDate

data class AnalyticsScreenState(
    val dailyStats: List<Pair<LocalDate, Int>> = listOf(),
    val completedTasksOverall: Int = 0,
    val completedTasksThisWeek: Int = 0,
    val percentageDiffPastWeek: Int = 0
)