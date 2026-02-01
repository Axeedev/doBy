package com.example.habitflow.presentation.screens.settings

import com.example.habitflow.domain.entities.AppSettings
import com.example.habitflow.domain.entities.SendNotificationBeforeDeadline
import com.example.habitflow.domain.entities.toSendBefore


data class SettingsScreenState(
    val wifiOnly: Boolean = AppSettings.WIFI_ONLY_DEFAULT,
    val notificationsEnabled: Boolean = AppSettings.NOTIFICATIONS_ENABLED_DEFAULT,
    val notifyBeforeMinutes: Int = AppSettings.sendNotificationBeforeDeadline.beforeMinutes,
    val selectedIndex: Int = SendNotificationBeforeDeadline.entries.indexOf(notifyBeforeMinutes.toSendBefore())
){

}