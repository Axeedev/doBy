package com.example.habitflow.domain.entities.settings

data class AppSettings(
    val notificationsEnabled: Boolean = false,
    val wifiOnly: Boolean = false,
    val sendNotificationBeforeDeadline: SendNotificationBeforeDeadline = SendNotificationBeforeDeadline.MINUTES_60,
    val morningInfoTime: MorningNotificationState = morningInfoTimeDefault,
    val nightInfoTime: NotificationTime = nightInfoTimeDefault,
    val showCompletedTasksOnMainScreen: Boolean = SHOW_COMPLETED_TASKS_DEFAULT
){
    companion object{

        const val NOTIFICATIONS_ENABLED_DEFAULT = false
        const val WIFI_ONLY_DEFAULT = false
        const val SHOW_COMPLETED_TASKS_DEFAULT = false
        val sendNotificationBeforeDeadlineDefault = SendNotificationBeforeDeadline.MINUTES_60
        val morningInfoTimeDefault = NotificationTime(8, 0)
        val nightInfoTimeDefault = NotificationTime(22, 0)

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