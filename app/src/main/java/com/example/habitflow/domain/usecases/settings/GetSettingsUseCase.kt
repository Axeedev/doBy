package com.example.habitflow.domain.usecases.settings

import com.example.habitflow.domain.repository.SettingsRepository
import javax.inject.Inject

class GetSettingsUseCase @Inject constructor(
    private val repository: SettingsRepository
) {
    operator fun invoke() = repository.getSettings()
}