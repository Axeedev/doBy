package com.example.habitflow.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.habitflow.data.background.RandomAdviceWorker
import com.example.habitflow.domain.entities.AppSettings
import com.example.habitflow.domain.entities.toSendBefore
import com.example.habitflow.domain.repository.SettingsRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.concurrent.TimeUnit
import javax.inject.Inject

val Context.dataStore: DataStore<Preferences> by preferencesDataStore("App settings")

class SettingsRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : SettingsRepository {

    private val wifiOnlyKey = booleanPreferencesKey("wifi_only")
    private val notificationBeforeDeadlineKey = intPreferencesKey("notify_before")
    private val notificationsEnabledKey = booleanPreferencesKey("notifications")

    override fun getSettings(): Flow<AppSettings> {
        return context.dataStore.data.map { preferences ->
            val wifiOnly = preferences[wifiOnlyKey] ?: false
            val notificationBeforeDeadlineAsInt = preferences[notificationBeforeDeadlineKey] ?: 60
            val notificationBeforeDeadline = notificationBeforeDeadlineAsInt.toSendBefore()
            val notificationsEnabled = preferences[notificationsEnabledKey] ?: false
            AppSettings(notificationsEnabled, wifiOnly, notificationBeforeDeadline)
        }
    }

    override suspend fun updateWifiOnly(wifiOnly: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[wifiOnlyKey] = wifiOnly
        }

        val networkType = if (wifiOnly) NetworkType.UNMETERED else NetworkType.CONNECTED

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(networkType)
            .build()

        val request = PeriodicWorkRequestBuilder<RandomAdviceWorker>(
            1,
            TimeUnit.DAYS
        ).setConstraints(constraints)
            .build()

        WorkManager.getInstance(context)
            .enqueueUniquePeriodicWork(
                RandomAdviceWorker.ADVICE_WORKER_NAME,
                existingPeriodicWorkPolicy = ExistingPeriodicWorkPolicy.UPDATE,
                request
            )
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
}