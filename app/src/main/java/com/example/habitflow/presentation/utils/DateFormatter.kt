package com.example.habitflow.presentation.utils

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale

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

    fun getLocalTime() : String{
        return String.format(
            Locale.getDefault(),
            "%02d:%02d",
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE)
        )
    }
}