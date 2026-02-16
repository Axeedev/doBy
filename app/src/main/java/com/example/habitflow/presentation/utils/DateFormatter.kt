package com.example.habitflow.presentation.utils

import androidx.collection.mutableIntIntMapOf
import com.example.habitflow.presentation.screens.tasks.TimeEntity
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale
import kotlin.math.min

object DateFormatter {

    private val calendar = Calendar.getInstance()
    fun formatDate(millis: Long): String {
        val formatter = DateTimeFormatter.ofPattern(
            "MMM dd, yyyy",
            Locale.ENGLISH
        )
        return Instant.ofEpochMilli(millis)
            .atZone(ZoneId.systemDefault())
            .toLocalDate()
            .format(formatter)
    }

    fun getLocalDate() : String{
        return formatDate(
            System.currentTimeMillis()
        )
    }

    fun getCurrentTimeMinutes(): Int{
        return calendar.get(Calendar.HOUR_OF_DAY) * 60 + Calendar.MINUTE
    }

    fun TimeEntity.formatTime() : String{
        return String.format(
            Locale.getDefault(),
            "%02d:%02d",
            hours,
            minutes
        )
    }

    fun getLocalTime() : String{
        return String.format(
            Locale.getDefault(),
            "%02d:%02d",
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE)
        )
    }
}