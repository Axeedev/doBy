package com.example.habitflow.data.utils

import com.example.habitflow.data.local.NotificationsProvider
import com.example.habitflow.domain.entities.tasks.Priority
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

fun Priority.toChannelImportance() : String{
    return when(this){
        Priority.LOW -> {
            NotificationsProvider.TASK_LOW
        }
        Priority.MIDDLE -> {
            NotificationsProvider.TASK_MED
        }
        Priority.HIGH -> {
            NotificationsProvider.TASK_HIGH
        }
    }
}


fun LocalDateTime.toLong() : Long{
    return atZone(ZoneId.systemDefault())
        .toInstant()
        .toEpochMilli()
}

fun parseToMillis(dateString: String): Long {
    val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")

    val localDateTime = LocalDateTime.parse(dateString, formatter)

    return localDateTime
        .atZone(ZoneId.systemDefault())
        .toInstant()
        .toEpochMilli()
}