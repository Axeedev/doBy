package com.example.habitflow.domain.repository

import com.example.habitflow.domain.entities.AppSettings
import kotlinx.coroutines.flow.Flow


interface SettingsRepository {

    fun getSettings() : Flow<AppSettings>

    suspend fun updateWifiOnly(wifiOnly: Boolean)

    suspend fun updateSendNotificationBeforeDeadline(beforeMinutes: Int)

    suspend fun updateNotificationsEnabled(enabled: Boolean)
}