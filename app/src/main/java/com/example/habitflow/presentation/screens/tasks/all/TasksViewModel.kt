package com.example.habitflow.presentation.screens.tasks.all

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.habitflow.domain.entities.Task
import com.example.habitflow.domain.usecases.tasks.AddTaskToCompletedUseCase
import com.example.habitflow.domain.usecases.tasks.AddTaskUseCase
import com.example.habitflow.domain.usecases.tasks.GetTasksUseCase
import com.example.habitflow.domain.usecases.tasks.ReturnTaskUseCase
import com.example.habitflow.presentation.screens.tasks.creation.TimeEntity
import com.example.habitflow.presentation.utils.groupBySection
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId
import javax.inject.Inject

@HiltViewModel
class TasksViewModel @Inject constructor(
    private val getTasksUseCase: GetTasksUseCase,
    private val addTaskUseCase: AddTaskUseCase,
    private val addTaskToCompletedUseCase: AddTaskToCompletedUseCase,
    private val returnTaskUseCase: ReturnTaskUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(TasksScreenState())

    val state
        get() = _state.asStateFlow()

    init {
        viewModelScope.launch {
            getTasksUseCase().collect { tasks ->
                _state.update { previousState ->
                    Log.d("TasksViewModel", tasks.joinToString(", "))
                    val mapTasks = tasks.groupBySection(ZoneId.systemDefault())
                    Log.d("Map tasks", mapTasks.toString())
                    previousState.copy(tasksMapSections = mapTasks)
                }
            }
        }
    }

    fun processCommand(command: TasksCommand) {
        when (command) {
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
                    val deadlineMillis = finalTask.date?.let { date ->
                        finalTask.remindAtMinutesOfDay?.let {
                            combineDateAndTime(date, it.hours, it.minutes)
                        } ?: combineDateAndTime(date, 18, 0)
                    }
                    addTaskUseCase(
                        Task(
                            id = finalTask.taskId ?: 0,
                            title = finalTask.title,
                            deadlineMillis = deadlineMillis,
                            note = finalTask.description,
                            category = finalTask.goalCategory,
                            priority = finalTask.priority
                        )
                    )

                }
            }

            is TasksCommand.ChangePriority -> {
                _state.update {
                    it.copy(priority = command.priority)
                }
            }

            is TasksCommand.ClickCompleteTask -> {
                _state.update { previous ->
                    val previousList = previous.tasksMapSections[command.taskDeadlineSection]
                        .orEmpty()
                        .map { oldTask ->
                            if (oldTask == command.task) {
                                oldTask.copy(isCompleted = true)
                            } else oldTask
                        }
                    val newMap = previous.tasksMapSections.toMutableMap()
                    newMap[command.taskDeadlineSection] = previousList
                    previous.copy(tasksMapSections = newMap)
                }
                viewModelScope.launch {
                    if (command.task.isCompleted) {
                        returnTaskUseCase(command.task.id)
                    }else{
                        addTaskToCompletedUseCase(command.task.id)
                    }

                }
            }

            is TasksCommand.ClickTask -> {
                _state.update { previous ->
                    val zonedDateTime = command.task.deadlineMillis?.let { date ->
                        Instant.ofEpochMilli(date)
                            .atZone(ZoneId.systemDefault())
                    }
                    val date = zonedDateTime?.toLocalDate()?.atStartOfDay(ZoneId.systemDefault())
                        ?.toInstant()?.toEpochMilli()
                    val timeHour = zonedDateTime?.toLocalTime()?.hour
                    val timeMinute = zonedDateTime?.toLocalTime()?.minute
                    val remind = timeMinute?.let { minute ->
                        timeHour?.let { hour ->
                            TimeEntity(hour, minute)
                        }
                    }

                    previous.copy(
                        taskId = command.task.id,
                        title = command.task.title,
                        date = date,
                        remindAtMinutesOfDay = remind,
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
    companion object{
        private const val DEFAULT_HOUR = 18
        private const val DEFAULT_MINUTE = 0
    }
}

fun combineDateAndTime(
    dateMillis: Long,
    hour: Int,
    minute: Int
): Long {
    val zone = ZoneId.systemDefault()

    val date = Instant.ofEpochMilli(dateMillis)
        .atZone(zone)
        .toLocalDate()

    return date
        .atTime(hour, minute)
        .atZone(zone)
        .toInstant()
        .toEpochMilli()
}
