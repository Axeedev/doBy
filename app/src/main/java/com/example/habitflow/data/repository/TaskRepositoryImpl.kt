package com.example.habitflow.data.repository

import androidx.room.Transaction
import com.example.habitflow.data.TaskReminder
import com.example.habitflow.data.background.DataSyncScheduler
import com.example.habitflow.data.calendar.SystemCalendarManager
import com.example.habitflow.data.local.tasks.CompletedTasksDao
import com.example.habitflow.data.local.tasks.TasksDao
import com.example.habitflow.data.mappers.toCompletedTask
import com.example.habitflow.data.mappers.toCompletedTaskEntity
import com.example.habitflow.data.mappers.toTask
import com.example.habitflow.data.mappers.toTaskEntity
import com.example.habitflow.data.utils.toLong
import com.example.habitflow.domain.entities.tasks.CompletedTask
import com.example.habitflow.domain.entities.tasks.Task
import com.example.habitflow.domain.repository.TaskRepository
import com.example.habitflow.domain.usecases.settings.GetSettingsUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import javax.inject.Inject

class TaskRepositoryImpl @Inject constructor(
    private val tasksDao: TasksDao,
    private val completedTasksDao: CompletedTasksDao,
    private val taskReminder: TaskReminder,
    private val getSettingsUseCase: GetSettingsUseCase,
    private val systemCalendarManager: SystemCalendarManager,
    private val dataSyncScheduler: DataSyncScheduler
) : TaskRepository {

    override fun getCompletedTasks(): Flow<List<CompletedTask>> {
        return completedTasksDao.getCompletedTasks()
            .map { list ->
                list.map {
                    it.toCompletedTask()
                }
            }
    }

    private suspend fun startRefresh(){
        dataSyncScheduler.scheduleDataPush()
    }

    override fun getTasks(): Flow<List<Task>> {
        return combine(
            flow = tasksDao.getTasks(),
            flow2 = systemCalendarManager.getEventsAsTasks(),
            flow3 = getSettingsUseCase()
        ) { tasksFromDao, eventsFromCalendar, appSettings ->

            val tasksDomain = tasksFromDao.map { it.toTask() }

            if (appSettings.showCalendarEvents) {
                tasksDomain + eventsFromCalendar
            } else {
                tasksDomain
            }
        }
    }

    override suspend fun addTask(task: Task) {
        val adjustedDeadline = adjustTaskDeadline(task.deadlineMillis)

        val adjustedTask = task.copy(
            deadlineMillis = adjustedDeadline
        )

        val taskId = tasksDao.addTask(
            taskEntity = adjustedTask.toTaskEntity(
                taskId = task.id
            )
        )


        adjustedDeadline?.let { deadline ->
            taskReminder.schedule(
                taskId,
                deadline
            )
        }
    }



    private fun adjustTaskDeadline(deadlineMillis: Long?): Long? {
        return deadlineMillis?.let { deadline ->
            val now = System.currentTimeMillis()
            if (deadline < now) {
                val instant = Instant.ofEpochMilli(deadline)
                val timeOfDeadline = LocalDateTime.ofInstant(
                    instant,
                    ZoneId.systemDefault()
                )
                val localTime = LocalDateTime.now()
                if (timeOfDeadline.hour > localTime.hour) {
                    val localDateNow = LocalDate.now()
                        .atStartOfDay()
                        .plusHours(timeOfDeadline.hour.toLong())
                        .plusMinutes(timeOfDeadline.minute.toLong())
                    localDateNow
                        .toLong()
                } else {
                    LocalDate.now()
                        .atStartOfDay()
                        .plusDays(1)
                        .plusHours(timeOfDeadline.hour.toLong())
                        .plusMinutes(timeOfDeadline.minute.toLong())
                        .toLong()
                }
            } else {
                deadline
            }
        }
    }

    override suspend fun deleteTask(taskId: Int) {
        val taskToDelete = tasksDao.getTaskById(taskId)
//        tasksDao.deleteTask(taskId)
        tasksDao.addTask(
            taskToDelete.copy(
                isDeleted = true,
                isSynced = false,
                updatedAt = System.currentTimeMillis()
            )
        )
        taskReminder.cancelTask(taskId.toLong())
//        startRefresh()
    }

    override suspend fun updateTask(task: Task) {
        val taskEntity = tasksDao.getTaskById(task.id)

        val adjustedDeadline = adjustTaskDeadline(task.deadlineMillis)

        val adjustedTask = task.copy(
            deadlineMillis = adjustedDeadline
        )

        val taskId = tasksDao.addTask(
            taskEntity = adjustedTask
                .toTaskEntity(task.id)
                .copy(remoteId = taskEntity.remoteId)
        )
//        val taskId = tasksDao.addTask(
//            adjustedTask.toTaskEntity(
//                task.id
//            )
//        )
        taskReminder.cancelTask(taskId)
        adjustedDeadline?.let { deadline ->
            taskReminder.schedule(
                taskId,
                deadline
            )
        }
//        startRefresh()
    }

    @Transaction
    override suspend fun completeTask(taskId: Int) {
        val task = tasksDao.getTaskById(taskId)

        val appSettings = getSettingsUseCase().first()

//        if (!appSettings.showCompletedTasksOnMainScreen) {
//            tasksDao.deleteTask(taskId)
//        }
        completedTasksDao.addTaskToCompleted(
            completedTaskEntity = task.toCompletedTaskEntity(
                dateOfCompletion = System.currentTimeMillis()
            ).copy(isSynced = false)
        )
        deleteTask(taskId)
    }

    override suspend fun returnTask(taskId: Int) {
        val task = completedTasksDao.getTaskById(taskId)
        completedTasksDao.deleteCompletedTaskById(taskId)
        addTask(
            task.toTaskEntity().toTask()
        )
    }

    override fun getNumberOfCompletedTasks(): Flow<Int> {
        return completedTasksDao.getCompletedTasksSize()
    }
}