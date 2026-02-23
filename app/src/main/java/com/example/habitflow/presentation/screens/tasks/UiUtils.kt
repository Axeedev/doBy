@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.habitflow.presentation.screens.tasks

import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDefaults
import androidx.compose.material3.TimePickerDialog
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.example.habitflow.R
import java.util.Calendar

@Composable
fun DatePickerModal(
    onDateSelected: (Long?) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState()

    DatePickerDialog(
        colors = DatePickerDefaults.colors(
            selectedDayContainerColor = MaterialTheme.colorScheme.onPrimary,
            selectedDayContentColor = MaterialTheme.colorScheme.onPrimary,
            todayContentColor = MaterialTheme.colorScheme.onPrimary,
        ),
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                onDateSelected(datePickerState.selectedDateMillis)
                onDismiss()
            }) {
                Text(
                    text = stringResource(R.string.date_picker_ok),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    stringResource(R.string.date_picker_cancel),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerDial(
    onConfirm: (TimeEntity) -> Unit,
    onDismiss: () -> Unit,
) {
    val currentTime = Calendar.getInstance()

    val timePickerState = rememberTimePickerState(
        initialHour = currentTime.get(Calendar.HOUR_OF_DAY),
        initialMinute = currentTime.get(Calendar.MINUTE),
        is24Hour = true,
    )

    TimePickerDialog(
        onDismissRequest = {
            onDismiss()
        },
        confirmButton = {
            Button(
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.scrim
                ),
                onClick = {
                    onConfirm(
                        TimeEntity(
                            timePickerState.hour, timePickerState.minute
                        )
                    )
                    onDismiss()
                }
            ) {
                Text(
                    text = stringResource(R.string.time_picker_confirm_time),
                    color = MaterialTheme.colorScheme.outline
                )
            }
        },
        title = {
            Text(
                text = stringResource(R.string.time_picker_title_choose_time),
                color = MaterialTheme.colorScheme.onPrimary
            )
        },
    ) {
        TimePicker(
            state = timePickerState,
            colors = TimePickerDefaults.colors(
                containerColor = MaterialTheme.colorScheme.inverseSurface,
                periodSelectorSelectedContainerColor = MaterialTheme.colorScheme.scrim,
                timeSelectorSelectedContentColor = MaterialTheme.colorScheme.onPrimary,
                timeSelectorUnselectedContentColor = MaterialTheme.colorScheme.onPrimary,
                timeSelectorUnselectedContainerColor = Color.Transparent
            )
        )
    }
}