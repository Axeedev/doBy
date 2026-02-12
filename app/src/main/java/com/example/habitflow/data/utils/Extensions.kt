package com.example.habitflow.data.utils

import com.example.habitflow.data.local.NotificationsProvider
import com.example.habitflow.domain.entities.tasks.Priority

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