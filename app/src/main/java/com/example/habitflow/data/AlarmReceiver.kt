package com.example.habitflow.data

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.habitflow.data.local.NotificationsProvider
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class AlarmReceiver : BroadcastReceiver() {

    @Inject
    lateinit var notificationsProvider: NotificationsProvider

    @Inject
    lateinit var alarmScheduler: AlarmScheduler

    override fun onReceive(context: Context?, intent: Intent?) {
        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                notificationsProvider.showTodaysTasksNotification(10)
                alarmScheduler.scheduleNextAlarm()
            } finally {
                pendingResult.finish()
            }
        }
    }

    companion object{
        fun newIntent(context: Context): Intent{
            return Intent(context, AlarmReceiver::class.java)
        }
    }
}