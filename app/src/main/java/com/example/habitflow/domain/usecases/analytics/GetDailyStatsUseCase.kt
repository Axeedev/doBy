package com.example.habitflow.domain.usecases.analytics

import com.example.habitflow.domain.repository.AnalyticsRepository
import javax.inject.Inject

class GetDailyStatsUseCase @Inject constructor(
    private val analyticsRepository: AnalyticsRepository
) {
    operator fun invoke() = analyticsRepository.getDailyStats()
}