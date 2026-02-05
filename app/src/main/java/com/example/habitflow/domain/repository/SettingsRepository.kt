package com.example.habitflow.domain.repository

import com.example.habitflow.domain.entities.settings.AppSettings
import com.example.habitflow.domain.entities.settings.NotificationTime
import kotlinx.coroutines.flow.Flow


interface SettingsRepository {

    fun getSettings() : Flow<AppSettings>

    suspend fun updateWifiOnly(wifiOnly: Boolean)

    suspend fun updateSendNotificationBeforeDeadline(beforeMinutes: Int)

    suspend fun updateNotificationsEnabled(enabled: Boolean)

    suspend fun updateShowCompletedTasksOnMainScreen(shouldShow: Boolean)

    suspend fun updateMorningTimeInfo(notificationTime: NotificationTime)

    suspend fun updateNightTimeInfo(notificationTime: NotificationTime)
}