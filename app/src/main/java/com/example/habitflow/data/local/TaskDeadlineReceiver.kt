package com.example.habitflow.data.local

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.habitflow.data.TaskReminder
import com.example.habitflow.data.background.TaskReminderWorker

class TaskDeadlineReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        context?.let { context ->
            intent?.let { intent ->
                val taskId = intent.getLongExtra(TaskReminder.TASK_ID, -1L)
                if (taskId == -1L) return
                TaskReminderWorker.enqueue(context, taskId)

            }
        }
    }
    companion object{
        fun newIntent(context: Context): Intent{
            return Intent(context, TaskDeadlineReceiver::class.java)
        }
    }
}