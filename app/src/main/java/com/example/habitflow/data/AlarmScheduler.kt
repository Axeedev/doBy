package com.example.habitflow.data

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.os.Build
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.Calendar
import javax.inject.Inject

class AlarmScheduler @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val alarmManager: AlarmManager?
) {
    fun scheduleNextAlarm(){
        val intent = AlarmReceiver.newIntent(context)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, 8)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            if (before(Calendar.getInstance())) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S){
                if (alarmManager?.canScheduleExactAlarms() == true) {
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        calendar.timeInMillis,
                        pendingIntent
                    )
                } else {
                    Log.d("AlarmScheduler", "Exact alarms are not allowed")
                }
            }

        } catch (e: SecurityException) {
            Log.d("AlarmScheduler", "exception: ${e.toString()}")
        }
    }
}