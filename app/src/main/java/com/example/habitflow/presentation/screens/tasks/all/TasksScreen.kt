package com.example.habitflow.presentation.screens.tasks.all

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import com.example.habitflow.presentation.screens.tasks.creation.DatePickerModal
import com.example.habitflow.presentation.screens.tasks.creation.TimePickerDial
import com.example.habitflow.presentation.utils.DateFormatter
import com.example.habitflow.presentation.utils.DateFormatter.formatTime
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TasksScreen(
    tasksViewModel: TasksViewModel = hiltViewModel(),
    onAddTaskClick: () -> Unit
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
                            .clickable{

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
                if (isDatePickerOpened){
                    DatePickerModal(
                        onDateSelected = {
                            it?.let {date ->
                                tasksViewModel.processCommand(
                                    TasksCommand.InputDate(date)
                                )
                            }

                        }
                    ) {
                        isDatePickerOpened = false
                    }
                }
                if (isTimePickerOpened){
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
                            .clickable{
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
                            .clickable{
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
                    Priority.entries.forEach {priority ->
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
                    text = "Create task"
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
        containerColor = MaterialTheme.colorScheme.background,
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
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
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Tasks"
                    )
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Today"
                )
                Text(
                    text = state.todayTasks.size.toString()
                )
            }
            LazyColumn(

            ) {

            }

        }
    }

}


@Composable
fun TaskCard(
    modifier: Modifier = Modifier,
    task: Task,
    onRadioButtonClick: () -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth(),
        border = BorderStroke(1.dp, Color.Blue)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = false,
                onClick = onRadioButtonClick
            )
            Column(
            ) {
                Text(
                    text = task.title
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = task.priority.title
                    )
                    Text(
                        text = task.category.title
                    )
                }
            }
        }
    }
}

