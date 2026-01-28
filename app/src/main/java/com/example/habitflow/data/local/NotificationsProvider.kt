package com.example.habitflow.data.local

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.habitflow.R
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class NotificationsProvider @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val notificationManager: NotificationManager?
) {
    init {
        createNotificationChannels()
    }
    fun createNotificationChannels(){
        val notificationChannelTodaysTasks = NotificationChannel(
            TODAYS_TASKS_CHANNEL_ID,
            "Tasks today",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        notificationManager?.createNotificationChannel(notificationChannelTodaysTasks)
        val notificationChannelRemind = NotificationChannel(
            REMINDER_CHANNEL_ID,
            "Tasks reminder",
            NotificationManager.IMPORTANCE_HIGH
        )
        notificationManager?.createNotificationChannel(notificationChannelRemind)
        val notificationChannelAdvice = NotificationChannel(
            ADVICE_FOR_THE_DAY_ID,
            "Advice for the day",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        notificationManager?.createNotificationChannel(notificationChannelAdvice)
    }

    //TODO: Refactor code, implement only one function for showing notification

    fun showTodaysTasksNotification(
        todaysTasksSize: Int
    ){
        val notification = NotificationCompat.Builder(context, TODAYS_TASKS_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_calendar_today)
            .setContentTitle("На сегодня")
            .setContentText("Запланировано задач: $todaysTasksSize")
            .build()
        Log.d("showTodaysTasksNotification", notification.toString())
        notificationManager?.notify(NOTIFICATION_ID, notification)
    }

    fun showReminder(
        taskTitle: String
    ){
        val notification = NotificationCompat.Builder(context, REMINDER_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_task_app)
            .setContentTitle("Скоро дедлайн")
            .setContentText(taskTitle)
            .build()
        Log.d("showTodaysTasksNotification", notification.toString())
        notificationManager?.notify(NOTIFICATION_ID, notification)
    }

    fun showAdviceForTheDay(advice: String){
        val notification = NotificationCompat.Builder(context,ADVICE_FOR_THE_DAY_ID)
            .setSmallIcon(R.drawable.ic_advice)
            .setContentTitle("Совет дня")
            .setContentText(advice)
            .build()
        notificationManager?.notify(NOTIFICATION_ID, notification)
    }


    companion object{
        private const val TODAYS_TASKS_CHANNEL_ID = "today tasks"
        private const val REMINDER_CHANNEL_ID = "Tasks reminders"
        private const val ADVICE_FOR_THE_DAY_ID = "advice for the day"

        private const val NOTIFICATION_ID = 1
    }

}