package com.example.habitflow.presentation.screens.settings

sealed interface SettingsCommand {


    data class ChangeWifiOnly(val isWifiOnly: Boolean): SettingsCommand

    data class ChangeNotificationsEnabled(val enabled: Boolean): SettingsCommand

    data class ChangeNotifyBefore(val newBefore : Int) : SettingsCommand

    data class ClickNotifyItem(val index: Int) : SettingsCommand

    data class ChangeShowCompletedTasks(val shouldShow: Boolean) : SettingsCommand
}