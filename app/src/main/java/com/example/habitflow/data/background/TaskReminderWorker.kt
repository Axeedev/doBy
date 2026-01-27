package com.example.habitflow.data.background

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.habitflow.data.local.NotificationsProvider
import com.example.habitflow.data.local.tasks.TasksDao
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject


@HiltWorker
class TaskReminderWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParameters: WorkerParameters,
    private val notificationsProvider: NotificationsProvider,
    private val tasksDao: TasksDao
): CoroutineWorker(appContext, workerParameters)
{
    override suspend fun doWork(): Result {
        val id = inputData.getLong(TASK_ID, -1)
        if (id != -1L){
            val task = tasksDao.getTaskById(id.toInt())
            notificationsProvider.showReminder(task.title)
            Log.d("TaskReminderWorker", "In doWork(), taskId: ${task.id}, tasktitle:${task.title}")
            return Result.success()
        }else return Result.failure()

    }

    companion object{
        const val TASK_ID = "TASK_ID"
        fun enqueue(
            context: Context,
            taskId: Long
        ){
            Log.d("TaskReminderWorker", "In worker")
            val data = workDataOf(
                TASK_ID to taskId
            )
            val request = OneTimeWorkRequestBuilder<TaskReminderWorker>()
                .setInputData(data)
                .build()
            WorkManager.getInstance(context).enqueue(request)
        }
    }

}