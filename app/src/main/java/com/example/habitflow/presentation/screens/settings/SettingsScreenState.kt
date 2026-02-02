package com.example.habitflow.presentation.screens.settings

import com.example.habitflow.domain.entities.settings.AppSettings
import com.example.habitflow.domain.entities.settings.SendNotificationBeforeDeadline
import com.example.habitflow.domain.entities.settings.toSendBefore


data class SettingsScreenState(
    val wifiOnly: Boolean = AppSettings.WIFI_ONLY_DEFAULT,
    val notificationsEnabled: Boolean = AppSettings.NOTIFICATIONS_ENABLED_DEFAULT,
    val notifyBeforeMinutes: Int = AppSettings.sendNotificationBeforeDeadlineDefault.beforeMinutes,
    val showCompletedTasks: Boolean = AppSettings.SHOW_COMPLETED_TASKS_DEFAULT,
    val selectedIndex: Int = SendNotificationBeforeDeadline.entries.indexOf(notifyBeforeMinutes.toSendBefore())
){

}