package com.example.habitflow.data.repository

import android.util.Log
import com.example.habitflow.data.local.tasks.TasksDao
import com.example.habitflow.data.mappers.toDto
import com.example.habitflow.data.mappers.toEntity
import com.example.habitflow.data.sync.DataSyncManager
import com.example.habitflow.domain.repository.SyncRepository
import javax.inject.Inject

class SyncRepositoryImpl @Inject constructor(
    private val dataSyncManager: DataSyncManager,
    private val tasksDao: TasksDao
) : SyncRepository {

    override suspend fun pullRemoteChanges() {

        dataSyncManager.getAll().forEach { taskDto ->

            taskDto.id?.let { id ->
                val localTask = tasksDao.getByIdRemote(id)
                when {
                    localTask == null -> {

                        tasksDao.insertFromRemoteToLocal(taskEntity =
                            taskDto.toEntity()
                        )
                    }

                    taskDto.updatedAt > localTask.updatedAt -> {
                        tasksDao.insertFromRemoteToLocal(
                            taskEntity = taskDto.toEntity(existingLocalId = localTask.id)
                        )
                    }

                    localTask.updatedAt > taskDto.updatedAt -> {

                        dataSyncManager.updateTask(taskDto)
                        tasksDao.markTaskAsSynced(localTask.id)
                    }
                }
            }
        }
    }

    override suspend fun pushLocalChanges() {

        val unsyncedTasks = tasksDao.getUnsyncedTasks()

        unsyncedTasks.forEach { taskEntity ->

            Log.d("unsyncedTasks", "${taskEntity.remoteId}")
            when {

                taskEntity.remoteId == null -> {
                    Log.d("pushLocalChanges", "task entity is null, need to create")
                    val remoteId = dataSyncManager.createTask(taskEntity.toDto())
                    tasksDao.updateRemoteId(remoteId, taskEntity.id)
                }

                else -> {

                    Log.d("pushLocalChanges", "task entity is not null, no need to create")
                    dataSyncManager.updateTask(
                        taskEntity.toDto(taskEntity.remoteId)
                    )
                    tasksDao.markTaskAsSynced(taskEntity.id)
                }
            }
        }
    }
}