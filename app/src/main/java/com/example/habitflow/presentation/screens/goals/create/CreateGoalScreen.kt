package com.example.habitflow.presentation.screens.goals.create

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.habitflow.R
import com.example.habitflow.domain.entities.GoalCategory
import com.example.habitflow.presentation.screens.goals.all.CreateGoalCommand

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateGoalScreen(
    viewModel: CreateGoalViewModel = hiltViewModel(),
    onFinished: () -> Unit
){
    val state by viewModel.state.collectAsState()
    Scaffold(
        containerColor = Color.White,
        contentColor = Color.Unspecified,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Новая цель"
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
                navigationIcon = {
                    Icon(
                        modifier = Modifier
                            .clip(CircleShape)
                            .clickable{
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
                            .clickable{}
                            .padding(end = 16.dp),
                        painter = painterResource(R.drawable.ic_more_horiz),
                        contentDescription = "more options"
                    )
                }
            )
        }
    ) {paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = paddingValues
        ) {
            item {
                CreateGoalTextFieldWithTitle(
                    value = state.title,
                    fieldTitle = "Название цели",
                    placeholderText = "Например: Выучить испанский язык"
                ) {
                    viewModel.processCommand(CreateGoalCommand.InputTitle(it))
                }
            }
            item{
                Text(
                    text = "Категория",
                )
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    GoalCategory.entries.forEach { goalCategory ->
                        FilterChip(
                            selected = state.goalCategory == goalCategory,
                            shape = RoundedCornerShape(20.dp),
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
                                selectedContainerColor = Color(0xFF06B6A4),
                                selectedLabelColor = Color.White,
                                disabledLabelColor = Color(0xFF065F54),
                                disabledContainerColor = Color(0xFFE6FFFB),
                            )
                        )
                    }
                }
            }
            item {
                CreateGoalTextFieldWithTitle(
                    fieldTitle = "Дата окончания",
                    value = state.endDate,
                    placeholderText = "Выберите дату окончания",
                    iconId = R.drawable.ic_calendar_today
                ) {
                    viewModel.processCommand(CreateGoalCommand.ChooseEndDate(it))
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
                    text = "Подцели (Шаги)"
                )
            }
            items(state.milestones, key = {it.id} ){milestone ->

            }
            item {
                Button(
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent,
                        contentColor = Color(0xFF06B6A4)
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
                        text = "Добавить шаг"
                    )
                }
            }
            item{
                HorizontalDivider(
                    color = Color(0xFFB4C0C2)
                )
            }
            item {
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    enabled = state.isSaveButtonEnabled,
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF06B6A4),
                        contentColor = Color.White
                    ),
                    onClick = {
                        viewModel.processCommand(CreateGoalCommand.ClickCreateGoal)
                        onFinished()
                    }
                ) {
                    Text(
                        modifier = Modifier.padding(vertical = 16.dp),
                        text = "Создать цель",
                        fontSize = 18.sp
                    )
                }
            }
        }
    }
}

@Composable
fun CreateGoalTextFieldWithTitle(
    modifier: Modifier = Modifier,
    value: String,
    fieldTitle: String,
    iconId: Int? = null,
    minLines: Int = 1,
    placeholderText: String,
    onValueChange: (String) -> Unit,
){
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
                .fillMaxWidth()
                .border(
                    BorderStroke(1.dp, Color(0xFFEBEBEB)),
                    shape = RoundedCornerShape(12.dp)
                ),
            value = value,
            minLines = minLines,
            shape = RoundedCornerShape(12.dp),
            onValueChange = onValueChange,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
            ),
            placeholder = {
                Text(
                    text = placeholderText
                )
            },
            trailingIcon = {
                iconId?.let {
                    Icon(
                        painter = painterResource(it),
                        contentDescription = "icon"
                    )
                }
            }
        )
    }
}