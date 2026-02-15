@file:OptIn(ExperimentalCoroutinesApi::class)

package com.example.habitflow.presentation.screens.achievements

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.habitflow.domain.usecases.achievements.GetAchievementsUseCase
import com.example.habitflow.domain.usecases.achievements.GetCurrentStreakUseCase
import com.example.habitflow.domain.usecases.achievements.GetLockedAchievementsUseCase
import com.example.habitflow.domain.usecases.achievements.GetUnlockedAchievementsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
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
            launch {
                getCurrentStreakUseCase()
                    .collect { streak ->
                        _state.update {
                            it.copy(currentStreak = streak)
                        }
                    }

            }
            filterChipTypes.flatMapLatest { filterChipType ->
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
            }.collect { achievements ->
                _state.update { it.copy(achievements = achievements) }
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