package com.example.habitflow.data.local

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.habitflow.data.TaskReminder
import com.example.habitflow.data.background.TaskReminderWorker

class TaskDeadlineReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d("TaskDeadlineReceiver", "Broadcast receiver: context:$context, intent:$intent")
        context?.let { context ->
            intent?.let { intent ->
                val taskId = intent.getLongExtra(TaskReminder.TASK_ID, -1L)
                if (taskId == -1L) return
                TaskReminderWorker.enqueue(context, taskId)

                Log.d("TaskDeadlineReceiver", "Broadcast receiver: work enqueued")
            }
        }
    }
    companion object{
        fun newIntent(context: Context): Intent{
            return Intent(context, TaskDeadlineReceiver::class.java)
        }
    }
}