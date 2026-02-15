package com.example.habitflow.domain.usecases.achievements

import com.example.habitflow.domain.repository.AchievementRepository
import javax.inject.Inject

class GetCurrentStreakUseCase @Inject constructor(
    private val repository: AchievementRepository
) {
    operator fun invoke() = repository.getCurrentStreak()
}