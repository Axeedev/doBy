package com.example.habitflow.data.repository

import android.content.Context
import android.content.res.Configuration
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import com.example.habitflow.data.dataStore
import com.example.habitflow.data.local.AppDatabase
import com.example.habitflow.domain.entities.settings.AppSettings
import com.example.habitflow.domain.entities.settings.NotificationTime
import com.example.habitflow.domain.entities.settings.toSendBefore
import com.example.habitflow.domain.repository.SettingsRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject


class SettingsRepositoryImpl @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val database: AppDatabase
) : SettingsRepository {

    private val wifiOnlyKey = booleanPreferencesKey("wifi_only")
    private val notificationBeforeDeadlineKey = intPreferencesKey("notify_before")
    private val notificationsEnabledKey = booleanPreferencesKey("notifications")
    private val morningNotificationMinuteKey = intPreferencesKey("morning_minute")
    private val morningNotificationHourKey = intPreferencesKey("morning_hour")
    private val nightNotificationMinuteKey = intPreferencesKey("night_minute")
    private val nightNotificationHourKey = intPreferencesKey("night_hour")
    private val showCalendarEventsKey = booleanPreferencesKey("show_calendar_events")
    private val isDarkThemeKey = booleanPreferencesKey("dark_theme")

    override fun getSettings(): Flow<AppSettings> {
        return context.dataStore.data.map { preferences ->
            val wifiOnly = preferences[wifiOnlyKey] ?: AppSettings.WIFI_ONLY_DEFAULT
            val notificationBeforeDeadlineAsInt = preferences[notificationBeforeDeadlineKey]
            val notificationBeforeDeadline = notificationBeforeDeadlineAsInt?.toSendBefore() ?: AppSettings.sendNotificationBeforeDeadlineDefault
            val notificationsEnabled = preferences[notificationsEnabledKey] ?: AppSettings.NOTIFICATIONS_ENABLED_DEFAULT
            val morningNotificationTimeMinute = preferences[morningNotificationMinuteKey] ?: AppSettings.morningInfoTimeDefault.minute
            val morningNotificationTimeHour = preferences[morningNotificationHourKey] ?: AppSettings.morningInfoTimeDefault.hour
            val morningNotificationTime = NotificationTime(morningNotificationTimeHour, morningNotificationTimeMinute)
            val showCalendarEvents = preferences[showCalendarEventsKey] ?: AppSettings.SHOW_CALENDAR_EVENTS_DEFAULT

            val isDarkTheme = preferences[isDarkThemeKey] ?: isDarkTheme(context)
            val nightNotificationTimeHour = preferences[nightNotificationHourKey] ?: AppSettings.nightInfoTimeDefault.hour
            val nightNotificationTimeMinute = preferences[nightNotificationMinuteKey] ?: AppSettings.nightInfoTimeDefault.minute
            val nightNotificationTime = NotificationTime(nightNotificationTimeHour, nightNotificationTimeMinute)

            AppSettings(
                notificationsEnabled = notificationsEnabled,
                wifiOnly = wifiOnly,
                sendNotificationBeforeDeadline = notificationBeforeDeadline,
                morningInfoTime = morningNotificationTime,
                nightInfoTime = nightNotificationTime,
                showCalendarEvents = showCalendarEvents,
                isDarkTheme = isDarkTheme
            )
        }
    }

    override suspend fun updateShowEventsFromCalendar(show: Boolean) {
        context.dataStore.edit {preferences ->
            preferences[showCalendarEventsKey] = show
        }
    }

    override suspend fun updateIsDarkTheme(isDarkTheme: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[isDarkThemeKey] = isDarkTheme
        }
    }

    override suspend fun updateWifiOnly(wifiOnly: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[wifiOnlyKey] = wifiOnly
        }
    }

    override suspend fun updateSendNotificationBeforeDeadline(beforeMinutes: Int) {
        context.dataStore.edit { preferences ->
            preferences[notificationBeforeDeadlineKey] = beforeMinutes
        }
    }

    override suspend fun updateNotificationsEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[notificationsEnabledKey] = enabled
        }
    }

    override suspend fun updateMorningTimeInfo(notificationTime: NotificationTime) {
        context.dataStore.edit {preferences ->
            preferences[morningNotificationMinuteKey] = notificationTime.minute
            preferences[morningNotificationHourKey] = notificationTime.hour
        }
    }

    override suspend fun updateNightTimeInfo(notificationTime: NotificationTime) {
        context.dataStore.edit { preferences ->
            preferences[nightNotificationHourKey] = notificationTime.hour
            preferences[nightNotificationMinuteKey] = notificationTime.hour
        }
    }

    override fun getNetworkType() : Flow<Boolean>{
        return context.dataStore.data.map {
            it[wifiOnlyKey] ?: AppSettings.WIFI_ONLY_DEFAULT
        }
    }

    override fun getIsDarkTheme(): Flow<Boolean> {
        return context.dataStore.data.map {
            it[isDarkThemeKey] ?: isDarkTheme(context)
        }
    }

    override fun clearAllTables() {
        database.clearAllTables()
    }

    private fun isDarkTheme(context: Context): Boolean {
        return when (context.resources.configuration.uiMode and
                Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_YES -> true
            Configuration.UI_MODE_NIGHT_NO -> false
            else -> false
        }
    }
}