package com.example.habitflow.data.daily

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.os.Build
import com.example.habitflow.domain.entities.settings.NotificationTime
import com.example.habitflow.domain.usecases.settings.GetSettingsUseCase
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import java.util.Calendar
import javax.inject.Inject

class MorningAlarmScheduler @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val alarmManager: AlarmManager?,
    private val getSettingsUseCase: GetSettingsUseCase
) {
    suspend fun scheduleNextMorningAlarm() {
        val morningNotificationTime = getSettingsUseCase().first().morningInfoTime

        if (morningNotificationTime is NotificationTime) {

            val intent = MorningAlarmReceiver.newIntent(context)

            val pendingIntent = PendingIntent.getBroadcast(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val calendarMorning = Calendar.getInstance().apply {
                timeInMillis = System.currentTimeMillis()
                set(Calendar.HOUR_OF_DAY, 8)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                if (before(Calendar.getInstance())) {
                    add(Calendar.DAY_OF_YEAR, 1)
                }
            }

            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    if (alarmManager?.canScheduleExactAlarms() == true) {
                        alarmManager.setExactAndAllowWhileIdle(
                            AlarmManager.RTC_WAKEUP,
                            calendarMorning.timeInMillis,
                            pendingIntent
                        )
                    }
                }

            } catch (e: SecurityException) {
                e.printStackTrace()
            }
        }


    }
}