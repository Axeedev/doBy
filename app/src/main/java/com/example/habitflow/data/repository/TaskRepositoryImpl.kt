package com.example.habitflow.data.repository

import com.example.habitflow.data.TaskReminder
import com.example.habitflow.data.local.tasks.CompletedTasksDao
import com.example.habitflow.data.local.tasks.TasksDao
import com.example.habitflow.data.mappers.toCompletedTask
import com.example.habitflow.data.mappers.toCompletedTaskEntity
import com.example.habitflow.data.mappers.toTask
import com.example.habitflow.data.mappers.toTaskEntity
import com.example.habitflow.domain.entities.CompletedTask
import com.example.habitflow.domain.entities.Task
import com.example.habitflow.domain.repository.TaskRepository
import com.example.habitflow.domain.usecases.settings.GetSettingsUseCase
import com.example.habitflow.domain.usecases.tasks.GetTasksUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class TaskRepositoryImpl @Inject constructor(
    private val tasksDao: TasksDao,
    private val completedTasksDao: CompletedTasksDao,
    private val taskReminder: TaskReminder,
    private val getSettingsUseCase: GetSettingsUseCase
) : TaskRepository {

    override fun getCompletedTasks(): Flow<List<CompletedTask>> {
        return completedTasksDao.getCompletedTasks().map { list ->
            list.map { it.toCompletedTask()
            }
        }
    }

    override fun getTasks(): Flow<List<Task>> {
        return tasksDao.getTasks().map {list ->
            list.map{
                it.toTask()
            }
        }
    }

    override suspend fun addTask(task: Task) {
        val taskId = tasksDao.addTask(
            task.toTaskEntity(
                task.id
            )
        )
        if (task.id != 0){
            taskReminder.cancelTask(taskId)
        }
        task.deadlineMillis?.let { deadline ->
            taskReminder.schedule(taskId, deadline)
        }
    }

    override suspend fun deleteTask(taskId: Int) {
        tasksDao.deleteTask(taskId)
        taskReminder.cancelTask(taskId.toLong())
    }

    override suspend fun updateTask(task: Task) {
        tasksDao.addTask(task.toTaskEntity(task.id))
    }

    override suspend fun completeTask(taskId: Int) {
        val task = tasksDao.getTaskById(taskId)
        val appSettings = getSettingsUseCase().first()
        if (!appSettings.showCompletedTasksOnMainScreen){
            tasksDao.deleteTask(taskId)
        }
        completedTasksDao.addTaskToCompleted(
            task.toCompletedTaskEntity(
                dateOfCompletion = System.currentTimeMillis()
            )
        )
    }

    override suspend fun returnTask(taskId: Int) {
        val task = completedTasksDao.getTaskById(taskId)
        completedTasksDao.deleteCompletedTaskById(taskId)
        tasksDao.addTask(task.toTaskEntity())
    }
}