package com.example.habitflow.presentation.screens.tasks.all

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.habitflow.domain.entities.Task
import com.example.habitflow.domain.usecases.tasks.AddTaskUseCase
import com.example.habitflow.domain.usecases.tasks.DeleteTaskUseCase
import com.example.habitflow.domain.usecases.tasks.GetTasksUseCase
import com.example.habitflow.presentation.screens.tasks.creation.TimeEntity
import com.example.habitflow.presentation.utils.groupBySection
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.ZoneId
import javax.inject.Inject

@HiltViewModel
class TasksViewModel @Inject constructor(
    private val getTasksUseCase: GetTasksUseCase,
    private val addTaskUseCase: AddTaskUseCase,
    private val deleteTaskUseCase: DeleteTaskUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(TasksScreenState())

    val state
        get() = _state.asStateFlow()

    init {
        viewModelScope.launch {
            getTasksUseCase().collect { tasks ->
                _state.update { previousState ->
                    previousState.copy(tasksMapSections = tasks.groupBySection(ZoneId.systemDefault()))
                }
            }
        }
    }

    fun processCommand(command: TasksCommand){
        when(command){
            is TasksCommand.InputDate -> {
                _state.update {
                    it.copy(date = command.date)
                }
            }
            is TasksCommand.InputDescription -> {
                _state.update {
                    it.copy(description = command.description)
                }
            }
            is TasksCommand.InputDeadline -> {
                _state.update {
                    it.copy(remindAtMinutesOfDay = command.deadline)
                }
            }
            is TasksCommand.InputTitle -> {
                _state.update {
                    it.copy(title = command.title)
                }
            }

            is TasksCommand.ChangeCategory -> {
                _state.update {
                    it.copy(goalCategory = command.taskCategory)
                }
            }

            TasksCommand.AddTask -> {
                viewModelScope.launch {
                    val finalTask = _state.value
                    val remind = if (finalTask.remindAtMinutesOfDay != null){
                        finalTask.remindAtMinutesOfDay.hours * 60 + finalTask.remindAtMinutesOfDay.minutes
                    }else null
                    addTaskUseCase(
                        Task(
                            id = finalTask.taskId ?: 0,
                            title = finalTask.title,
                            date = finalTask.date,
                            note = finalTask.description,
                            remindAtMinutesOfDay = remind,
                            category = finalTask.goalCategory,
                            priority = finalTask.priority
                        )
                    )
                    _state.value = TasksScreenState()
                }
            }
            is TasksCommand.ChangePriority -> {
                _state.update {
                    it.copy(priority = command.priority)
                }
            }

            is TasksCommand.ClickCompleteTask -> {
                _state.update {previous ->
                    val previousList = previous.tasksMapSections[command.taskDeadlineSection]
                        .orEmpty()
                        .map {oldTask ->
                            if (oldTask == command.task){
                                oldTask.copy(isCompleted = true)
                            }else oldTask
                        }
                    val newMap = previous.tasksMapSections.toMutableMap()
                    newMap[command.taskDeadlineSection] = previousList
                    previous.copy(tasksMapSections = newMap)
                }
                viewModelScope.launch { deleteTaskUseCase(command.task.id) }
            }

            is TasksCommand.ClickTask -> {
                _state.update {previous ->
                    previous.copy(
                        taskId = command.task.id,
                        title = command.task.title,
                        date = command.task.date,
                        remindAtMinutesOfDay = command.task.remindAtMinutesOfDay?.let {
                            TimeEntity(
                                hours = it / 60,
                                minutes = it % 60
                            )
                        },
                        description = command.task.note,
                        priority = command.task.priority,
                        goalCategory = command.task.category,
                        buttonText = "Confirm"
                    )
                }
            }

            TasksCommand.ClickButtonAddTask -> {
                _state.update {
                    TasksScreenState(
                        tasksMapSections = it.tasksMapSections
                    )
                }
            }
        }
    }

}