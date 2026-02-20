package com.example.habitflow.presentation.screens.analytics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.habitflow.R
import com.example.habitflow.domain.usecases.analytics.GetCountOfCompletedTasksForWeekUseCase
import com.example.habitflow.domain.usecases.analytics.GetDailyStatsUseCase
import com.example.habitflow.domain.usecases.analytics.GetWeeklyDifferencePercentageUseCase
import com.example.habitflow.domain.usecases.tasks.GetNumberOfCompletedTasksUseCase
import com.example.habitflow.presentation.utils.toWeeklyPairsFromDayBucket
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AnalyticsViewModel @Inject constructor(
    private val getDailyStatsUseCase: GetDailyStatsUseCase,
    private val getNumberOfCompletedTasksUseCase: GetNumberOfCompletedTasksUseCase,
    private val getWeeklyDifferencePercentageUseCase: GetWeeklyDifferencePercentageUseCase,
    private val getCountOfCompletedTasksForWeekUseCase: GetCountOfCompletedTasksForWeekUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(AnalyticsScreenState())
    val state
        get() = _state.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                flow = getDailyStatsUseCase(),
                flow2 = getNumberOfCompletedTasksUseCase(),
                flow3 = getWeeklyDifferencePercentageUseCase(),
                flow4 = getCountOfCompletedTasksForWeekUseCase()
            ) { stats, size, differencePercentage, countOfCompletedTasks ->
                val pairs = stats.toWeeklyPairsFromDayBucket()
                AnalyticsScreenState(
                    dailyStats = pairs,
                    heatMapStats = pairs,
                    completedTasksOverall = size,
                    completedTasksThisWeek = countOfCompletedTasks,
                    percentageDiffPastWeek = differencePercentage
                )
            }.collect { state->
                _state.value = state
            }
        }
    }

    fun processCommand(analyticsCommand: AnalyticsCommand){
        when(analyticsCommand){
            is AnalyticsCommand.SwitchSelectedChartType -> {
                _state.update {
                    it.copy(selectedChartType = analyticsCommand.chartType)
                }
            }
        }
    }
}