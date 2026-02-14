package com.example.habitflow.presentation.screens.analytics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.habitflow.domain.usecases.analytics.GetDailyStatsUseCase
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
    private val getNumberOfCompletedTasksUseCase: GetNumberOfCompletedTasksUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(AnalyticsScreenState())
    val state
        get() = _state.asStateFlow()


    init {

        viewModelScope.launch {
            combine(
                flow = getDailyStatsUseCase(),
                flow2 = getNumberOfCompletedTasksUseCase()
            ) { stats, size ->
                stats to size
            }.collect {(stats , size) ->
                val pairs = stats.toWeeklyPairsFromDayBucket()
                _state.update { it.copy(dailyStats = pairs, completedTasks = size) }

            }
        }


    }




}