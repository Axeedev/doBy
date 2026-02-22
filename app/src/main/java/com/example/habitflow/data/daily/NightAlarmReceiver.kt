package com.example.habitflow.data.daily

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class NightAlarmReceiver : BroadcastReceiver() {

    @Inject
    lateinit var dailyRemindManager: DailyRemindManager

    @Inject
    lateinit var nightAlarmScheduler: NightAlarmScheduler

    override fun onReceive(context: Context?, intent: Intent?) {
        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                dailyRemindManager.getNotificationAboutTodaysSummary()
                nightAlarmScheduler.scheduleNextNightAlarm()
            } finally {
                pendingResult.finish()
            }
        }
    }

    companion object{
        fun newIntent(context: Context): Intent{
            return Intent(context, NightAlarmReceiver::class.java)
        }
    }
}