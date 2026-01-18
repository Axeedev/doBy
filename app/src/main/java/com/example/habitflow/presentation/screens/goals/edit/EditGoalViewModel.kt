package com.example.habitflow.presentation.screens.goals.edit

import androidx.compose.runtime.toLong
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.habitflow.domain.usecases.goals.EditGoalUseCase
import com.example.habitflow.domain.usecases.goals.GetGoalByIdUseCase
import com.example.habitflow.presentation.screens.goals.create.CreateGoalScreenState
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = EditGoalViewModel.EditGoalFactory::class)
class EditGoalViewModel @AssistedInject constructor(
    private val editGoalUseCase: EditGoalUseCase,
    private val getGoalByIdUseCase: GetGoalByIdUseCase,
    @Assisted("goalId") val goalId: Int
) : ViewModel() {

    private val _state = MutableStateFlow(
        CreateGoalScreenState()
    )
    val state
        get() = _state.asStateFlow()


    init {
        viewModelScope.launch {
            val goal = getGoalByIdUseCase(goalId)
            _state.update { previous ->
                previous.copy(
                    title = goal.title,
                    coverUri = goal.coverUri?.toUri(),
                    goalCategory = goal.category,
                    description = goal.description,
                    endDate = goal.goalEndDate,
                    milestones = goal.milestones
                )
            }
        }
    }

    @AssistedFactory
    interface EditGoalFactory {
        fun create(
            @Assisted("goalId") goalId: Int,
        ): EditGoalViewModel

    }
}