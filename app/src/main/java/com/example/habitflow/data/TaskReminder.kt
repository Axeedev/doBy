package com.example.habitflow.data

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.os.Build
import android.util.Log
import com.example.habitflow.data.local.TaskDeadlineReceiver
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class TaskReminder @Inject constructor(
    @ApplicationContext private val context: Context,
    private val alarmManager: AlarmManager?
) {
    fun schedule(
        taskId: Long,
        deadline: Long
    ) {
        Log.d("TaskReminder", "Task scheduled")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
            && alarmManager?.canScheduleExactAlarms() == false) return
        val intent = TaskDeadlineReceiver.newIntent(context).apply {
            putExtra(TASK_ID, taskId)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            taskId.toInt(),
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        alarmManager?.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            deadline,
            pendingIntent
        )
    }
    companion object{
        const val TASK_ID = "TASK_ID"
    }
}