package com.example.habitflow.presentation.screens.tasks.creation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.habitflow.domain.usecases.tasks.AddTaskUseCase
import com.example.habitflow.presentation.utils.DateFormatter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
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
                    it.copy(date = DateFormatter.formatDate(command.date))
                }
            }
            is CreateTaskCommand.InputDescription -> {
                _state.update {
                    it.copy(description = command.description)
                }
            }
            is CreateTaskCommand.InputDeadline -> {
                _state.update {
                    it.copy(remindAtMinutesOfDay = command.deadline)
                }
            }
            is CreateTaskCommand.InputTitle -> {
                _state.update {
                    it.copy(title = command.title)
                }
            }

            is CreateTaskCommand.ChangeCategory -> {
                _state.update {
                    it.copy(taskCategory = command.taskCategory)
                }
            }

            CreateTaskCommand.AddTask -> {
                viewModelScope.launch {
                    val finalTask = _state.value
                    val remind = if (finalTask.remindAtMinutesOfDay != null){
                        finalTask.remindAtMinutesOfDay.hours * 60 + finalTask.remindAtMinutesOfDay.minutes
                    }else null

                }
            }
            is CreateTaskCommand.ChangePriority -> {
                _state.update {
                    it.copy(priority = command.priority)
                }
            }
        }
    }
}