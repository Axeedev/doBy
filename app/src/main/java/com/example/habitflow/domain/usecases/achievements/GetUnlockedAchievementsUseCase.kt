package com.example.habitflow.domain.usecases.achievements

import com.example.habitflow.domain.repository.AchievementRepository
import javax.inject.Inject

class GetUnlockedAchievementsUseCase @Inject constructor(
    private val repository: AchievementRepository
) {
    operator fun invoke() = repository.getUnlockedAchievements()
}