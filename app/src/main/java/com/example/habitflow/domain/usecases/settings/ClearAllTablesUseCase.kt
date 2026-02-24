package com.example.habitflow.domain.usecases.settings

import com.example.habitflow.domain.repository.SettingsRepository
import javax.inject.Inject

class ClearAllTablesUseCase @Inject constructor(
    private val repository: SettingsRepository
) {
    operator fun invoke() = repository.clearAllTables()
}