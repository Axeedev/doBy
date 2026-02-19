package com.example.habitflow.domain.repository

import kotlinx.coroutines.flow.Flow

interface AnalyticsRepository {

    fun getDailyStats(): Flow<Map<Long, Int>>

    fun getWeeklyDifferencePercentage() : Flow<Int>

    fun getCountOfCompletedTasksForWeek(): Flow<Int>
}