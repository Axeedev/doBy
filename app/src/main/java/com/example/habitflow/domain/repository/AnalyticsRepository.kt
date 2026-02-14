package com.example.habitflow.domain.repository

import com.example.habitflow.domain.entities.DailyStat
import kotlinx.coroutines.flow.Flow

interface AnalyticsRepository {

    fun getDailyStats(): Flow<Map<Long, Int>>
}