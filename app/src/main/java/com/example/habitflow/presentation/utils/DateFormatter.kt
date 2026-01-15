package com.example.habitflow.presentation.utils

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

object DateFormatter {
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
}