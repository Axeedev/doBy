package com.example.habitflow.data.utils

import com.example.habitflow.data.local.NotificationsProvider
import com.example.habitflow.domain.entities.tasks.Priority
import java.time.LocalDateTime
import java.time.ZoneId

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