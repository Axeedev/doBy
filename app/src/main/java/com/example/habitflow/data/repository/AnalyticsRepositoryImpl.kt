package com.example.habitflow.data.repository

import com.example.habitflow.data.local.tasks.CompletedTasksDao
import com.example.habitflow.domain.repository.AnalyticsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import javax.inject.Inject

class AnalyticsRepositoryImpl @Inject constructor(
    private val completedTasksDao: CompletedTasksDao
) : AnalyticsRepository {

    override fun getDailyStats(): Flow<Map<Long, Int>> {
        return completedTasksDao.getDailyStatsMap()
    }

    override fun getWeeklyDifferencePercentage(): Flow<Int> {
        val endTime = LocalDate.now()

        val aWeekAgo = endTime.minusWeeks(1)
        val twoWeeksAgo = endTime.minusWeeks(2)

        val endTimeMillis = getMillisByLocalDate(endTime)
        val aWeekAgoMillis = getMillisByLocalDate(aWeekAgo)

        val twoWeeksAgoMillis = getMillisByLocalDate(twoWeeksAgo)
        val previousWeekCount = completedTasksDao.getCountOfTasksForPeriodFlow(
            start = twoWeeksAgoMillis,
            end = aWeekAgoMillis
        )

        val thisWeekCount = completedTasksDao.getCountOfTasksForPeriodFlow(
            start = aWeekAgoMillis,
            end = endTimeMillis
        )
        return combine(
            flow = previousWeekCount,
            flow2 = thisWeekCount
        ){previousWeek , thisWeek ->

            val isDecrease = if (thisWeek < previousWeek) -1 else 1
            if (previousWeek == 0) {
                100 * thisWeek
            }else {
                ((thisWeek.toFloat() / previousWeek.toFloat()) * 100).toInt() * isDecrease
            }
        }

    }

    override fun getCountOfCompletedTasksForWeek(): Flow<Int> {
        val endTime = LocalDate.now()

        val startTime = endTime.minusWeeks(1)

        val endTimeMillis = getMillisByLocalDate(endTime)

        val startTimeMillis = getMillisByLocalDate(startTime)

        return completedTasksDao.getCountOfTasksForPeriodFlow(startTimeMillis, endTimeMillis)
    }

    private fun getMillisByLocalDate(date: LocalDate): Long{
        return date.atTime(LocalTime.MAX)
            .atZone(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()
    }
}