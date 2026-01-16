package com.example.habitflow.presentation.screens.tasks.creation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDialog
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.habitflow.R
import com.example.habitflow.domain.entities.Priority
import com.example.habitflow.domain.entities.TaskCategory
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateTaskScreen(
    viewModel: CreateTaskViewModel = hiltViewModel(),
    onBackClick: () -> Unit
) {

    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = MaterialTheme.colorScheme.scrim,
                    navigationIconContentColor = MaterialTheme.colorScheme.scrim
                ),
                navigationIcon = {
                    Icon(
                        modifier = Modifier
                            .padding(start = 16.dp)
                            .clip(CircleShape)
                            .clickable {
                                onBackClick()
                            },
                        painter = painterResource(R.drawable.ic_arrow_back),
                        contentDescription = "Go back"
                    )
                },
                title = {
                    Text(
                        fontWeight = FontWeight.SemiBold,
                        text = "Create a Task"
                    )
                },
                actions = {
                    Icon(
                        modifier = Modifier
                            .clip(CircleShape)
                            .size(30.dp)
                            .clickable{
                                viewModel.processCommand(CreateTaskCommand.ChangePriority)
                            },
                        painter = painterResource(
                            when(state.priority){
                                Priority.REGULAR -> {
                                    R.drawable.ic_priority_low
                                }
                                Priority.MIDDLE -> {
                                    R.drawable.ic_priority_low
                                }
                                Priority.HIGH -> {
                                    R.drawable.ic_priority_high
                                }
                                Priority.EXTRA_HIGH -> {
                                    R.drawable.ic_priority_high
                                }
                            }
                        ),
                        tint = when(state.priority){
                            Priority.REGULAR ->
                                Color.White
                            Priority.MIDDLE ->
                                Color.Red
                            Priority.HIGH ->
                                Color.White
                            Priority.EXTRA_HIGH ->
                                Color.Red
                        },
                        contentDescription = "priority of the task"
                    )
                }
            )
        },
        containerColor = MaterialTheme.colorScheme.primary
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = paddingValues.calculateTopPadding())
                .drawBehind {
                    drawCircle(
                        color = Color(0xFF4A3BCF),
                        center = Offset(x = this.size.width, y = 0f),
                        radius = size.minDimension / 2.5f
                    )
                    drawCircle(
                        color = Color(0xFF4A3BCF),
                        center = Offset(x = 0f, y = this.size.height / 3)
                    )
                }
        ) {
            Text(
                modifier = Modifier
                    .padding(horizontal = 24.dp),
                fontSize = 16.sp,
                text = "Title"
            )
            CreateTaskTextField(
                modifier = Modifier
                    .padding(horizontal = 8.dp),
                value = state.title,
                supportingText = "Add title",
            ) {
                viewModel.processCommand(CreateTaskCommand.InputTitle(it))
            }
            HorizontalDivider(
                modifier = Modifier
                    .padding(horizontal = 24.dp, vertical = 8.dp)
                    .fillMaxWidth(),
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )

            Spacer(Modifier.size(8.dp))

            Text(
                modifier = Modifier
                    .padding(horizontal = 24.dp),
                fontSize = 16.sp,
                text = "Date"
            )
            var isDatepickerOpened by remember { mutableStateOf(false) }
            if (isDatepickerOpened) {
                DatePickerModal(
                    {
                        it?.let {
                            viewModel.processCommand(
                                CreateTaskCommand.InputDate(
                                    it
                                )
                            )
                        }
                        isDatepickerOpened = false
                    }) {
                    isDatepickerOpened = false
                }
            }

            CreateTaskTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
                    .clickable {
                        isDatepickerOpened = true
                    },
                value = state.date,
                readOnly = true,
                supportingText = "Choose date",
                enabled = false
            ) {
                viewModel.processCommand(CreateTaskCommand.InputDate(
                    it.toLong())
                )

            }
            HorizontalDivider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 8.dp),
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Spacer(Modifier.size(16.dp))

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer),
            ) {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 24.dp)
                    ) {
                        var isEndTimePickerEnabled by remember { mutableStateOf(false) }
                        if (isEndTimePickerEnabled){
                            TimePickerDial(
                                onConfirm = {
                                    viewModel.processCommand(
                                        CreateTaskCommand.InputStartTime(
                                            it
                                        )
                                    )
                                    isEndTimePickerEnabled = false

                                }
                            ) {
                                isEndTimePickerEnabled = false
                            }
                        }
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    isEndTimePickerEnabled = true
                                }
                        ) {
                            Text(
                                text = "Start Time",
                                fontSize = 20.sp,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                            Spacer(Modifier.size(12.dp))
                            Text(
                                text = state.startTime,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }
                        var isStartTimePickerEnabled by remember { mutableStateOf(false) }
                        if (isStartTimePickerEnabled){
                            TimePickerDial(
                                onConfirm = {
                                    viewModel.processCommand(
                                        CreateTaskCommand.InputEndTime(
                                            it
                                        )
                                    )
                                    isStartTimePickerEnabled = false
                                },
                                onDismiss = {
                                    isStartTimePickerEnabled = false
                                }
                            )
                        }
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    isStartTimePickerEnabled = true
                                }
                        ) {
                            Text(
                                text = "End Time",
                                fontSize = 20.sp,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                            Spacer(Modifier.size(12.dp))
                            Text(
                                text = state.endTime,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }
                    }
                }
                item {
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 24.dp))
                    Spacer(Modifier.size(16.dp))
                    Text(
                        modifier = Modifier.padding(horizontal = 24.dp),
                        text = "Description",
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                    TextField(
                        modifier = Modifier.padding(horizontal = 8.dp),
                        value = state.description,
                        colors = TextFieldDefaults
                            .colors(
                                unfocusedContainerColor = Color.Transparent,
                                focusedContainerColor = Color.Transparent,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent
                            ),
                        textStyle = TextStyle(
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.secondary
                        ),
                        placeholder = {
                            Text(
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.secondary,
                                text = "Add a description"
                            )
                        },
                        onValueChange = {
                            viewModel.processCommand(
                                CreateTaskCommand.InputDescription(
                                    it
                                )
                            )
                        }
                    )
                    HorizontalDivider(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp)
                    )
                    Text(
                        text = "Category"
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        TaskCategory.entries.forEach { category ->
                            FilterChip(
                                modifier = Modifier
                                    .heightIn(min = 64.dp),
                                shape = RoundedCornerShape(50),
                                border = null,
                                selected = state.taskCategory == category,
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                                    containerColor = MaterialTheme.colorScheme.onPrimaryFixed,
                                    selectedLabelColor = MaterialTheme.colorScheme.primaryContainer,
                                    labelColor = MaterialTheme.colorScheme.secondary,

                                    ),
                                label = {
                                    Text(
                                        modifier = Modifier.padding(horizontal = 16.dp),
                                        text = category.title
                                    )
                                },
                                onClick = {
                                    viewModel.processCommand(
                                        CreateTaskCommand.ChangeCategory(
                                            category
                                        )
                                    )
                                }
                            )
                        }
                    }
                    Button(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 16.dp)
                            .heightIn(min = 64.dp),
                        enabled = state.isButtonEnabled,
                        onClick = {
                            viewModel.processCommand(CreateTaskCommand.AddTask)
                            onBackClick()
                        }
                    ) {
                        Text(
                            text = "Create Task",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 18.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CreateTaskTextField(
    modifier: Modifier = Modifier,
    value: String,
    readOnly: Boolean = false,
    enabled: Boolean = true,
    supportingText: String,
    onValueChange: (String) -> Unit
) {
    TextField(
        colors = TextFieldDefaults
            .colors(
                unfocusedContainerColor = Color.Transparent,
                focusedContainerColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledContainerColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent
            ),
        placeholder = {
            Text(
                fontSize = 25.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.scrim,
                text = supportingText
            )
        },
        readOnly = readOnly,
        enabled = enabled,
        textStyle = TextStyle(
            fontSize = 25.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.scrim
        ),
        modifier = modifier,
        value = value,
        onValueChange = onValueChange
    )
}

@Composable
fun DatePickerModal(
    onDateSelected: (Long?) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState()

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                onDateSelected(datePickerState.selectedDateMillis)
                onDismiss()
            }) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerDial(
    onConfirm: (String) -> Unit,
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
            Button(onClick = {
                onConfirm("${timePickerState.hour}:${timePickerState.minute}")
            }
            ) {
                Text("Confirm time")
            }
        },
        title = {
            Text(
                text = "Choose time"
            )
        },
    ){
        TimePicker(state = timePickerState)
    }
}
