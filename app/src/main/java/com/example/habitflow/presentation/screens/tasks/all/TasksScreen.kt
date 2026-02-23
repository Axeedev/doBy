package com.example.habitflow.presentation.screens.tasks.all

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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.habitflow.R
import com.example.habitflow.domain.entities.goals.GoalCategory
import com.example.habitflow.domain.entities.tasks.Priority
import com.example.habitflow.domain.entities.tasks.Task
import com.example.habitflow.presentation.screens.goals.create.AppTextField
import com.example.habitflow.presentation.screens.goals.create.CreateGoalTextFieldWithTitle
import com.example.habitflow.presentation.screens.goals.create.CreateOrEditButton
import com.example.habitflow.presentation.screens.goals.create.FilterChips
import com.example.habitflow.presentation.screens.tasks.DatePickerModal
import com.example.habitflow.presentation.screens.tasks.TaskDeadlineSection
import com.example.habitflow.presentation.screens.tasks.TimePickerDial
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
    onGoToAchievementClick: () -> Unit,
    onOpenMenuClick: () -> Unit
) {
    val state by tasksViewModel.state.collectAsState()
    var showBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        tasksViewModel.unlockEvent.collect { event ->
            when (event) {
                AchievementEvent.AchievementUnlocked -> {
                    snackbarHostState.showSnackbar(
                        message = "",
                        withDismissAction = true,
                        duration = SnackbarDuration.Short
                    )
                }
            }
        }
    }
    LaunchedEffect(Unit) {
        tasksViewModel.bottomSheetEvents.collect { event ->
            when(event){
                BottomSheetEvent.CloseSheet -> {
                    showBottomSheet = false
                }
                BottomSheetEvent.OpenSheet -> {
                    showBottomSheet = true
                }
            }
        }
    }

    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = {
                tasksViewModel.processCommand(TasksCommand.CloseBottomSheet)
            },
            containerColor = MaterialTheme.colorScheme.background,
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
                        text = if (state.taskId == null) stringResource(R.string.create_new_task) else stringResource(R.string.edit_task),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    val taskId = state.taskId
                    if (taskId != null && state.goalCategory != GoalCategory.CALENDAR) {
                        Icon(
                            modifier = Modifier
                                .padding(end = 16.dp)
                                .clip(CircleShape)
                                .clickable {
                                    tasksViewModel.processCommand(TasksCommand.DeleteTask(taskId))
                                },
                            painter = painterResource(R.drawable.ic_delete),
                            contentDescription = "Delete task"
                        )
                    }
                }
                CreateGoalTextFieldWithTitle(
                    value = state.title,
                    fieldTitle = stringResource(R.string.task_name_field),
                    placeholderText = stringResource(R.string.task_name_placeholder),
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
                    AppTextField(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .clickable {
                                isDatePickerOpened = true
                            },
                        leadingIconId = R.drawable.ic_calendar_today,
                        placeholderText = stringResource(R.string.select_date_field),
                        value = state.date?.let {
                            DateFormatter.formatDate(it)
                        } ?: ""
                    )
                    val deadline = state.remindAtMinutesOfDay
                    AppTextField(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .weight(1f)
                            .clickable {
                                isTimePickerOpened = true
                            },
                        leadingIconId = R.drawable.ic_calendar,
                        placeholderText = stringResource(R.string.set_time_field),
                        value = deadline?.formatTime() ?: ""
                    )
                }
                CreateGoalTextFieldWithTitle(
                    value = state.description,
                    fieldTitle = stringResource(R.string.tasks_description_field),
                    placeholderText = stringResource(R.string.tasks_description_placeholder),
                    minLines = 4
                ) {
                    tasksViewModel.processCommand(TasksCommand.InputDescription(it))
                }
                Text(
                    text = stringResource(R.string.category_field)
                )
                FilterChips(
                    stateGoalCategory = state.goalCategory
                ) {
                    tasksViewModel.processCommand(TasksCommand.ChangeCategory(it))
                }
                Text(
                    text = stringResource(R.string.priority_field)
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
                                selectedContainerColor = MaterialTheme.colorScheme.tertiary,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimaryFixedVariant,
                                containerColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                disabledContainerColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            )
                        )
                    }
                }
                CreateOrEditButton(
                    isSaveButtonEnabled = state.isButtonEnabled,
                    text = if (state.taskId == null )stringResource(R.string.create_new_task) else stringResource(R.string.save_changes_button)
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
        containerColor = MaterialTheme.colorScheme.primary,
        floatingActionButton = {
            AppFAB{
                tasksViewModel.processCommand(TasksCommand.ClickButtonAddTask)
            }

        },
        snackbarHost = {
            SnackbarHost(
                modifier = Modifier
                    .padding(horizontal = 16.dp),
                hostState = snackbarHostState
            ) { data ->
                Snackbar(
                    modifier = Modifier,
                    action = {
                        Button(
                            onClick = {
                                onGoToAchievementClick()
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Transparent,
                                contentColor = Color(0xFF007AFF)
                            )
                        ) {
                            Text(
                                text = stringResource(R.string.go_to_achievements),
                                fontWeight = FontWeight.Medium
                            )
                        }
                    },
                    containerColor = Color(0xFFF5F9FF),
                    contentColor = Color(0xFF2F3F53),
                    shape = RoundedCornerShape(12.dp),
                ) {
                    Text(
                        text = stringResource(R.string.new_achievement_unlocked),
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        },
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
                title = {
                    Text(
                        modifier = Modifier
                            .padding(start = 16.dp),
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary,
                        text = stringResource(R.string.my_tasks_screen)
                    )
                },
                navigationIcon = {
                    Icon(
                        modifier = Modifier
                            .padding(start = 16.dp)
                            .clip(CircleShape)
                            .clickable {
                                onOpenMenuClick()
                            },
                        painter = painterResource(R.drawable.ic_menu),
                        contentDescription = "open menu",
                        tint = MaterialTheme.colorScheme.onPrimary
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
                                    text = stringResource(taskDeadlineSection.titleId),
                                    color = if (tasks.isNotEmpty()) MaterialTheme.colorScheme.onPrimary
                                    else MaterialTheme.colorScheme.onPrimaryFixed,
                                    fontWeight = FontWeight.SemiBold
                                )
                                if (tasks.isNotEmpty()) {
                                    Text(
                                        text = pluralStringResource(
                                            R.plurals.tasks_count,
                                            tasks.size,
                                            tasks.size
                                        ),
                                        fontWeight = FontWeight.Medium,
                                        color = MaterialTheme.colorScheme.onPrimaryFixed
                                    )
                                }
                            }

                        }

                    }
                    itemsIndexed(items = tasks) { index, task ->
                        TaskCard(
                            modifier = Modifier,
                            task = task,
                            taskDeadlineSection = taskDeadlineSection,
                            onTaskClick = {
                                tasksViewModel.processCommand(TasksCommand.ClickTask(task))
                            }
                        ) {
                            tasksViewModel.processCommand(
                                TasksCommand.ClickCompleteTask(
                                    task,
                                    taskDeadlineSection
                                )
                            )
                        }
                        if (index != tasks.lastIndex) {
                            HorizontalDivider(
                                modifier = Modifier.padding(top = 8.dp),
                                thickness = 0.8.dp,
                                color = MaterialTheme.colorScheme.surfaceTint
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
    taskDeadlineSection: TaskDeadlineSection,
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
            containerColor = MaterialTheme.colorScheme.primary,
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
                    selectedColor = MaterialTheme.colorScheme.scrim
                )
            )
            Column{
                Text(
                    text = task.title,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontSize = 16.sp
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.onTertiary)
                    ) {
                        Text(
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
                            text = stringResource(task.category.titleId),
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontSize = 14.sp
                        )
                    }
                    task.deadlineMillis?.let { deadline ->

                        val formatter = DateTimeFormatter.ofPattern("HH:mm")

                        val taskTime = when (taskDeadlineSection) {
                            TaskDeadlineSection.TODAY -> {
                                Instant.ofEpochMilli(deadline)
                                    .atZone(ZoneId.systemDefault())
                                    .format(formatter)
                            }

                            TaskDeadlineSection.TOMORROW -> {
                                Instant.ofEpochMilli(deadline)
                                    .atZone(ZoneId.systemDefault())
                                    .format(formatter)
                            }

                            TaskDeadlineSection.NEXT_WEEK -> {
                                DateFormatter.formatDate(deadline)
                            }

                            TaskDeadlineSection.LATER -> {
                                DateFormatter.formatDate(deadline)
                            }
                        }


                        Text(
                            text = taskTime,
                            fontWeight = FontWeight.W400,
                            color = MaterialTheme.colorScheme.onPrimaryFixed
                        )
                    }

                }
            }
        }
    }
}

@Composable
fun AppFAB(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
){
    FloatingActionButton(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.tertiary,
        contentColor = Color.White,
        onClick = onClick
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_add),
            contentDescription = "Add"
        )
    }
}