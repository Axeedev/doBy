package com.example.habitflow.data.calendar

import android.Manifest
import android.content.ContentResolver
import android.content.Context
import android.content.pm.PackageManager
import android.provider.CalendarContract
import androidx.core.content.ContextCompat
import com.example.habitflow.domain.entities.tasks.Task
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class SystemCalendarManager @Inject constructor(
    @param:ApplicationContext private val context: Context,
) {

    fun getEventsAsTasks(): Flow<List<Task>> = flow {
        emit(fetchUpcomingEvents().map { it.toTask() })

    }

    private fun fetchUpcomingEvents(): List<CalendarEvent> {
        return if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.READ_CALENDAR
            ) == PackageManager.PERMISSION_GRANTED
        ) {


            val events = mutableListOf<CalendarEvent>()
            val contentResolver: ContentResolver = context.contentResolver

            val projection = arrayOf(
                CalendarContract.Events._ID,
                CalendarContract.Events.TITLE,
                CalendarContract.Events.DESCRIPTION,
                CalendarContract.Events.DTSTART,
                CalendarContract.Events.DTEND
            )

            val startMillis = System.currentTimeMillis()
            val endMillis = startMillis + (7 * 24 * 60 * 60 * 1000L)

            val selection =
                "${CalendarContract.Events.DTSTART} >= ? AND ${CalendarContract.Events.DTSTART} <= ?"
            val selectionArgs = arrayOf(
                startMillis.toString(),
                endMillis.toString()
            )

            val sortOrder = "${CalendarContract.Events.DTSTART} ASC"

            val cursor = contentResolver.query(
                CalendarContract.Events.CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                sortOrder
            )

            cursor?.use {
                val idCol = it.getColumnIndexOrThrow(CalendarContract.Events._ID)
                val titleCol = it.getColumnIndexOrThrow(CalendarContract.Events.TITLE)
                val descCol = it.getColumnIndexOrThrow(CalendarContract.Events.DESCRIPTION)
                val startCol = it.getColumnIndexOrThrow(CalendarContract.Events.DTSTART)
                val endCol = it.getColumnIndexOrThrow(CalendarContract.Events.DTEND)


                while (it.moveToNext()) {
                    events.add(
                        CalendarEvent(
                            id = it.getInt(idCol),
                            title = it.getString(titleCol) ?: "No title",
                            description = it.getString(descCol) ?: "No description",
                            startMillis = it.getLong(startCol) + 1000L * 6 * 60 * 60,
                            endMillis = it.getLong(endCol)
                        )
                    )
                }
            }
            events
        } else listOf()
    }
}