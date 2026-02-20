package com.example.habitflow.presentation.screens.analytics

import java.time.LocalDate
import java.time.YearMonth

data class AnalyticsScreenState(
    val dailyStats: List<Pair<LocalDate, Int>> = listOf(),
    val heatMapStats: List<Pair<LocalDate, Int>> = listOf(),
    val selectedChartType: ChartType = ChartType.BAR_CHART,
    val completedTasksOverall: Int = 0,
    val completedTasksThisWeek: Int = 0,
    val percentageDiffPastWeek: Int = 0,
){
    val statsMap = dailyStats.toMap()

    private val currentMonth = YearMonth.now()

    val months = (0 until 12)
        .map { currentMonth.minusMonths((11 - it).toLong()) }


    val maxValue = (dailyStats.maxOfOrNull { it.second } ?: 0).coerceAtLeast(1)
}
