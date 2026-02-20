package com.example.habitflow.presentation.screens.analytics

sealed interface AnalyticsCommand {

    data class SwitchSelectedChartType(
        val chartType: ChartType
    ) : AnalyticsCommand

}