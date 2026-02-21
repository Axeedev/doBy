package com.example.habitflow.presentation.screens.tasks.all

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.habitflow.domain.entities.goals.GoalCategory
import com.example.habitflow.domain.entities.tasks.Task
import com.example.habitflow.domain.usecases.achievements.OnTaskCompletedUseCase
import com.example.habitflow.domain.usecases.tasks.AddTaskToCompletedUseCase
import com.example.habitflow.domain.usecases.tasks.AddTaskUseCase
import com.example.habitflow.domain.usecases.tasks.DeleteTaskUseCase
import com.example.habitflow.domain.usecases.tasks.GetTasksUseCase
import com.example.habitflow.domain.usecases.tasks.ReturnTaskUseCase
import com.example.habitflow.presentation.screens.tasks.TimeEntity
import com.example.habitflow.presentation.utils.groupBySection
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
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
    private val returnTaskUseCase: ReturnTaskUseCase,
    private val deleteTaskUseCase: DeleteTaskUseCase,
    private val onTaskCompletedUseCase: OnTaskCompletedUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(TasksScreenState())

    val state
        get() = _state.asStateFlow()

    private val _unlockEvents = MutableSharedFlow<AchievementEvent>()
    val unlockEvent
        get() = _unlockEvents.asSharedFlow()

    private val _bottomSheetEvents = MutableSharedFlow<BottomSheetEvent>()
    val bottomSheetEvents
        get() = _bottomSheetEvents.asSharedFlow()


    init {
        viewModelScope.launch {
            getTasksUseCase().collect { tasks ->
                _state.update { previousState ->
                    val mapTasks = tasks.groupBySection(ZoneId.systemDefault())
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
                    _bottomSheetEvents.emit(BottomSheetEvent.CloseSheet)
                }
            }

            is TasksCommand.ChangePriority -> {
                _state.update {
                    it.copy(priority = command.priority)
                }
            }

            is TasksCommand.ClickCompleteTask -> {
                if (command.task.category != GoalCategory.CALENDAR) {
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
                        } else {
                            addTaskToCompletedUseCase(command.task.id)
                        }
                        val isAchieveUnlocked = if (!command.task.isReturned) {
                            onTaskCompletedUseCase()
                        } else false
                        if (isAchieveUnlocked) {
                            _unlockEvents.emit(AchievementEvent.AchievementUnlocked)
                        }
                    }
                }
            }

            is TasksCommand.ClickTask -> {
                viewModelScope.launch {
                    _bottomSheetEvents.emit(BottomSheetEvent.OpenSheet)
                }
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
                viewModelScope.launch { _bottomSheetEvents.emit(BottomSheetEvent.OpenSheet) }
                _state.update {
                    TasksScreenState(
                        tasksMapSections = it.tasksMapSections
                    )
                }
            }

            is TasksCommand.DeleteTask -> {
                viewModelScope.launch {
                    deleteTaskUseCase(command.taskId)
                    _bottomSheetEvents.emit(BottomSheetEvent.CloseSheet)
                }
            }

            TasksCommand.CloseBottomSheet -> {
                viewModelScope.launch {
                    _bottomSheetEvents.emit(BottomSheetEvent.CloseSheet)
                }
            }
        }
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