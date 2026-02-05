package com.example.habitflow.domain.usecases.settings

import com.example.habitflow.domain.entities.settings.NotificationTime
import com.example.habitflow.domain.repository.SettingsRepository
import javax.inject.Inject

class UpdateMorningTimeInfoUseCase @Inject constructor(
    private val repository: SettingsRepository
) {

    suspend operator fun invoke(notificationTime: NotificationTime) = repository.updateMorningTimeInfo(notificationTime)
}