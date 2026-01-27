package com.example.habitflow.data.repository

import android.util.Log
import com.example.habitflow.data.AlarmScheduler
import com.example.habitflow.data.TaskReminder
import com.example.habitflow.data.local.tasks.TasksDao
import com.example.habitflow.data.mappers.toTask
import com.example.habitflow.data.mappers.toTaskEntity
import com.example.habitflow.domain.entities.Task
import com.example.habitflow.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class TaskRepositoryImpl @Inject constructor(
    private val tasksDao: TasksDao,
    private val alarmScheduler: AlarmScheduler,
    private val taskReminder: TaskReminder
) : TaskRepository {
    override fun getTasks(): Flow<List<Task>> {
        return tasksDao.getTasks().map {list ->
            list.map{
                it.toTask()
            }
        }
    }

    override suspend fun addTask(task: Task) {
        Log.d("TaskRepositoryImpl", "Add task call")
        val taskId = tasksDao.addTask(
            task.toTaskEntity(
                task.id
            )
        )
        if (task.id == 0){
            Log.d("TaskRepositoryImpl", "id = 0, this is new task")
            task.deadlineMillis?.let {deadline ->
                val notifyAt = deadline - TimeUnit.HOURS.toMillis(1)
                taskReminder.schedule(taskId, notifyAt)
            }
        }else{

        }
    }

    override suspend fun deleteTask(taskId: Int) {
        tasksDao.deleteTask(taskId)
    }

    override suspend fun updateTask(task: Task) {
        tasksDao.addTask(task.toTaskEntity(task.id))
    }

    override suspend fun changeTaskCompletedState(taskId: Int) {
        tasksDao.changeTaskCompletedState(taskId)
    }
}