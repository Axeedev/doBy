package com.example.habitflow.domain.usecases.settings

import com.example.habitflow.domain.repository.SettingsRepository
import javax.inject.Inject

class UpdateNotificationsEnabledUseCase @Inject constructor(
    private val repository: SettingsRepository
) {
    suspend operator fun invoke(notificationsEnabled: Boolean) = repository.updateNotificationsEnabled(notificationsEnabled)
}