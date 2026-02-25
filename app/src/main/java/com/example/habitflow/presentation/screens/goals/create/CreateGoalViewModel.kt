package com.example.habitflow.presentation.screens.goals.create

import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.habitflow.domain.entities.goals.Goal
import com.example.habitflow.domain.entities.goals.GoalCategory
import com.example.habitflow.domain.entities.goals.Milestone
import com.example.habitflow.domain.usecases.achievements.OnTaskCompletedUseCase
import com.example.habitflow.domain.usecases.goals.AddGoalUseCase
import com.example.habitflow.domain.usecases.goals.CompleteGoalUseCase
import com.example.habitflow.domain.usecases.goals.GetGoalByIdUseCase
import com.example.habitflow.domain.usecases.goals.UpdateGoalUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = CreateGoalViewModel.ViewModelFactory::class)
class CreateGoalViewModel @AssistedInject constructor(
    private val addGoalUseCase: AddGoalUseCase,
    private val updateGoalUseCase: UpdateGoalUseCase,
    private val completeGoalUseCase: CompleteGoalUseCase,
    private val getGoalByIdUseCase: GetGoalByIdUseCase,
    @Assisted("id") private val taskId : Int? = null
): ViewModel() {

    private val _state = MutableStateFlow(CreateGoalScreenState())
    val state
        get() = _state.asStateFlow()

    init {
        viewModelScope.launch {
            taskId?.let { id ->
                val goal = getGoalByIdUseCase(id)
                _state.value = CreateGoalScreenState(
                    goalId = id,
                    title = goal.title,
                    coverUri = goal.coverUri?.toUri(),
                    goalCategory = goal.category,
                    description = goal.description,
                    startDate = goal.goalStartDate,
                    endDate = goal.goalEndDate,
                    milestones = goal.milestones
                )
            }
        }
    }

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
                    updateGoalUseCase(goal)
                }
            }

            is CreateGoalCommand.ClickCompleteGoal -> {
                viewModelScope.launch {
                    completeGoalUseCase(command.goalId)
                }
            }
            is CreateGoalCommand.AddNewCategory -> {
                _state.update {
                    val newList = it.categories.toMutableList()
                    newList.add(GoalCategory(command.categoryName))
                    it.copy(
                        categories = newList,
                        goalCategory = newList.last()
                    )
                }
            }

            is CreateGoalCommand.InputCategoryName -> {
                _state.update { it.copy(newCategoryName = command.name) }
            }
        }
    }
    @AssistedFactory
    interface ViewModelFactory{
        fun create(
            @Assisted("id") goalId: Int?,
        ) : CreateGoalViewModel
    }
}
