package com.example.habitflow.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import javax.inject.Inject

class StreakManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val streakKey = intPreferencesKey("current_streak")
    private val lastDateKey = stringPreferencesKey("last_date")

    fun getCurrentStreak() : Flow<Int> {
        return context.dataStore.data.map {
            it[streakKey] ?: 0
        }
    }

    suspend fun updateStreak() {
        val today = LocalDate.now().toString()

        context.dataStore.edit { prefs ->
            val lastDateStr = prefs[lastDateKey]
            val currentStreak = prefs[streakKey] ?: 0

            if (lastDateStr == today) {
                return@edit
            }

            val lastDate = lastDateStr?.let { LocalDate.parse(it) }

            val newStreak = if (lastDate != null && lastDate.plusDays(1).toString() == today) {
                currentStreak + 1
            } else {
                1
            }

            prefs[streakKey] = newStreak
            prefs[lastDateKey] = today
        }
    }

    suspend fun checkStreakExpiration() {
        val today = LocalDate.now()
        context.dataStore.edit { prefs ->
            val lastDateStr = prefs[lastDateKey] ?: return@edit
            val lastDate = LocalDate.parse(lastDateStr)

            if (ChronoUnit.DAYS.between(lastDate, today) > 1) {
                prefs[streakKey] = 0
            }
        }
    }

}