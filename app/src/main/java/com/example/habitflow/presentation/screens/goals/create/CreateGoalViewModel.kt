package com.example.habitflow.presentation.screens.goals.create

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.habitflow.domain.entities.Goal
import com.example.habitflow.domain.entities.Milestone
import com.example.habitflow.domain.usecases.goals.AddGoalUseCase
import com.example.habitflow.domain.usecases.goals.UpdateGoalUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
open class CreateGoalViewModel @Inject constructor(
    private val addGoalUseCase: AddGoalUseCase,
    private val updateGoalUseCase: UpdateGoalUseCase
): ViewModel() {

    protected val _state = MutableStateFlow(CreateGoalScreenState())
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
                _state.update {previous ->
                    val newList = previous.milestones.toMutableList().apply {
                        add(Milestone(0, "", false))
                    }
                    previous.copy(milestones = newList)
                }
            }
            CreateGoalCommand.ClickCreateGoal -> {
                viewModelScope.launch {
                    val currentState = _state.value
                    val goal = Goal(
                        id = 0,
                        category = currentState.goalCategory,
                        title = currentState.title,
                        description = currentState.description,
                        goalStartDate = currentState.startDate,
                        goalEndDate = currentState.endDate,
                        milestones = currentState.milestones,
                        coverUri = currentState.coverUri?.toString()
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
                _state.update {previous ->
                    val milestoneToEdit = previous.milestones[command.index].copy(
                        title = command.title
                    )
                    val newList = previous.milestones.mapIndexed { index, milestone ->
                        if (index == command.index){
                            milestoneToEdit
                        }else{
                            milestone
                        }
                    }
                    previous.copy(milestones = newList)
                }
            }
            is CreateGoalCommand.InputTitle -> {
                _state.update { previous ->
                    previous.copy(title = command.title)
                }
            }

            is CreateGoalCommand.RemoveMilestoneAt -> {
                _state.update {previous ->
                    val newList = previous.milestones.toMutableList()
                    newList.removeAt(command.index)
                    previous.copy(milestones = newList)
                }
            }

            is CreateGoalCommand.AddPhoto -> {
                _state.update { it.copy(coverUri = command.uri) }
            }
            CreateGoalCommand.ClickDeletePhoto -> {
                _state.update { it.copy(coverUri = null) }
            }

            is CreateGoalCommand.ChooseStartDate -> {
                _state.update { previous ->
                    previous.copy(startDate = command.startDate)
                }
            }

            is CreateGoalCommand.ChangeMilestoneCompletedStatusAt -> {
                _state.update {previous ->
                    val newList = previous.milestones.mapIndexed {index, milestone ->
                        if (index == command.index){
                            milestone.copy(isCompleted = !milestone.isCompleted)
                        }else milestone
                    }
                    previous.copy(milestones = newList)
                }
            }

            CreateGoalCommand.ClickUpdateGoal -> {
                viewModelScope.launch {
                    val currentState = _state.value
                    val goal = Goal(
                        id = currentState.goalId ?: 0,
                        category = currentState.goalCategory,
                        title = currentState.title,
                        description = currentState.description,
                        goalStartDate = currentState.startDate,
                        goalEndDate = currentState.endDate,
                        milestones = currentState.milestones,
                        coverUri = currentState.coverUri?.toString()
                    )
                    Log.d("CreateGoalViewModel", goal.toString())
                    updateGoalUseCase(goal)
                }
            }
        }
    }
}
