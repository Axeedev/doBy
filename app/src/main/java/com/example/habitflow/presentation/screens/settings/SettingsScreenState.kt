package com.example.habitflow.presentation.screens.settings

import com.example.habitflow.domain.entities.settings.AppSettings
import com.example.habitflow.domain.entities.settings.SendNotificationBeforeDeadline
import com.example.habitflow.domain.entities.settings.toSendBefore
import java.util.Locale


data class SettingsScreenState(
    val wifiOnly: Boolean = AppSettings.WIFI_ONLY_DEFAULT,
    val notificationsEnabled: Boolean = AppSettings.NOTIFICATIONS_ENABLED_DEFAULT,
    val notifyBeforeMinutes: Int = AppSettings.sendNotificationBeforeDeadlineDefault.beforeMinutes,
    val showCompletedTasks: Boolean = AppSettings.SHOW_COMPLETED_TASKS_DEFAULT,
    val selectedNotifyBeforeIndex: Int = SendNotificationBeforeDeadline.entries.indexOf(notifyBeforeMinutes.toSendBefore()),
    val selectedMorningTimeIndex: Int = AppSettings.morningInfoTimeItems.indexOf(AppSettings.morningInfoTimeItems.first { time ->
        time.minute == AppSettings.morningInfoTimeDefault.minute && time.hour == AppSettings.morningInfoTimeDefault.hour
    }),
    val selectedNightTimeIndex: Int = AppSettings.nightInfoTimeItems.indexOf(
        AppSettings.nightInfoTimeItems.first {time ->
            time.hour == AppSettings.nightInfoTimeDefault.hour && time.minute == AppSettings.nightInfoTimeDefault.minute
        }
    ),
    val bottomSheetType: BottomSheetType? = null
){
    val morningTimeFormatted = String.format(
        Locale.getDefault(),
        "%02d:%02d",
        AppSettings.morningInfoTimeItems[selectedMorningTimeIndex].hour,
        AppSettings.morningInfoTimeItems[selectedMorningTimeIndex].minute,
        )
    val nightTimeFormatted = String.format(
        Locale.getDefault(),
        "%02d:%02d",
        AppSettings.nightInfoTimeItems[selectedNightTimeIndex].hour,
        AppSettings.nightInfoTimeItems[selectedNightTimeIndex].minute,
        )
}
sealed interface BottomSheetType {

    data object NotifyBefore: BottomSheetType

    data object NightTimeNotification : BottomSheetType

    data object MorningTimeNotification : BottomSheetType

}