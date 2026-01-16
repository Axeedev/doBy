package com.example.habitflow.presentation.screens.goals.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.habitflow.domain.entities.Goal
import com.example.habitflow.domain.usecases.goals.AddGoalUseCase
import com.example.habitflow.presentation.screens.goals.all.CreateGoalCommand
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateGoalViewModel @Inject constructor(
    private val addGoalUseCase: AddGoalUseCase
): ViewModel() {

    private val _state = MutableStateFlow(CreateGoalScreenState())
    val state
        get() = _state.asStateFlow()


    fun processCommand(command: CreateGoalCommand){
        when(command) {
            is CreateGoalCommand.ChangeGoalCategory -> {
                _state.update { previous ->
                    previous.copy(goalCategory = command.category)
                }
            }
            is CreateGoalCommand.ChooseEndDate -> {
                _state.update { previous ->
                    previous.copy(endDate = command.endDate)
                }
            }
            CreateGoalCommand.ClickAddMilestone -> {

            }
            CreateGoalCommand.ClickCreateGoal -> {
                viewModelScope.launch {
                    val currentState = _state.value
                    val goal = Goal(
                        id = 0,
                        category = currentState.goalCategory,
                        title = currentState.title,
                        description = currentState.description,
                        goalStartDate = "",
                        goalEndDate = currentState.endDate,
                        milestones = currentState.milestones
                    )
                    addGoalUseCase(goal)
                }

            }
            is CreateGoalCommand.InputDescription -> {
                _state.update { previous ->
                    previous.copy(description = command.description)
                }
            }
            is CreateGoalCommand.InputMilestoneTitle -> {

            }
            is CreateGoalCommand.InputTitle -> {
                _state.update { previous ->
                    previous.copy(title = command.title)
                }
            }
        }
    }
}