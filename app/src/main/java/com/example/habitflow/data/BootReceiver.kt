package com.example.habitflow.data

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.habitflow.data.daily.MorningAlarmScheduler
import com.example.habitflow.data.daily.NightAlarmScheduler
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class BootReceiver: BroadcastReceiver() {

    @Inject
    lateinit var taskReminder: TaskReminder

    @Inject
    lateinit var morningAlarmScheduler: MorningAlarmScheduler

    @Inject
    lateinit var nightAlarmScheduler: NightAlarmScheduler

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == Intent.ACTION_BOOT_COMPLETED){
            val pendingResult = goAsync()
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    taskReminder.rescheduleTasks()
                    morningAlarmScheduler.scheduleNextMorningAlarm()
                    nightAlarmScheduler.scheduleNextNightAlarm()
                } finally {
                    pendingResult.finish()
                }
            }
        }
    }
}