package com.example.habitflow.domain.entities.settings

data class AppSettings(
    val notificationsEnabled: Boolean = false,
    val wifiOnly: Boolean = false,
    val sendNotificationBeforeDeadline: SendNotificationBeforeDeadline = SendNotificationBeforeDeadline.MINUTES_60,
    val morningInfoTime: NotificationTime = morningInfoTimeDefault,
    val nightInfoTime: NotificationTime = nightInfoTimeDefault,
    val showCompletedTasksOnMainScreen: Boolean = SHOW_COMPLETED_TASKS_DEFAULT,
    val showCalendarEvents: Boolean = SHOW_CALENDAR_EVENTS_DEFAULT,
    val isDarkTheme: Boolean = IS_DARK_THEME_DEFAULT
){
    companion object{

        const val NOTIFICATIONS_ENABLED_DEFAULT = false
        const val WIFI_ONLY_DEFAULT = false
        const val IS_DARK_THEME_DEFAULT = false
        const val SHOW_COMPLETED_TASKS_DEFAULT = false
        const val SHOW_CALENDAR_EVENTS_DEFAULT = false
        val nightInfoTimeDefault = NotificationTime(22, 0)
        val sendNotificationBeforeDeadlineDefault = SendNotificationBeforeDeadline.MINUTES_60
        val morningInfoTimeDefault = NotificationTime(8, 0)

        val morningInfoTimeItems = listOf(
            NotificationTime(6, 0),
            NotificationTime(7, 0),
            NotificationTime(8, 0),
            NotificationTime(9, 0),
            NotificationTime(10, 0),
            NotificationTime(11, 0),
            NotificationTime(12, 0)
        )
        val nightInfoTimeItems = listOf(
            NotificationTime(18, 0),
            NotificationTime(19, 0),
            NotificationTime(20, 0),
            NotificationTime(21, 0),
            NotificationTime(22, 0),
            NotificationTime(23, 0),
        )
    }
}


enum class SendNotificationBeforeDeadline(val beforeMinutes: Int){

    MINUTES_30(30), MINUTES_15(15),
    MINUTES_45(45), MINUTES_60(60),
    MINUTES_90(90), MINUTES_120(120)

}

fun Int.toSendBefore() : SendNotificationBeforeDeadline{
    return SendNotificationBeforeDeadline.entries.first { it.beforeMinutes == this }
}