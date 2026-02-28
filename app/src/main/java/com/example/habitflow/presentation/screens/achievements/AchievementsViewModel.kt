@file:OptIn(ExperimentalCoroutinesApi::class)

package com.example.habitflow.presentation.screens.achievements

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.habitflow.domain.entities.achievements.Achievement
import com.example.habitflow.domain.usecases.achievements.GetAchievementsUseCase
import com.example.habitflow.domain.usecases.achievements.GetCurrentStreakUseCase
import com.example.habitflow.domain.usecases.achievements.GetLockedAchievementsUseCase
import com.example.habitflow.domain.usecases.achievements.GetUnlockedAchievementsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AchievementsViewModel @Inject constructor(
    private val getAchievementsUseCase: GetAchievementsUseCase,
    private val getLockedAchievementsUseCase: GetLockedAchievementsUseCase,
    private val getUnlockedAchievementsUseCase: GetUnlockedAchievementsUseCase,
    private val getCurrentStreakUseCase: GetCurrentStreakUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(AchievementsScreenState())
    val state
        get() = _state.asStateFlow()
    private val filterChipTypes = MutableStateFlow(FilterChipType.IN_PROGRESS)


    init {
        viewModelScope.launch {
            init()
        }
    }

    private suspend fun init(){
        combine(
            flow = subscribeFilterChips(),
            flow2 = getCurrentStreakUseCase()
        ){ achievements, streak ->
            achievements to streak
        }.collect { (achievements, streak) ->
            _state.update { it.copy(achievements = achievements, currentStreak = streak) }

        }
    }

    private fun subscribeFilterChips(): Flow<List<Achievement>> {
        return filterChipTypes.flatMapLatest { filterChipType ->
            when (filterChipType) {
                FilterChipType.ALL -> {
                    getAchievementsUseCase()
                }

                FilterChipType.IN_PROGRESS -> {
                    getLockedAchievementsUseCase()
                }

                FilterChipType.COMPLETED -> {
                    getUnlockedAchievementsUseCase()
                }
            }
        }
    }

    fun processCommand(command: AchievementsCommand) {
        when (command) {
            is AchievementsCommand.ChangeFilterType -> {
                filterChipTypes.value = command.filterChipType
                _state.update { previous ->
                    previous.copy(selectedType = command.filterChipType)
                }
            }
        }
    }
}