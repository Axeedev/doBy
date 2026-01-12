package com.example.habitflow.presentation.screens.tasks.creation

import androidx.lifecycle.ViewModel
import com.example.habitflow.domain.usecases.tasks.AddTaskUseCase
import com.example.habitflow.presentation.screens.tasks.Category
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class CreateTaskViewModel @Inject constructor(
    private val addTaskUseCase: AddTaskUseCase
) : ViewModel(){
    private val _state = MutableStateFlow(
        CreateTaskScreenState()
    )
    val state
        get() = _state.asStateFlow()

    fun processCommand(command: CreateTaskCommand){
        when(command){
            is CreateTaskCommand.InputDate -> {
                _state.update {
                    it.copy(date = command.date)
                }
            }
            is CreateTaskCommand.InputDescription -> {
                _state.update {
                    it.copy(date = command.description)
                }
            }
            is CreateTaskCommand.InputEndTime -> {
                _state.update {
                    it.copy(date = command.endTime)
                }
            }
            is CreateTaskCommand.InputStartTime -> {
                _state.update {
                    it.copy(date = command.startTime)
                }
            }
            is CreateTaskCommand.InputTitle -> {
                _state.update {
                    it.copy(date = command.title)
                }
            }

            is CreateTaskCommand.ChangeCategory -> {
                _state.update {
                    it.copy(category = command.category)
                }
            }
        }
    }

    sealed interface CreateTaskCommand{

        data class InputTitle(val title: String) : CreateTaskCommand

        data class InputDate(val date: String) : CreateTaskCommand

        data class InputStartTime(val startTime: String) : CreateTaskCommand

        data class InputEndTime(val endTime: String) : CreateTaskCommand

        data class InputDescription(val description: String) : CreateTaskCommand

        data class ChangeCategory(val category: Category) : CreateTaskCommand


    }
}