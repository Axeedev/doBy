package com.example.habitflow.presentation.screens.tasks.all

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.habitflow.domain.usecases.tasks.AddTaskUseCase
import com.example.habitflow.domain.usecases.tasks.DeleteTaskUseCase
import com.example.habitflow.domain.usecases.tasks.GetTasksUseCase
import com.example.habitflow.domain.entities.Priority
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TasksViewModel @Inject constructor(
    private val getTasksUseCase: GetTasksUseCase,
    private val deleteTaskUseCase: DeleteTaskUseCase,
    private val addTaskUseCase: AddTaskUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(TasksScreenState())

    val state
        get() = _state.asStateFlow()

    init {
        viewModelScope.launch {
            getTasksUseCase().collect { tasks ->
                _state.update { previousState ->
                    previousState.copy(todayTasks = tasks)
                }
            }
        }
    }

    fun processCommand(command: TasksCommand) {
        when (command) {
            TasksCommand.AddTask -> {
                _state.value.currentTask?.let { task ->
                    viewModelScope.launch {
                        addTaskUseCase(task)
                    }
                }
            }

            TasksCommand.ChangePriority -> {
                _state.update { previousState ->
                    val previousPriority = previousState.taskPriority
                    val newPriorityIndex =
                        (Priority.entries.indexOf(previousPriority) + 1) % Priority.entries.size
                    previousState.copy(taskPriority = Priority.entries[newPriorityIndex])
                }
            }

            is TasksCommand.InputTaskNote -> {
                _state.update { previousState ->
                    previousState.copy(taskNote = command.taskNote)
                }
            }

            is TasksCommand.InputTaskTitle -> {
                _state.update { previousState ->
                    previousState.copy(taskTitle = command.taskTitle)
                }
            }

            TasksCommand.DeleteTask -> {
                _state.value.currentTask?.let { task ->
                    viewModelScope.launch {
                        deleteTaskUseCase(task.id)
                    }
                }
            }
        }
    }

}