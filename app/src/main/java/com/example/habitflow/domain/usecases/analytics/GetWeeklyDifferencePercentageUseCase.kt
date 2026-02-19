package com.example.habitflow.domain.usecases.analytics

import com.example.habitflow.domain.repository.AnalyticsRepository
import javax.inject.Inject

class GetWeeklyDifferencePercentageUseCase @Inject constructor(
    private val repository: AnalyticsRepository
) {
    operator fun invoke() = repository.getWeeklyDifferencePercentage()
}