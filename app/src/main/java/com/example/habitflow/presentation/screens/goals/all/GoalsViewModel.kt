package com.example.habitflow.presentation.screens.goals.all

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.habitflow.domain.usecases.goals.GetGoalsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GoalsViewModel @Inject constructor(
    private val getGoalsUseCase: GetGoalsUseCase,
) : ViewModel() {
    private val _state = MutableStateFlow(GoalsScreenState())
    val state
        get() = _state.asStateFlow()

    init {
        viewModelScope.launch {
            init()
        }
    }

    suspend fun init() {
        getGoalsUseCase()
            .collect { goals ->
                _state.update {
                    it.copy(goals = goals)
                }
            }
    }
}