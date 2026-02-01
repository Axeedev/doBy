package com.example.habitflow.presentation.screens.tasks.all

import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.habitflow.R
import com.example.habitflow.domain.entities.Priority
import com.example.habitflow.domain.entities.Task
import com.example.habitflow.presentation.screens.goals.create.CreateGoalTextFieldWithTitle
import com.example.habitflow.presentation.screens.goals.create.CreateOrEditButton
import com.example.habitflow.presentation.screens.goals.create.FilterChips
import com.example.habitflow.presentation.screens.goals.create.MyTextField
import com.example.habitflow.presentation.screens.tasks.TaskDeadlineSection
import com.example.habitflow.presentation.screens.tasks.creation.DatePickerModal
import com.example.habitflow.presentation.screens.tasks.creation.TimePickerDial
import com.example.habitflow.presentation.utils.DateFormatter
import com.example.habitflow.presentation.utils.DateFormatter.formatTime
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TasksScreen(
    tasksViewModel: TasksViewModel = hiltViewModel(),
    onOpenMenuClick: () -> Unit
) {
    val state by tasksViewModel.state.collectAsState()
    var showBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    val scope = rememberCoroutineScope()
    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = {
                scope.launch { sheetState.hide() }
                showBottomSheet = false
            },
            sheetState = sheetState
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        modifier = Modifier
                            .weight(1f),
                        text = "Create new task",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Icon(
                        modifier = Modifier
                            .padding(end = 16.dp)
                            .clip(CircleShape)
                            .clickable {

                            },
                        painter = painterResource(R.drawable.ic_archive),
                        contentDescription = "Archive task"
                    )
                }
                CreateGoalTextFieldWithTitle(
                    value = state.title,
                    fieldTitle = "Task name",
                    placeholderText = "Например: полить цветы",
                    onValueChange = {
                        tasksViewModel.processCommand(TasksCommand.InputTitle(it))
                    }
                )

                var isDatePickerOpened by remember { mutableStateOf(false) }
                var isTimePickerOpened by remember { mutableStateOf(false) }
                if (isDatePickerOpened) {
                    DatePickerModal(
                        onDateSelected = {
                            it?.let { date ->
                                tasksViewModel.processCommand(
                                    TasksCommand.InputDate(date)
                                )
                            }

                        }
                    ) {
                        isDatePickerOpened = false
                    }
                }
                if (isTimePickerOpened) {
                    TimePickerDial(
                        onConfirm = {
                            tasksViewModel.processCommand(
                                TasksCommand.InputDeadline(it)
                            )
                        }
                    ) {
                        isTimePickerOpened = false
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    MyTextField(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .clickable {
                                isDatePickerOpened = true
                            },
                        leadingIconId = R.drawable.ic_calendar_today,
                        placeholderText = "Select date",
                        value = state.date?.let {
                            DateFormatter.formatDate(it)
                        } ?: ""
                    )
                    val deadline = state.remindAtMinutesOfDay
                    MyTextField(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .weight(1f)
                            .clickable {
                                isTimePickerOpened = true
                            },
                        leadingIconId = R.drawable.ic_calendar,
                        placeholderText = "Set time",
                        value = deadline?.formatTime() ?: ""
                    )

                }

                CreateGoalTextFieldWithTitle(
                    value = state.description,
                    fieldTitle = "Description",
                    placeholderText = "Add task details",
                    minLines = 4
                ) {
                    tasksViewModel.processCommand(TasksCommand.InputDescription(it))
                }
                Text(
                    text = "Category"
                )
                FilterChips(
                    stateGoalCategory = state.goalCategory
                ) {
                    tasksViewModel.processCommand(TasksCommand.ChangeCategory(it))
                }
                Text(
                    text = "Priority"
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Priority.entries.forEach { priority ->
                        FilterChip(
                            selected = state.priority == priority,
                            onClick = {
                                tasksViewModel.processCommand(TasksCommand.ChangePriority(priority))
                            },
                            label = {
                                Text(
                                    text = priority.title,
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                                )
                            },
                            shape = CircleShape,
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(0xFF10B981),
                                selectedLabelColor = Color.White,
                                containerColor = Color.White,
                                disabledLabelColor = Color(0xFF065F54),
                                disabledContainerColor = Color.White,
                            )
                        )
                    }
                }
                CreateOrEditButton(
                    isSaveButtonEnabled = state.isButtonEnabled,
                    text = state.buttonText
                ) {
                    tasksViewModel.processCommand(TasksCommand.AddTask)
                    scope.launch {
                        sheetState.hide()
                    }
                }
            }
        }
    }
    Scaffold(
        containerColor = Color.White,
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    tasksViewModel.processCommand(TasksCommand.ClickButtonAddTask)
                    showBottomSheet = true

                }
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_add),
                    contentDescription = "Add task"
                )
            }
        },
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
                title = {
                    Text(
                        modifier = Modifier
                            .padding(start = 16.dp),
                        fontWeight = FontWeight.Medium,
                        text = "My tasks"
                    )
                },
                navigationIcon = {
                    Icon(
                        modifier = Modifier
                            .padding(start = 16.dp)
                            .clickable {
                                onOpenMenuClick()
                            },
                        painter = painterResource(R.drawable.ic_menu),
                        contentDescription = "open menu"
                    )
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TaskDeadlineSection.entries.forEach { taskDeadlineSection ->
                    val tasks = state.tasksMapSections[taskDeadlineSection].orEmpty()
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = taskDeadlineSection.title,
                                    color = Color.Black,
                                    fontWeight = FontWeight.SemiBold
                                )
                                if (tasks.isNotEmpty()) {
                                    Text(
                                        text = "${tasks.size} tasks",
                                        fontWeight = FontWeight.Medium,
                                        color = Color(0xFF94A3B8)
                                    )
                                }
                            }
//                            if (tasks.isNotEmpty()) {
//                                Box(
//                                    modifier = Modifier
//                                        .clip(CircleShape)
//                                        .background(Color.White)
//                                ) {
//                                    Text(
//                                        modifier = Modifier.padding(
//                                            horizontal = 16.dp,
//                                            vertical = 4.dp
//                                        ),
//                                        text = "Tasks: ${tasks.size}",
//                                        fontSize = 12.sp,
//                                        fontWeight = FontWeight.SemiBold
//                                    )
//                                }
//                            }
                        }
//                        if (taskDeadlineSection == TaskDeadlineSection.TODAY && tasks.isEmpty()){
//                            Text(
//                                modifier = Modifier
//                                    .padding(vertical = 8.dp)
//                                    .clip(RoundedCornerShape(12.dp))
//                                    .clickable{
//                                        showBottomSheet = true
//                                    },
//                                text = "Нажмите на текст или на + чтобы добавить задачу",
//                                color = Color.Gray
//                            )
//                        }

                    }
                    itemsIndexed(items = tasks) { index, task ->
                        TaskCard(
                            modifier = Modifier
                                .animateItem(
                                    fadeOutSpec = tween(300),
                                    fadeInSpec = tween(300)
                                ),
                            task = task,
                            onTaskClick = {
                                tasksViewModel.processCommand(TasksCommand.ClickTask(task))
                                showBottomSheet = true
                            }
                        ) {
                            tasksViewModel.processCommand(
                                TasksCommand.ClickCompleteTask(
                                    task,
                                    taskDeadlineSection
                                )
                            )
                        }
                        if (index != tasks.lastIndex){
                            HorizontalDivider(
                                modifier = Modifier.padding(top = 8.dp),
                                thickness = 0.8.dp
                            )
                        }
                    }

                }
            }

        }
    }

}


@Composable
fun TaskCard(
    modifier: Modifier = Modifier,
    task: Task,
    onTaskClick: () -> Unit,
    onRadioButtonClick: () -> Unit
) {
    Card(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable {
                onTaskClick()
            }
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.White,
            contentColor = Color.Unspecified
        ),
        shape = RoundedCornerShape(12.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = task.isCompleted,
                onClick = onRadioButtonClick,
                colors = RadioButtonDefaults.colors(
                    selectedColor = Color(0xFF10B981)
                )
            )
            Column(
            ) {
                Text(
                    text = task.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Box(
                        modifier = Modifier.clip(CircleShape)
                            .background(Color(0XFFF1F5F9))
                    ) {
                        Text(
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
                            text = task.category.title,
                            color = Color.Black,
                            fontSize = 14.sp
                        )
                    }
                    task.deadlineMillis?.let { deadline ->

                        val formatter = DateTimeFormatter.ofPattern("HH:mm")
                        val time = Instant.ofEpochMilli(deadline)
                            .atZone(ZoneId.systemDefault())
                            .format(formatter)
                        Text(
                            text = time,
                            fontWeight = FontWeight.W400,
                            color = Color(0XFF94A3B8)
                        )
                    }

                }
            }
        }
    }
}

