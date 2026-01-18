package com.example.habitflow.presentation.screens.goals.create

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.example.habitflow.R
import com.example.habitflow.domain.entities.GoalCategory
import com.example.habitflow.domain.entities.Milestone
import com.example.habitflow.presentation.screens.goals.OpenReason
import com.example.habitflow.presentation.screens.tasks.creation.DatePickerModal
import com.example.habitflow.presentation.utils.DateFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateGoalScreen(
    viewModel: CreateGoalViewModel = hiltViewModel(),
    openReason: OpenReason = OpenReason.CREATE,
    onFinished: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    Scaffold(
        containerColor = Color(0xFFF8FAFC),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = when(openReason){
                            OpenReason.CREATE -> {
                                "Новая Цель"
                            }
                            OpenReason.EDIT -> {
                                state.title
                            }
                        }
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
                navigationIcon = {
                    Icon(
                        modifier = Modifier
                            .clip(CircleShape)
                            .clickable {
                                onFinished()
                            }
                            .padding(start = 8.dp),
                        painter = painterResource(R.drawable.ic_arrow_back),
                        contentDescription = "go back"
                    )
                },
                actions = {
                    Icon(
                        modifier = Modifier
                            .clip(CircleShape)
                            .clickable {}
                            .padding(end = 16.dp),
                        painter = painterResource(R.drawable.ic_more_horiz),
                        contentDescription = "more options"
                    )
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = paddingValues
        ) {


            item {
                val imagePicker = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.GetContent()
                ) { uri ->
                    uri?.let {
                        viewModel.processCommand(CreateGoalCommand.AddPhoto(it))
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.Gray.copy(alpha = 0.5f))
                        .heightIn(min = 200.dp)
                        .clickable {
                            imagePicker.launch("image/*")
                        }
                ) {
                    if (state.coverUri == null) {
                        Column(
                            modifier = Modifier.align(Alignment.Center),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {

                            Spacer(Modifier.size(8.dp))
                            Box(
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(Color.Gray.copy(alpha = 0.2f))
                            ) {
                                Icon(
                                    modifier = Modifier
                                        .padding(all = 8.dp),
                                    painter = painterResource(R.drawable.ic_photo_camera),
                                    contentDescription = "add picture"
                                )
                            }
                            Text(
                                color = MaterialTheme.colorScheme.onPrimary,
                                text = "Add cover photo",
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    } else {
                        AsyncImage(
                            model = state.coverUri,
                            contentDescription = "cover of goal",
                            contentScale = ContentScale.FillWidth
                        )
                        Icon(
                            modifier = Modifier
                                .clip(CircleShape)
                                .align(Alignment.TopEnd)
                                .padding(top = 16.dp, end = 16.dp)
                                .clickable {
                                    viewModel.processCommand(CreateGoalCommand.ClickDeletePhoto)
                                },
                            painter = painterResource(R.drawable.ic_close),
                            tint = Color.Black,
                            contentDescription = "delete cover"
                        )
                    }

                }
            }

            item {
                CreateGoalTextFieldWithTitle(
                    value = state.title,
                    fieldTitle = "Название цели",
                    placeholderText = "Например: Выучить испанский язык"
                ) {
                    viewModel.processCommand(CreateGoalCommand.InputTitle(it))
                }
            }
            item {
                Text(
                    text = "Категория",
                )
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    GoalCategory.entries.forEach { goalCategory ->
                        FilterChip(
                            selected = state.goalCategory == goalCategory,
                            border = null,
                            label = {
                                Text(
                                    modifier = Modifier.padding(all = 8.dp),
                                    text = goalCategory.title
                                )
                            },
                            onClick = {
                                viewModel.processCommand(
                                    CreateGoalCommand.ChangeGoalCategory(goalCategory)
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(0xFF10B981),
                                selectedLabelColor = Color.White,
                                disabledLabelColor = Color(0xFF065F54),
                                disabledContainerColor = Color(0xFFE6FFFB),
                            )
                        )
                    }
                }
            }
            item {
                var isStartDatePickerEnabled by remember{ mutableStateOf(false) }
                var isEndDatePickerEnabled by remember{ mutableStateOf(false) }
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    CreateGoalTextFieldWithTitle(
                        modifier = Modifier
                            .weight(1f),
                        fieldTitle = "Дата начала",
                        readOnly = true,
                        value = DateFormatter.formatDate(state.startDate),
                        enabled = false,
                        onTextFieldClick ={
                            isStartDatePickerEnabled = true
                        },
                        trailingIconId = R.drawable.ic_calendar_today,
                        placeholderText = "Начало",
                    ) {
                    }
                    CreateGoalTextFieldWithTitle(
                        modifier = Modifier
                            .weight(1f),
                        fieldTitle = "Дата окончания",
                        readOnly = true,
                        enabled = false,
                        value = DateFormatter.formatDate(state.endDate),
                        onTextFieldClick = {
                            isEndDatePickerEnabled = true
                        },
                        placeholderText = "Окончание",
                        trailingIconId = R.drawable.ic_calendar
                    ) {
                    }
                    if (isStartDatePickerEnabled){
                        DatePickerModal(
                            onDateSelected = {date ->
                                date?.let {
                                    viewModel.processCommand(CreateGoalCommand.ChooseStartDate(it))
                                    isStartDatePickerEnabled = false
                                }
                            }
                        ) {
                            isStartDatePickerEnabled = false
                        }
                    }
                    if (isEndDatePickerEnabled){
                        DatePickerModal(
                            onDateSelected = {date ->
                                date?.let {
                                    viewModel.processCommand(CreateGoalCommand.ChooseEndDate(it))
                                    isEndDatePickerEnabled = false
                                }
                            }
                        ) {
                            isEndDatePickerEnabled = false
                        }
                    }
                }
            }
            item {
                CreateGoalTextFieldWithTitle(
                    fieldTitle = "Описание",
                    minLines = 5,
                    value = state.description,
                    placeholderText = "Опишите, почему эта цель важна для вас"
                ) {
                    viewModel.processCommand(
                        CreateGoalCommand.InputDescription(it)
                    )
                }
            }
            item {
                Text(
                    text = "Подцели (Шаги)",
                    fontWeight = FontWeight.Bold
                )
            }
            itemsIndexed(state.milestones) { index, milestone ->
                MilestoneCard(
                    milestone = milestone,
                    onRemoveMilestoneClick = {
                        viewModel.processCommand(
                            CreateGoalCommand.RemoveMilestoneAt(
                                index
                            )
                        )
                    },
                    onRadioButtonClick = {
                        viewModel.processCommand(CreateGoalCommand.ChangeMilestoneCompletedStatusAt(index))
                    }
                ) {
                    viewModel.processCommand(
                        CreateGoalCommand.InputMilestoneTitle(
                            title = it,
                            index = index
                        )
                    )
                }
            }
            item {
                Button(
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFE1F4EF),
                        contentColor = Color(0xFF10B981)
                    ),
                    onClick = {
                        viewModel.processCommand(CreateGoalCommand.ClickAddMilestone)
                    }
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_add),
                        contentDescription = "add milestone"
                    )
                    Spacer(Modifier.size(8.dp))
                    Text(
                        text = "Добавить шаг",
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
            item {
                HorizontalDivider(
                    color = Color(0xFFB4C0C2)
                )
            }
            item {
                Button(
                    modifier = Modifier
                        .fillMaxWidth(),
                    enabled = state.isSaveButtonEnabled,
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF10B981),
                        contentColor = Color.White
                    ),
                    onClick = {
                        when(openReason){
                            OpenReason.CREATE -> {
                                viewModel.processCommand(CreateGoalCommand.ClickCreateGoal)
                            }
                            OpenReason.EDIT -> {
                                viewModel.processCommand(CreateGoalCommand.ClickUpdateGoal)
                            }
                        }

                        onFinished()
                    }
                ) {
                    Text(
                        modifier = Modifier.padding(vertical = 16.dp),
                        text = when(openReason){
                            OpenReason.CREATE -> {
                                "Создать цель"
                            }
                            OpenReason.EDIT -> {
                                "Сохранить изменения"
                            }
                        },
                        fontSize = 18.sp
                    )
                }
            }
        }
    }
}

@Composable
fun MilestoneCard(
    modifier: Modifier = Modifier,
    milestone: Milestone,
    onRemoveMilestoneClick: () -> Unit,
    onRadioButtonClick: () -> Unit,
    onValueChange: (String) -> Unit
) {
    ElevatedCard(
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.onPrimary
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                modifier = Modifier
                    .clip(CircleShape),
                colors = RadioButtonDefaults.colors(),
                selected = milestone.isCompleted,
                onClick = {
                    onRadioButtonClick()
                }
            )
            TextField(
                modifier = modifier
                    .fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                trailingIcon = {
                    Icon(
                        modifier = Modifier
                            .clip(CircleShape)
                            .clickable {
                                onRemoveMilestoneClick()
                            },
                        painter = painterResource(R.drawable.ic_close),
                        contentDescription = "remove milestone"
                    )
                },
                value = milestone.title,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                ),
                onValueChange = onValueChange,
                placeholder = {
                    Text(
                        text = "Добавьте описание цели"
                    )
                }

            )
        }
    }
}

@Composable
fun CreateGoalTextFieldWithTitle(
    modifier: Modifier = Modifier,
    value: String,
    fieldTitle: String,
    trailingIconId: Int? = null,
    minLines: Int = 1,
    readOnly: Boolean = false,
    enabled: Boolean = true,
    placeholderText: String,
    onTextFieldClick: () -> Unit = {},
    onValueChange: (String) -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
    ) {
        Text(
            text = fieldTitle
        )
        Spacer(Modifier.size(8.dp))
        TextField(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .clickable { onTextFieldClick() }
                .fillMaxWidth()
                .border(
                    BorderStroke(1.dp, Color(0xFFEBEBEB)),
                    shape = RoundedCornerShape(12.dp)
                ),
            enabled = enabled,
            value = value,
            readOnly = readOnly,
            minLines = minLines,
            shape = RoundedCornerShape(12.dp),
            onValueChange = onValueChange,
            colors = TextFieldDefaults.colors(
                disabledContainerColor = Color.White,
                disabledIndicatorColor = Color.White,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                unfocusedIndicatorColor = Color.White,
                focusedIndicatorColor = Color.White,
            ),
            placeholder = {
                Text(
                    text = placeholderText
                )
            },
            trailingIcon = {
                trailingIconId?.let {
                    Icon(
                        painter = painterResource(it),
                        contentDescription = "icon"
                    )
                }
            },
            )
    }
}