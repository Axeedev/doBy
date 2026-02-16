package com.example.habitflow.data.repository

import com.example.habitflow.data.local.tasks.CompletedTasksDao
import com.example.habitflow.domain.repository.AnalyticsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AnalyticsRepositoryImpl @Inject constructor(
    private val completedTasksDao: CompletedTasksDao
) : AnalyticsRepository {

    override fun getDailyStats(): Flow<Map<Long, Int>> {
        return completedTasksDao.getDailyStatsMap()
    }
}