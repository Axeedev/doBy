package com.example.habitflow.presentation.screens.tasks.creation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.habitflow.domain.entities.Task
import com.example.habitflow.domain.usecases.tasks.AddTaskUseCase
import com.example.habitflow.domain.entities.Priority
import com.example.habitflow.domain.entities.TaskCategory
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
            is CreateTaskCommand.InputEndTime -> {
                _state.update {
                    it.copy(endTime = command.endTime)
                }
            }
            is CreateTaskCommand.InputStartTime -> {
                _state.update {
                    it.copy(startTime = command.startTime)
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
                    addTaskUseCase(
                        Task(
                            id = 0,
                            title = finalTask.title,
                            date = finalTask.date,
                            note = finalTask.description,
                            startTime = finalTask.startTime,
                            endTime = finalTask.endTime,
                            category = finalTask.taskCategory,
                            priority = finalTask.priority
                        )
                    )
                }
            }
            CreateTaskCommand.ChangePriority -> {
                _state.update {
                    val previousPriorityIndex = Priority.entries.indexOf(it.priority)
                    val newPriority = Priority.entries[(previousPriorityIndex + 1) % Priority.entries.size]
                    it.copy(priority = newPriority)
                }
            }
        }
    }
}