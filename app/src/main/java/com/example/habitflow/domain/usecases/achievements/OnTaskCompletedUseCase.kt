package com.example.habitflow.domain.usecases.achievements

import com.example.habitflow.domain.repository.AchievementRepository
import javax.inject.Inject

class OnTaskCompletedUseCase @Inject constructor(
    private val repository: AchievementRepository
) {
    suspend operator fun invoke() = repository.onTaskCompleted()
}