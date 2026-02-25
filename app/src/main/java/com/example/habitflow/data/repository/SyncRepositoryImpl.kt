package com.example.habitflow.data.repository

import android.util.Log
import com.example.habitflow.data.local.tasks.CompletedTasksDao
import com.example.habitflow.data.local.tasks.TasksDao
import com.example.habitflow.data.mappers.toDto
import com.example.habitflow.data.mappers.toEntity
import com.example.habitflow.data.sync.CompletedTasksSyncManager
import com.example.habitflow.data.sync.TasksSyncManager
import com.example.habitflow.domain.repository.SyncRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject

class SyncRepositoryImpl @Inject constructor(
    private val tasksSyncManager: TasksSyncManager,
    private val completedTasksSyncManager: CompletedTasksSyncManager,
    private val tasksDao: TasksDao,
    private val completedTasksDao: CompletedTasksDao
) : SyncRepository {

    override suspend fun pullRemoteChanges() = withContext(Dispatchers.IO) {
        pullTasksChanges()

    }

    override suspend fun pushLocalChanges() = withContext(Dispatchers.IO) {
        pushTasksChanges()
    }


    private suspend fun pullCompletedTasksChanges() {
        completedTasksSyncManager.getAll().forEach { completedTaskDto ->
            completedTaskDto.id?.let { id ->
                val localTask = completedTasksDao.getTaskByRemoteId(id)
                when {
                    localTask == null -> {
                        completedTasksDao.addTaskToCompleted(
                            completedTaskEntity = completedTaskDto.toEntity()
                        )
                    }

                    completedTaskDto.updatedAt > localTask.updatedAt -> {
                        completedTasksDao.addTaskToCompleted(
                            completedTaskEntity = completedTaskDto.toEntity(existingLocalId = localTask.id)
                        )
                    }

                    completedTaskDto.updatedAt < localTask.updatedAt -> {
                        completedTasksSyncManager.updateTask(
                            completedTaskDto
                        )
                        completedTasksDao.markCompletedTaskAsSynced(localTask.id)

                    }
                }
            }
        }
    }

    private suspend fun pullTasksChanges() {
        tasksSyncManager.getAll().forEach { taskDto ->
            taskDto.id?.let { id ->
                val localTask = tasksDao.getByIdRemote(id)

                when {
                    localTask == null -> {
                        tasksDao.insertFromRemoteToLocal(
                            taskEntity =
                                taskDto.toEntity()
                        )
                    }

                    taskDto.updatedAt > localTask.updatedAt -> {
                        Log.d("SyncRepositoryImpl", "$taskDto, $localTask")

                        tasksDao.insertFromRemoteToLocal(
                            taskEntity = taskDto.toEntity(existingLocalId = localTask.id)
                        )
                    }

                    localTask.updatedAt > taskDto.updatedAt -> {

                        tasksSyncManager.updateTask(taskDto)
                        tasksDao.markTaskAsSynced(localTask.id)
                    }
                }
            }
        }
    }

    private suspend fun pushTasksChanges() {
        val unsyncedTasks = tasksDao.getUnsyncedTasks()

        unsyncedTasks.forEach { taskEntity ->
            Log.d("pushTasksChanges", taskEntity.toString())

            Log.d("unsyncedTasks", "${taskEntity.remoteId}")
            when {

                taskEntity.remoteId == null -> {
                    Log.d("pushLocalChanges", "task entity is null, need to create")
                    val remoteId = tasksSyncManager.createTask(taskEntity.toDto())
                    tasksDao.updateRemoteId(remoteId, taskEntity.id)
                }

                else -> {
                    Log.d("pushLocalChanges", "task entity is not null, no need to create")
                    tasksSyncManager.updateTask(
                        taskEntity.toDto(taskEntity.remoteId)
                    )
                    tasksDao.markTaskAsSynced(taskEntity.id)
                }
            }
        }
    }

    private suspend fun pushCompletedTasksChanges() {

        val unsyncedCompleted = completedTasksDao.getUnsyncedTasks()

        unsyncedCompleted.forEach { completedTaskEntity ->

            val remoteId = completedTaskEntity.remoteId ?: UUID.randomUUID().toString()

            completedTaskEntity.remoteId?.let {
                tasksSyncManager.delete(it)
            }
            completedTasksDao.updateRemoteId(remoteId, completedTaskEntity.id)

            completedTasksSyncManager.createTask(
                completedTaskEntity.toDto(remoteId)
            )
            completedTasksDao.markCompletedTaskAsSynced(completedTaskEntity.id)
        }

    }

}