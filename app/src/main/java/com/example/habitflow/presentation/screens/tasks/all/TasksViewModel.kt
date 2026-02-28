package com.example.habitflow.presentation.screens.tasks.all

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.example.habitflow.data.background.VoiceToTaskWorker
import com.example.habitflow.domain.VoiceRecordResult
import com.example.habitflow.domain.entities.Category
import com.example.habitflow.domain.entities.tasks.Task
import com.example.habitflow.domain.usecases.achievements.OnTaskCompletedUseCase
import com.example.habitflow.domain.usecases.tasks.AddTaskToCompletedUseCase
import com.example.habitflow.domain.usecases.tasks.AddTaskUseCase
import com.example.habitflow.domain.usecases.tasks.DeleteTaskUseCase
import com.example.habitflow.domain.usecases.tasks.EditTaskUseCase
import com.example.habitflow.domain.usecases.tasks.GetTasksUseCase
import com.example.habitflow.domain.usecases.tasks.ReturnTaskUseCase
import com.example.habitflow.domain.usecases.voice.StartVoiceRecordingUseCase
import com.example.habitflow.domain.usecases.voice.StopRecordingUseCase
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
    private val editTaskUseCase: EditTaskUseCase,
    private val addTaskToCompletedUseCase: AddTaskToCompletedUseCase,
    private val returnTaskUseCase: ReturnTaskUseCase,
    private val deleteTaskUseCase: DeleteTaskUseCase,
    private val onTaskCompletedUseCase: OnTaskCompletedUseCase,
    private val startVoiceRecordingUseCase: StartVoiceRecordingUseCase,
    private val stopRecordingUseCase: StopRecordingUseCase,
    private val workManager: WorkManager
) : ViewModel() {

    private val _state = MutableStateFlow(TasksScreenState())

    val state
        get() = _state.asStateFlow()

    private val _snackbarEvents = MutableSharedFlow<SnackbarEvent>()
    val snackbarEvents
        get() = _snackbarEvents.asSharedFlow()


    init {
        viewModelScope.launch {
            subscribeTasks()
        }
        viewModelScope.launch {
            subscribeWorkInfo()
        }
    }

    private suspend fun subscribeTasks() {
        getTasksUseCase().collect { tasks ->
            _state.update { previousState ->
                val mapTasks = tasks.groupBySection(ZoneId.systemDefault())
                previousState.copy(tasksMapSections = mapTasks)
            }
        }
    }

    private suspend fun subscribeWorkInfo() {
        workManager.getWorkInfosByTagFlow(
            VoiceToTaskWorker.VOICE_RECOGNIZE_WORKER_TAG
        ).collect { workInfos ->
            workInfos.forEach { workInfo ->

                when (workInfo.state) {
                    WorkInfo.State.FAILED -> {
                        _state.update {
                            it.copy(isRefreshLoading = false)
                        }
                    }

                    WorkInfo.State.SUCCEEDED -> {
                        _state.update {
                            it.copy(isRefreshLoading = false)
                        }
                    }

                    else -> {
                        _state.update {
                            it.copy(isRefreshLoading = true)
                        }
                    }
                }
            }
        }
    }

    fun processCommand(command: TasksCommand) {
        when (command) {
            TasksCommand.StartVoiceInput -> {
                _state.update {
                    it.copy(isVoiceRecording = true)
                }
                viewModelScope.launch {
                    startVoiceInput()
                }
            }

            is TasksCommand.StopVoiceInput -> {
                _state.update {
                    it.copy(isVoiceRecording = false)
                }
                viewModelScope.launch {
                    stopRecordingUseCase()
                }
            }


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
                    it.copy(category = command.taskCategory)
                }
            }

            TasksCommand.AddTask -> {
                _state.update { it.copy(showBottomSheet = false) }
                viewModelScope.launch {
                    addTask()
                }
            }

            is TasksCommand.ChangePriority -> {
                _state.update {
                    it.copy(priority = command.priority)
                }
            }

            is TasksCommand.ClickCompleteTask -> {
                viewModelScope.launch {
                    clickCompleteTask(command)
                }
            }

            is TasksCommand.ClickTask -> {
                clickTask(command)
            }

            TasksCommand.ClickButtonAddTask -> {
                _state.update {
                    TasksScreenState(
                        tasksMapSections = it.tasksMapSections,
                        showBottomSheet = true
                    )
                }
            }

            is TasksCommand.DeleteTask -> {
                _state.update { it.copy(showBottomSheet = false) }
                viewModelScope.launch {
                    deleteTaskUseCase(command.taskId)
                }
            }

            TasksCommand.CloseBottomSheet -> {
                _state.update { it.copy(showBottomSheet = false) }
            }

            is TasksCommand.AddNewCategory -> {
                _state.update {
                    val newList = Category.defaultCategories + Category(command.categoryName)
                    it.copy(
                        categories = newList,
                        newCategoryName = "",
                        category = newList.last()
                    )
                }
            }

            is TasksCommand.InputCategoryName -> {
                _state.update { it.copy(newCategoryName = command.name) }
            }
        }
    }

    private suspend fun startVoiceInput() {
        val recordResult = startVoiceRecordingUseCase()
        if (recordResult is VoiceRecordResult.Error) {
            _state.update { it.copy(isVoiceRecording = false) }
            _snackbarEvents.emit(SnackbarEvent.VoiceRecordError)
        }
    }

    private suspend fun addTask() {
        val finalTask = _state.value
        val deadlineMillis = finalTask.date?.let { date ->
            finalTask.remindAtMinutesOfDay?.let {
                combineDateAndTime(date, it.hours, it.minutes)
            } ?: combineDateAndTime(date, 18, 0)
        }
        if (finalTask.taskId == null) {
            addTaskUseCase(
                Task(
                    id = 0,
                    title = finalTask.title,
                    deadlineMillis = deadlineMillis,
                    description = finalTask.description,
                    category = finalTask.category,
                    priority = finalTask.priority
                )
            )
        } else {
            editTaskUseCase(
                Task(
                    id = finalTask.taskId,
                    title = finalTask.title,
                    deadlineMillis = deadlineMillis,
                    description = finalTask.description,
                    category = finalTask.category,
                    priority = finalTask.priority
                )
            )
        }
    }

    private suspend fun clickCompleteTask(
        command: TasksCommand.ClickCompleteTask
    ) {
        if (command.task.isCompleted) {
            returnTaskUseCase(command.task.id)
        } else {
            addTaskToCompletedUseCase(command.task.id)
        }
        val isAchieveUnlocked = if (!command.task.isReturned) {
            onTaskCompletedUseCase()
        } else false
        if (isAchieveUnlocked) {
            _snackbarEvents.emit(SnackbarEvent.SnackbarUnlocked)
        }

    }

    private fun clickTask(command: TasksCommand.ClickTask) {
        if (command.task.category.name != Category.CALENDAR_NAME) {
            _state.update { previous ->
                val zonedDateTime = command.task.deadlineMillis?.let { date ->
                    Instant.ofEpochMilli(date)
                        .atZone(ZoneId.systemDefault())
                }
                val date =
                    zonedDateTime?.toLocalDate()?.atStartOfDay(ZoneId.systemDefault())
                        ?.toInstant()?.toEpochMilli()
                val timeHour = zonedDateTime?.toLocalTime()?.hour
                val timeMinute = zonedDateTime?.toLocalTime()?.minute
                val remind = timeMinute?.let { minute ->
                    timeHour?.let { hour ->
                        TimeEntity(hour, minute)
                    }
                }

                val taskCategory = command.task.category

                val newList = Category.defaultCategories + taskCategory

                previous.copy(
                    taskId = command.task.id,
                    title = command.task.title,
                    date = date,
                    remindAtMinutesOfDay = remind,
                    description = command.task.description,
                    priority = command.task.priority,
                    category = command.task.category,
                    categories = newList,
                    buttonText = "Confirm",
                    showBottomSheet = true
                )
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