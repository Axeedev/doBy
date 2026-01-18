package com.example.habitflow.presentation.screens.goals.edit

import androidx.core.net.toUri
import androidx.lifecycle.viewModelScope
import com.example.habitflow.domain.usecases.goals.AddGoalUseCase
import com.example.habitflow.domain.usecases.goals.GetGoalByIdUseCase
import com.example.habitflow.domain.usecases.goals.UpdateGoalUseCase
import com.example.habitflow.presentation.screens.goals.create.CreateGoalViewModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = EditGoalViewModel.EditGoalFactory::class)
class EditGoalViewModel @AssistedInject constructor(
    private val getGoalByIdUseCase: GetGoalByIdUseCase,
    addGoalUseCase: AddGoalUseCase,
    updateGoalUseCase: UpdateGoalUseCase,
    @Assisted("goalId") val goalId: Int
) : CreateGoalViewModel(addGoalUseCase, updateGoalUseCase) {

    init {
        viewModelScope.launch {
            val goal = getGoalByIdUseCase(goalId)
            _state.update { previous ->
                previous.copy(
                    goalId = goalId,
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

    @AssistedFactory
    interface EditGoalFactory {
        fun create(
            @Assisted("goalId") goalId: Int
        ): EditGoalViewModel

    }
}