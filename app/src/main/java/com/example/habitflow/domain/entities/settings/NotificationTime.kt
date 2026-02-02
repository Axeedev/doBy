package com.example.habitflow.domain.entities.settings

data class NotificationTime(
    val hour: Int,
    val minute: Int
) : MorningNotificationState()

data object Disabled : MorningNotificationState()

sealed class MorningNotificationState