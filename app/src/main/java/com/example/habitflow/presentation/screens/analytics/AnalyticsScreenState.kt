package com.example.habitflow.presentation.screens.analytics

import java.time.LocalDate

data class AnalyticsScreenState(
    val dailyStats: List<Pair<LocalDate, Int>> = listOf(),
    val completedTasks: Int = 0
){



}
