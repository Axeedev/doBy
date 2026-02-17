package com.example.habitflow.presentation.screens.settings

import com.example.habitflow.domain.entities.settings.NotificationTime

sealed interface SettingsCommand {


    data class ChangeWifiOnly(val isWifiOnly: Boolean): SettingsCommand

    data class ChangeNotificationsEnabled(val enabled: Boolean): SettingsCommand

    data class ChangeShowCalendarEvents(val enabled: Boolean) : SettingsCommand

    data class ChangeNotifyBefore(val newBefore : Int) : SettingsCommand

    data class ClickNotifyItem(val index: Int) : SettingsCommand

    data class ClickMorningTimeItem(val index: Int) : SettingsCommand

    data class ClickNightTimeItem(val index: Int) : SettingsCommand

    data class ChangeShowCompletedTasks(val shouldShow: Boolean) : SettingsCommand

    data class ChangeNightTimeInfo(val notificationTime: NotificationTime) : SettingsCommand

    data class ChangeMorningTimeInfo(val notificationTime: NotificationTime) : SettingsCommand

    data class OpenSheet(val sheetType: BottomSheetType) : SettingsCommand

    data object CloseSheet : SettingsCommand

    data object SignOut : SettingsCommand
}