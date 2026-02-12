package com.example.habitflow.presentation.utils

import android.util.Log
import com.example.habitflow.domain.entities.tasks.Task
import com.example.habitflow.presentation.screens.tasks.TaskDeadlineSection
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

fun List<Task>.groupBySection(
    zoneId: ZoneId
): Map<TaskDeadlineSection, List<Task>>{
    val today = LocalDate.now(zoneId)
    Log.d("groupBySection", today.toString())
    Log.d("List group by", joinToString(", "))
    return groupBy { it.section(today, zoneId) }
}


fun Task.section(
    today: LocalDate,
    zoneId: ZoneId
): TaskDeadlineSection {
    val deadlineDate = deadlineMillis
        ?.let { Instant.ofEpochMilli(it).atZone(zoneId).toLocalDate() }
        ?: return TaskDeadlineSection.LATER

    return when {
        deadlineDate.isEqual(today) -> TaskDeadlineSection.TODAY
        deadlineDate.isEqual(today.plusDays(1)) -> TaskDeadlineSection.TOMORROW
        deadlineDate.isBefore(today.plusWeeks(1)) -> TaskDeadlineSection.NEXT_WEEK
        else -> TaskDeadlineSection.LATER
    }
}
