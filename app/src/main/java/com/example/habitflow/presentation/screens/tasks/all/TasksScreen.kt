@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.habitflow.presentation.screens.tasks.all

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.material3.SheetState
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.habitflow.R
import com.example.habitflow.domain.entities.Category
import com.example.habitflow.domain.entities.tasks.Priority
import com.example.habitflow.domain.entities.tasks.Task
import com.example.habitflow.presentation.screens.goals.create.AppTextField
import com.example.habitflow.presentation.screens.goals.create.CreateGoalTextFieldWithTitle
import com.example.habitflow.presentation.screens.goals.create.CreateOrEditButton
import com.example.habitflow.presentation.screens.tasks.DatePickerModal
import com.example.habitflow.presentation.screens.tasks.TaskDeadlineSection
import com.example.habitflow.presentation.screens.tasks.TimeEntity
import com.example.habitflow.presentation.screens.tasks.TimePickerDial
import com.example.habitflow.presentation.utils.DateFormatter
import com.example.habitflow.presentation.utils.DateFormatter.formatTime
import kotlinx.coroutines.delay
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

    TasksScreenContent(
        tasksViewModel = tasksViewModel,
        state = state,
        onGoToAchievementClick = onGoToAchievementClick,
        onOpenMenuClick = onOpenMenuClick
    )
}

@Composable
fun TasksScreenContent(
    tasksViewModel: TasksViewModel = hiltViewModel(),
    state: TasksScreenState,
    onGoToAchievementClick: () -> Unit,
    onOpenMenuClick: () -> Unit
) {
    val context = LocalContext.current

    var hasPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasPermission = isGranted
    }

    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        tasksViewModel.snackbarEvents.collect { event ->
            when (event) {
                SnackbarEvent.SnackbarUnlocked -> {
                    snackbarHostState.showSnackbar(
                        message = "",
                        withDismissAction = true,
                        duration = SnackbarDuration.Short
                    )
                }

                SnackbarEvent.SpeechRecognizingError -> {
                    snackbarHostState.showSnackbar(
                        message = "An error has occurred in speech recognizing. Try again",
                        withDismissAction = false,
                        duration = SnackbarDuration.Short
                    )
                }

                SnackbarEvent.VoiceRecordError -> {

                }
            }
        }
    }
    if (state.showBottomSheet) {
        AddTaskBottomSheet(
            tasksViewModel = tasksViewModel,
            sheetState = sheetState,
            state = state
        )
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.primary,
        floatingActionButton = {
            TasksScreenFAB(
                isVoiceRecording = state.isVoiceRecording,
                onToggleRecording = {isRecording ->
                    if (!hasPermission) {
                        launcher.launch(Manifest.permission.RECORD_AUDIO)
                    } else {
                        if (isRecording) {
                            tasksViewModel.processCommand(TasksCommand.StartVoiceInput)
                        } else {
                            tasksViewModel.processCommand(TasksCommand.StopVoiceInput)
                        }
                    }
                }
            ) {
                tasksViewModel.processCommand(TasksCommand.ClickButtonAddTask)
            }
        },
        snackbarHost = {
            TasksScreenSnackbar(
                snackbarHostState = snackbarHostState
            ) {
                onGoToAchievementClick()
            }
        },
        topBar = {
            TasksScreenTopAppBar(
                isRefreshLoading = state.isRefreshLoading
            ) {
                onOpenMenuClick()
            }
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
                        TaskSectionInfo(
                            taskDeadlineSection = taskDeadlineSection,
                            isTasksEmpty = tasks.isNotEmpty(),
                            tasksSize = tasks.size
                        )

                        if (state.tasksMapSections.isEmpty() && taskDeadlineSection == TaskDeadlineSection.TODAY) {
                            EmptyTasksForToday()
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
fun EmptyTasksForToday() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            modifier = Modifier.size(100.dp),
            painter = painterResource(R.drawable.ic_no_tasks),
            contentDescription = "no tasks",
            tint = MaterialTheme.colorScheme.onPrimaryFixed
        )
        Text(
            text = stringResource(R.string.no_tasks_scheduled),
            fontWeight = FontWeight.SemiBold,
            fontSize = 20.sp,
            color = MaterialTheme.colorScheme.onPrimaryFixed
        )
    }
}

@Composable
fun TaskSectionInfo(
    taskDeadlineSection: TaskDeadlineSection,
    isTasksEmpty: Boolean,
    tasksSize: Int
) {
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
                color = if (isTasksEmpty) MaterialTheme.colorScheme.onPrimary
                else MaterialTheme.colorScheme.onPrimaryFixed,
                fontWeight = FontWeight.SemiBold
            )
            if (isTasksEmpty) {
                Text(
                    text = pluralStringResource(
                        R.plurals.tasks_count,
                        tasksSize,
                        tasksSize
                    ),
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onPrimaryFixed
                )
            }
        }
    }
}


@Composable
fun TasksScreenFAB(
    isVoiceRecording: Boolean,
    onToggleRecording: (Boolean) -> Unit,
    onFabClick: () -> Unit
) {
    val offsetY by animateDpAsState(
        targetValue = if (isVoiceRecording) 0.dp else 200.dp,
        animationSpec = tween(300, easing = FastOutSlowInEasing)
    )
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        SpeechPanel(
            modifier = Modifier
                .offset(y = offsetY)
                .padding(end = 24.dp)
        )
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            VoiceInputFAB(
                isRecording = isVoiceRecording
            ) { isRecording ->
                onToggleRecording(isRecording)
            }
            AppFAB {
                onFabClick()
            }
        }
    }
}

@Composable
fun TasksScreenSnackbar(
    snackbarHostState: SnackbarHostState,
    onButtonClick: () -> Unit
) {
    SnackbarHost(
        modifier = Modifier
            .padding(horizontal = 16.dp),
        hostState = snackbarHostState
    ) { snackbarData ->
        Snackbar(
            modifier = Modifier,
            action = {
                Button(
                    onClick = {
                        onButtonClick()
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
                text = snackbarData.visuals.message.ifEmpty { stringResource(R.string.new_achievement_unlocked) },
                fontWeight = FontWeight.Medium
            )
        }
    }
}


@Composable
fun AddTaskBottomSheet(
    modifier: Modifier = Modifier,
    tasksViewModel: TasksViewModel,
    sheetState: SheetState,
    state: TasksScreenState,
) {

    val scope = rememberCoroutineScope()

    ModalBottomSheet(
        modifier = modifier,
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
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {

            BottomSheetTitle(
                taskId = state.taskId,
                category = state.category
            ) { id ->
                id?.let {
                    tasksViewModel.processCommand(TasksCommand.DeleteTask(it))
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

            DateTimeFields(
                date = state.date,
                deadline = state.remindAtMinutesOfDay,
                onDateFieldClick = {
                    isDatePickerOpened = true
                }
            ) {
                isTimePickerOpened = true
            }

            CreateGoalTextFieldWithTitle(
                value = state.description,
                fieldTitle = stringResource(R.string.tasks_description_field),
                placeholderText = stringResource(R.string.tasks_description_placeholder),
                minLines = 4
            ) {
                tasksViewModel.processCommand(TasksCommand.InputDescription(it))
            }

            var isDialogOpened by remember { mutableStateOf(false) }

            CategoryChooseContent { isDialogOpened = true }

            if (isDialogOpened) {
                AddCategoryDialog(
                    categoryName = state.newCategoryName,
                    isAddCategoryButtonEnabled = state.isAddCategoryButtonEnabled,
                    onDismiss = {
                        isDialogOpened = false
                    },
                    onValueChange = {
                        tasksViewModel.processCommand(TasksCommand.InputCategoryName(it))
                    },
                ) {
                    tasksViewModel.processCommand(TasksCommand.AddNewCategory(it))
                    isDialogOpened = false
                }
            }

            CategoryChips(
                categories = state.categories,
                selectedCategory = state.category
            ) {
                tasksViewModel.processCommand(TasksCommand.ChangeCategory(Category(it)))
            }


            Text(
                text = stringResource(R.string.priority_field)
            )

            PriorityChips(
                selectedPriority = state.priority
            ) { priority ->
                tasksViewModel.processCommand(TasksCommand.ChangePriority(priority))
            }

            CreateOrEditButton(
                isSaveButtonEnabled = state.isButtonEnabled,
                text = if (state.taskId == null) stringResource(R.string.create_new_task) else stringResource(
                    R.string.save_changes_button
                )
            ) {
                tasksViewModel.processCommand(TasksCommand.AddTask)
                scope.launch {
                    sheetState.hide()
                }
            }
        }
    }
}

@Composable
fun TasksScreenTopAppBar(
    isRefreshLoading: Boolean,
    onOpenMenuClick: () -> Unit
) {
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
        },
        actions = {
            if (isRefreshLoading) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.onPrimaryFixed
                )
            }
        }
    )
}


@Composable
fun BottomSheetTitle(
    taskId: Int?,
    category: Category,
    onDeleteClick: (Int?) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = Modifier
                .weight(1f),
            text = if (taskId == null) stringResource(R.string.create_new_task) else stringResource(
                R.string.edit_task
            ),
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onPrimary
        )
        val taskId = taskId
        if (taskId != null && category.name != Category.CALENDAR_NAME) {
            Icon(
                modifier = Modifier
                    .padding(end = 16.dp)
                    .clip(CircleShape)
                    .clickable {
                        onDeleteClick(taskId)
                    },
                painter = painterResource(R.drawable.ic_delete),
                contentDescription = "Delete task"
            )
        }
    }
}

@Composable
fun DateTimeFields(
    date: Long?,
    deadline: TimeEntity?,
    onDateFieldClick: () -> Unit,
    onTimeClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        AppTextField(
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(12.dp))
                .clickable {
                    onDateFieldClick()
                },
            leadingIconId = R.drawable.ic_calendar_today,
            placeholderText = stringResource(R.string.select_date_field),
            value = date?.let {
                DateFormatter.formatDate(it)
            } ?: ""
        )
        AppTextField(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .weight(1f)
                .clickable {
                    onTimeClick()
                },
            leadingIconId = R.drawable.ic_calendar,
            placeholderText = stringResource(R.string.set_time_field),
            value = deadline?.formatTime() ?: ""
        )
    }
}


@Composable
fun PriorityChips(
    modifier: Modifier = Modifier,
    selectedPriority: Priority,
    onPriorityClick: (Priority) -> Unit
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Priority.entries.forEach { priority ->
            FilterChip(
                selected = selectedPriority == priority,
                onClick = {
                    onPriorityClick(priority)
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
}


@Composable
fun CategoryChooseContent(
    onButtonClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(R.string.category_field)
        )
        Button(
            onClick = onButtonClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.onPrimaryContainer
            )
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_add),
                contentDescription = "add category",
                tint = MaterialTheme.colorScheme.onPrimary
            )
            Text(
                text = stringResource(R.string.add_your_category),
            )
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
            Column {
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
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                            text = task.category.name,
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
    iconId: Int = R.drawable.ic_add,
    onClick: () -> Unit
) {
    FloatingActionButton(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.tertiary,
        contentColor = Color.White,
        onClick = onClick
    ) {
        Icon(
            painter = painterResource(iconId),
            contentDescription = "Add"
        )
    }
}

@Composable
fun AddCategoryDialog(
    categoryName: String,
    isAddCategoryButtonEnabled: Boolean,
    onDismiss: () -> Unit,
    onValueChange: (String) -> Unit,
    onComplete: (String) -> Unit
) {


    BasicAlertDialog(
        onDismissRequest = onDismiss,
    ) {
        Box(
            modifier = Modifier
                .size(width = 500.dp, height = 300.dp)
                .clip(RoundedCornerShape(32.dp))
                .background(
                    MaterialTheme.colorScheme.secondaryContainer
                ),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(vertical = 16.dp, horizontal = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Spacer(Modifier.size(24.dp))

                Text(
                    text = stringResource(R.string.enter_category_s_name),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onPrimary
                )
                TextField(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .fillMaxWidth()
                        .padding(vertical = 16.dp)
                        .border(
                            BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceTint),
                            shape = RoundedCornerShape(12.dp)
                        ),
                    placeholder = {
                        Text(
                            text = stringResource(R.string.category)
                        )
                    },
                    shape = RoundedCornerShape(12.dp),
                    onValueChange = onValueChange,
                    value = categoryName,
                    colors = TextFieldDefaults.colors(
                        disabledContainerColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        disabledIndicatorColor = Color.Transparent,
                        focusedContainerColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        unfocusedContainerColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedTextColor = MaterialTheme.colorScheme.onPrimary,
                        focusedTextColor = MaterialTheme.colorScheme.onPrimary,
                        disabledTextColor = MaterialTheme.colorScheme.onPrimary,
                        cursorColor = MaterialTheme.colorScheme.onPrimary

                    ),
                )

                Spacer(Modifier.size(16.dp))

                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(topEnd = 12.dp, topStart = 12.dp),
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.scrim.copy(alpha = 0.4f),
                    )
                ) {
                    Text(
                        modifier = Modifier.padding(vertical = 4.dp),
                        text = stringResource(R.string.cancel),
                        color = MaterialTheme.colorScheme.outline
                    )
                }
                Spacer(Modifier.size(8.dp))
                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(bottomStart = 12.dp, bottomEnd = 12.dp),
                    onClick = {
                        onComplete(categoryName)
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.scrim.copy(alpha = 0.4f),
                    ),
                    enabled = isAddCategoryButtonEnabled
                ) {
                    Text(
                        modifier = Modifier.padding(vertical = 4.dp),
                        text = stringResource(R.string.add),
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }
        }
    }
}


@Composable
fun CategoryChips(
    categories: List<Category>,
    selectedCategory: Category,
    onClick: (String) -> Unit
) {
    LazyVerticalGrid(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp),
        columns = GridCells.Fixed(2),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        userScrollEnabled = false
    ) {
        items(
            items = categories,
            key = {
                it.id
            }
        ) { category ->
            FilterChip(
                selected = selectedCategory.name == category.name,
                onClick = {
                    onClick(category.name)
                },
                label = {
                    Text(
                        text = category.name.replaceFirstChar {
                            it.uppercase()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        textAlign = TextAlign.Center
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
}

@Composable
fun VoiceInputFAB(
    isRecording: Boolean,
    onToggleRecording: (Boolean) -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    var longPressActive by remember { mutableStateOf(false) }

    LaunchedEffect(isPressed) {
        if (isPressed && !longPressActive) {
            delay(100)
            onToggleRecording(true)
            longPressActive = true
        } else if (!isPressed && longPressActive) {
            onToggleRecording(false)
            longPressActive = false
        }
    }
    FloatingActionButton(
        modifier = Modifier
            .semantics(mergeDescendants = true) {},
        interactionSource = interactionSource,
        containerColor = if (isRecording) MaterialTheme.colorScheme.surfaceDim else MaterialTheme.colorScheme.tertiary,
        contentColor = Color.White,
        onClick = { }
    ) {
        if (isRecording) {
            RecordingAnimation()
        } else {
            Icon(
                painter = painterResource(R.drawable.ic_mic),
                contentDescription = "Voice input"
            )
        }
    }
}


@Composable
private fun RecordingAnimation() {
    var visible by remember { mutableStateOf(true) }
    val alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0.3f,
        label = "micAlpha"
    )

    LaunchedEffect(Unit) {
        while (true) {
            delay(800)
            visible = !visible
        }
    }
    Icon(
        painter = painterResource(R.drawable.ic_mic),
        contentDescription = "Recording",
        modifier = Modifier.alpha(alpha)
    )
}

@Composable
fun SpeechPanel(
    modifier: Modifier = Modifier,
) {
    var visible by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(1500)
            visible = !visible
        }
    }

    val alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0.5f,
        animationSpec = tween(durationMillis = 1500)
    )
    Box(
        modifier = modifier
            .width(160.dp)
            .height(100.dp)
            .background(
                color = MaterialTheme.colorScheme.surfaceContainer,
                shape = RoundedCornerShape(24.dp)
            )
            .border(
                BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceTint),
                RoundedCornerShape(24.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            modifier = Modifier.padding(horizontal = 16.dp),
            text = stringResource(R.string.speak),
            fontSize = 20.sp,
            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = alpha)
        )
    }
}