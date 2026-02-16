package com.example.habitflow.data

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.os.Build
import android.util.Log
import com.example.habitflow.data.local.TaskDeadlineReceiver
import com.example.habitflow.domain.usecases.settings.GetSettingsUseCase
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class TaskReminder @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val alarmManager: AlarmManager?,
    private val getSettingsUseCase: GetSettingsUseCase
) {
    suspend fun schedule(
        taskId: Long,
        deadline: Long
    ) {
        Log.d("TaskReminder", "Task scheduled")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
            && alarmManager?.canScheduleExactAlarms() == false) return
        val intent = TaskDeadlineReceiver.newIntent(context).apply {
            putExtra(TASK_ID, taskId)
        }
        val settings = getSettingsUseCase().first()
        val remindBefore = settings.sendNotificationBeforeDeadline.beforeMinutes.toLong()
        val reminderBeforeMillis = TimeUnit.MINUTES.toMillis(remindBefore)
        val remindTime = deadline - reminderBeforeMillis
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            taskId.toInt(),
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        alarmManager?.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            remindTime,
            pendingIntent
        )
    }
    fun cancelTask(taskId: Long){
        val intent = TaskDeadlineReceiver.newIntent(context)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            taskId.toInt(),
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )
        pendingIntent?.let {
            alarmManager?.cancel(it)
        }
    }
    companion object{
        const val TASK_ID = "TASK_ID"
    }
}