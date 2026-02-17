package com.example.habitflow.domain.usecases.settings

import com.example.habitflow.domain.repository.SettingsRepository
import javax.inject.Inject

class UpdateShowCalendarEventsUseCase @Inject constructor(
    private val repository: SettingsRepository
) {
    suspend operator fun invoke(show: Boolean) = repository.updateShowEventsFromCalendar(show)
}