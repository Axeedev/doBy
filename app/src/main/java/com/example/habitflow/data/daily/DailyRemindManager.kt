package com.example.habitflow.data.daily

import com.example.habitflow.data.local.NotificationsProvider
import com.example.habitflow.data.local.tasks.CompletedTasksDao
import com.example.habitflow.data.local.tasks.TasksDao
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import javax.inject.Inject

class DailyRemindManager @Inject constructor(
    private val tasksDao: TasksDao,
    private val completedTasksDao: CompletedTasksDao,
    private val notificationsProvider: NotificationsProvider
){

    private fun getStartOfToday(startOfToday: LocalDateTime) : Long{
        return startOfToday
            .atZone(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()
    }

    private fun getEndOfToday(endOfToday: LocalDateTime) : Long{
        return endOfToday
            .atZone(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()
    }

    suspend fun getNotificationAboutTodaysTasks(){

        val startOfToday = LocalDateTime.now()
        val endOfToday = LocalDate.now().atStartOfDay().plusDays(1)

        val startOfTodayDayMillis = getStartOfToday(startOfToday)

        val endOfDayMillis = getEndOfToday(endOfToday)

        val countOfScheduledTasks = tasksDao.getCountOfScheduledTasksForPeriod(
            startOfTodayDayMillis,
            endOfDayMillis
        )
        notificationsProvider.showTodaysTasksNotification(countOfScheduledTasks)
    }

    suspend fun getNotificationAboutTodaysSummary(){

        val startOfToday = LocalDate.now().atStartOfDay()
        val endOfToday = LocalDate.now().atStartOfDay().plusDays(1)

        val startOfTodayDayMillis = getStartOfToday(startOfToday)

        val endOfDayMillis = getEndOfToday(endOfToday)

        val countOfCompletedTasks = completedTasksDao.getCountOfTasksForPeriod(
            startOfTodayDayMillis,
            endOfDayMillis
        )

        notificationsProvider.showTodaysCompletedTasksNotification(countOfCompletedTasks)
    }


}