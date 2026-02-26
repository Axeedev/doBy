package com.example.habitflow.data.daily


import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.os.Build
import android.util.Log
import com.example.habitflow.domain.entities.settings.NotificationTime
import com.example.habitflow.domain.usecases.settings.GetSettingsUseCase
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Calendar
import javax.inject.Inject

class NightAlarmScheduler @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val alarmManager: AlarmManager?,
    private val getSettingsUseCase: GetSettingsUseCase
) {
    suspend fun scheduleNextNightAlarm() {
        val nightNotificationTime = getSettingsUseCase().first().nightInfoTime

        if (nightNotificationTime is NotificationTime) {
            val intent = NightAlarmReceiver.newIntent(context)
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                1,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            val calendarNight = Calendar.getInstance().apply {
                timeInMillis = System.currentTimeMillis()
                set(Calendar.HOUR_OF_DAY, 22)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                if (before(Calendar.getInstance())) {
                    add(Calendar.DAY_OF_YEAR, 1)
                }
            }
            val instant = calendarNight.toInstant()
            val a = LocalDateTime.ofInstant(
                instant,
                ZoneId.systemDefault()
            )
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    if (alarmManager?.canScheduleExactAlarms() == true) {
                        alarmManager.setExactAndAllowWhileIdle(
                            AlarmManager.RTC_WAKEUP,
                            calendarNight.timeInMillis,
                            pendingIntent
                        )

                    } else {
                        TODO()
                    }
                }

            } catch (e: SecurityException) {
                Log.d("NightAlarmScheduler", "exception: ${e.toString()}")
            }
        }


    }
}