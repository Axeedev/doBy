package com.example.habitflow.data.local

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.habitflow.R
import com.example.habitflow.data.utils.toChannelImportance
import com.example.habitflow.domain.entities.Priority
import com.example.habitflow.presentation.MainActivity
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class NotificationsProvider @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val notificationManager: NotificationManager?
) {
    private val intent = Intent(context, MainActivity::class.java)
    private val pendingIntent = PendingIntent.getActivity(
        context,
        0,
        intent,
        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT

    )

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


        val notificationChannelLowImportance = NotificationChannel(
            TASK_LOW,
            "Task reminder low importance",
            NotificationManager.IMPORTANCE_LOW
        )
        notificationManager?.createNotificationChannel(notificationChannelLowImportance)


        val notificationChannelMedImportance = NotificationChannel(
            TASK_MED,
            "Task reminder medium importance",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        notificationManager?.createNotificationChannel(notificationChannelMedImportance)

        val notificationChannelHighImportance = NotificationChannel(
            TASK_HIGH,
            "Task reminder high importance",
            NotificationManager.IMPORTANCE_HIGH
        )
        notificationManager?.createNotificationChannel(notificationChannelHighImportance)

        val notificationChannelAdvice = NotificationChannel(
            ADVICE_FOR_THE_DAY_ID,
            "Advice for the day",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        notificationManager?.createNotificationChannel(notificationChannelAdvice)
    }

    //TODO: Refactor code, implement only one function for showing notification


    fun showNotification(
        channelId: String,
        smallIconId: Int,
        contentTitle: String,
        contentText: String,

    ){
        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT

        )
        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(smallIconId)
            .setContentTitle(contentTitle)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setContentText(contentText)
            .build()
        Log.d("showTodaysTasksNotification", notification.toString())
        notificationManager?.notify(NOTIFICATION_ID, notification)

    }

    fun showTodaysTasksNotification(
        todaysTasksSize: Int
    ){
        val notification = NotificationCompat.Builder(context, TODAYS_TASKS_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_calendar_today)
            .setContentTitle("На сегодня")
            .setContentIntent(pendingIntent)
            .setContentText("Запланировано задач: $todaysTasksSize")
            .build()
        Log.d("showTodaysTasksNotification", notification.toString())
        notificationManager?.notify(NOTIFICATION_ID, notification)
    }

    fun showReminder(
        taskTitle: String,
        taskPriority: Priority
    ){
        val channelIdImportance = taskPriority.toChannelImportance()
        val notification = NotificationCompat.Builder(context, channelIdImportance)
            .setSmallIcon(R.drawable.ic_task_app)
            .setContentTitle("Скоро дедлайн")
            .setContentText(taskTitle)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()
        Log.d("showTodaysTasksNotification", notification.toString())
        notificationManager?.notify(NOTIFICATION_ID, notification)
    }

    fun showAdviceForTheDay(advice: String){
        val notification = NotificationCompat.Builder(context,ADVICE_FOR_THE_DAY_ID)
            .setSmallIcon(R.drawable.ic_advice)
            .setContentTitle("Совет дня")
            .setContentText(advice)
            .setContentIntent(pendingIntent)
            .build()
        notificationManager?.notify(NOTIFICATION_ID, notification)
    }


    companion object{

        private const val TODAYS_TASKS_CHANNEL_ID = "today tasks"
        private const val ADVICE_FOR_THE_DAY_ID = "advice for the day"
        const val TASK_LOW = "Low priority tasks"
        const val TASK_MED = "Medium priority tasks"
        const val TASK_HIGH = "High priority tasks"

        private const val NOTIFICATION_ID = 1
    }

}