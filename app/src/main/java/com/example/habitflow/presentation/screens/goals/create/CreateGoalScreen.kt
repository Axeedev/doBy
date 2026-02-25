@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.habitflow.presentation.screens.goals.create

import android.net.Uri
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
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
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
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.example.habitflow.R
import com.example.habitflow.domain.entities.goals.GoalCategory
import com.example.habitflow.domain.entities.goals.Milestone
import com.example.habitflow.presentation.screens.goals.OpenReason
import com.example.habitflow.presentation.screens.tasks.DatePickerModal
import com.example.habitflow.presentation.screens.tasks.all.AddCategoryDialog
import com.example.habitflow.presentation.screens.tasks.all.CategoryChips
import com.example.habitflow.presentation.utils.DateFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateGoalScreen(
    viewModel: CreateGoalViewModel = hiltViewModel(
        creationCallback = { factory: CreateGoalViewModel.ViewModelFactory ->
            factory.create(null)
        }
    ),
    openReason: OpenReason = OpenReason.CREATE,
    onFinished: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            viewModel.processCommand(CreateGoalCommand.AddPhoto(it))
        }
    }

    var isCompleteDialogOpened by remember { mutableStateOf(false) }

    val goalId = state.goalId

    if (isCompleteDialogOpened && goalId != null) {
        CompleteDialog(
            onComplete = {
                isCompleteDialogOpened = false
                viewModel.processCommand(CreateGoalCommand.ClickCompleteGoal(goalId = goalId))
                onFinished()
            }
        ) {
            isCompleteDialogOpened = false
        }
    }
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            CreateAndEditGoalScreenTopBar(
                openReason = openReason,
                title = state.title,
                onFinished = onFinished,
                onCompleteGoalClick = {
                    isCompleteDialogOpened = true
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = paddingValues
        ) {
            item {
                Photo(
                    coverUri = state.coverUri,
                    onAddPhotoClick = {
                        imagePicker.launch("image/*")
                    }
                ) {
                    viewModel.processCommand(CreateGoalCommand.ClickDeletePhoto)
                }

                Spacer(Modifier.size(16.dp))

                CreateGoalTextFieldWithTitle(
                    value = state.title,
                    fieldTitle = stringResource(R.string.goal_title_field),
                    placeholderText = stringResource(R.string.goal_title_placeholder)
                ) {
                    viewModel.processCommand(CreateGoalCommand.InputTitle(it))
                }
                Spacer(Modifier.size(16.dp))

                var isDialogOpened by remember { mutableStateOf(false) }

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
                        onClick = {
                            isDialogOpened = true
                        },
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

                if (isDialogOpened) {
                    AddCategoryDialog(
                        categoryName = state.newCategoryName,
                        isAddCategoryButtonEnabled = state.isAddCategoryButtonEnabled,
                        onDismiss = {
                            isDialogOpened = false
                        },
                        onValueChange = {
                            viewModel.processCommand(CreateGoalCommand.InputCategoryName(it))
                        }
                    ) {
                        viewModel.processCommand(CreateGoalCommand.AddNewCategory(it))
                        isDialogOpened = false
                    }
                }
                CategoryChips(
                    categories = state.categories,
                    selectedCategory = state.goalCategory
                ) {
                    viewModel.processCommand(
                        CreateGoalCommand.ChangeGoalCategory(
                            GoalCategory(
                                it
                            )
                        )
                    )
                }
                DatePickerFields(
                    startDate = state.startDate,
                    endDate = state.endDate,
                    onStartDatePick = {
                        viewModel.processCommand(CreateGoalCommand.ChooseStartDate(it))
                    }
                ) {
                    viewModel.processCommand(CreateGoalCommand.ChooseEndDate(it))
                }
                Spacer(Modifier.size(16.dp))


                CreateGoalTextFieldWithTitle(
                    fieldTitle = stringResource(R.string.goals_description_field),
                    minLines = 5,
                    value = state.description,
                    placeholderText = stringResource(R.string.description_field_placeholder)
                ) {
                    viewModel.processCommand(
                        CreateGoalCommand.InputDescription(it)
                    )
                }
                Spacer(Modifier.size(16.dp))


                Text(
                    text = stringResource(R.string.milestones_field),
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.size(8.dp))

                AddMilestoneButton {
                    viewModel.processCommand(CreateGoalCommand.ClickAddMilestone)
                }
                Spacer(Modifier.size(16.dp))

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
                    shape = when (index) {
                        0 -> RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)
                        state.milestones.lastIndex -> RoundedCornerShape(
                            bottomEnd = 12.dp,
                            bottomStart = 12.dp
                        )

                        else -> RoundedCornerShape(0)
                    },
                    onRadioButtonClick = {
                        viewModel.processCommand(
                            CreateGoalCommand.ChangeMilestoneCompletedStatusAt(
                                index
                            )
                        )
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
                Spacer(Modifier.size(16.dp))
                HorizontalDivider(
                    color = Color(0xFFB4C0C2)
                )
                Spacer(Modifier.size(16.dp))


                CreateOrEditButton(
                    isSaveButtonEnabled = state.isSaveButtonEnabled,
                    text = when (openReason) {
                        OpenReason.CREATE -> {
                            stringResource(R.string.create_goal_button)
                        }

                        OpenReason.EDIT -> {
                            stringResource(R.string.save_changes_button)
                        }
                    },
                ) {
                    when (openReason) {
                        OpenReason.CREATE -> {
                            viewModel.processCommand(CreateGoalCommand.ClickCreateGoal)
                        }

                        OpenReason.EDIT -> {
                            viewModel.processCommand(CreateGoalCommand.ClickUpdateGoal)
                        }
                    }
                    onFinished()
                }
            }

        }

    }
}

@Composable
fun MilestoneCard(
    modifier: Modifier = Modifier,
    milestone: Milestone,
    shape: Shape,
    onRemoveMilestoneClick: () -> Unit,
    onRadioButtonClick: () -> Unit,
    onValueChange: (String) -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimary
        ),
        shape = shape,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceTint)

    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                modifier = Modifier
                    .clip(CircleShape),
                colors = RadioButtonDefaults.colors(selectedColor = MaterialTheme.colorScheme.scrim),
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
                        contentDescription = "remove milestone",
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                },
                textStyle = TextStyle(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onPrimary
                ),
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
                        text = stringResource(R.string.milestone_description_placeholder)
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
            text = fieldTitle,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onPrimary
        )
        Spacer(Modifier.size(8.dp))
        TextField(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .clickable { onTextFieldClick() }
                .fillMaxWidth()
                .border(
                    BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceTint),
                    shape = RoundedCornerShape(12.dp)
                ),
            enabled = enabled,
            value = value,
            readOnly = readOnly,
            minLines = minLines,
            shape = RoundedCornerShape(12.dp),
            onValueChange = onValueChange,
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
            placeholder = {
                Text(
                    text = placeholderText,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            },
            trailingIcon = {
                trailingIconId?.let {
                    Icon(
                        painter = painterResource(it),
                        contentDescription = "icon",
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            },
        )
    }
}

@Composable
fun CreateAndEditGoalScreenTopBar(
    modifier: Modifier = Modifier,
    openReason: OpenReason,
    title: String,
    onCompleteGoalClick: () -> Unit,
    onFinished: () -> Unit
) {
    CenterAlignedTopAppBar(
        modifier = modifier,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background
        ),
        title = {
            Text(
                text = when (openReason) {
                    OpenReason.CREATE -> {
                        stringResource(R.string.new_goal)
                    }

                    OpenReason.EDIT -> {
                        title
                    }
                },
                color = MaterialTheme.colorScheme.onPrimary
            )
        },
        navigationIcon = {
            Icon(
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .clip(CircleShape)
                    .clickable {
                        onFinished()
                    },
                painter = painterResource(R.drawable.ic_arrow_back),
                contentDescription = "go back",
                tint = MaterialTheme.colorScheme.onPrimary
            )
        },
        actions = {
            Icon(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .clip(CircleShape)
                    .clickable {
                        onCompleteGoalClick()
                    },
                painter = painterResource(R.drawable.ic_task_app),
                contentDescription = "more options",
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }
    )
}

@Composable
fun Photo(
    modifier: Modifier = Modifier,
    coverUri: Uri?,
    onAddPhotoClick: () -> Unit,
    onDeleteImageClick: () -> Unit,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color.Gray.copy(alpha = 0.5f))
            .heightIn(min = 200.dp)
            .clickable {
                onAddPhotoClick()
            }
    ) {
        if (coverUri == null) {
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
                        contentDescription = "add picture",
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
                Text(
                    color = MaterialTheme.colorScheme.outline,
                    text = stringResource(R.string.add_cover_photo),
                    fontWeight = FontWeight.SemiBold
                )
            }
        } else {
            AsyncImage(
                model = coverUri,
                contentDescription = "cover of goal",
                contentScale = ContentScale.FillWidth
            )
            Icon(
                modifier = Modifier
                    .clip(CircleShape)
                    .align(Alignment.TopEnd)
                    .padding(top = 16.dp, end = 16.dp)
                    .clickable {
                        onDeleteImageClick()
                    },
                painter = painterResource(R.drawable.ic_close),
                tint = Color.Black,
                contentDescription = "delete cover"
            )
        }

    }
}

@Composable
fun FilterChips(
    modifier: Modifier = Modifier,
    stateGoalCategory: GoalCategory,
    onChipClick: (GoalCategory) -> Unit,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        GoalCategory.defaultCategories.forEach { goalCategory ->
            FilterChip(
                selected = stateGoalCategory == goalCategory,
                label = {
                    Text(
                        modifier = Modifier.padding(all = 8.dp),
                        text = goalCategory.name,
                    )
                },
                shape = CircleShape,
                onClick = {
                    onChipClick(goalCategory)
                },
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
fun DatePickerFields(
    modifier: Modifier = Modifier,
    startDate: Long,
    endDate: Long,
    onStartDatePick: (Long) -> Unit,
    onEndDatePick: (Long) -> Unit,
) {
    var isStartDatePickerEnabled by remember { mutableStateOf(false) }
    var isEndDatePickerEnabled by remember { mutableStateOf(false) }
    Row(
        modifier = modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        CreateGoalTextFieldWithTitle(
            modifier = Modifier
                .weight(1f),
            fieldTitle = stringResource(R.string.start_date_field),
            readOnly = true,
            value = DateFormatter.formatDate(startDate),
            enabled = false,
            onTextFieldClick = {
                isStartDatePickerEnabled = true
            },
            trailingIconId = R.drawable.ic_calendar_today,
            placeholderText = "Start",
        ) {
        }
        CreateGoalTextFieldWithTitle(
            modifier = Modifier
                .weight(1f),
            fieldTitle = stringResource(R.string.end_date_field),
            readOnly = true,
            enabled = false,
            value = DateFormatter.formatDate(endDate),
            onTextFieldClick = {
                isEndDatePickerEnabled = true
            },
            placeholderText = "End",
            trailingIconId = R.drawable.ic_calendar
        ) {
        }
        if (isStartDatePickerEnabled) {
            DatePickerModal(
                onDateSelected = { date ->
                    date?.let {
                        onStartDatePick(it)
                        isStartDatePickerEnabled = false
                    }
                }
            ) {
                isStartDatePickerEnabled = false
            }
        }
        if (isEndDatePickerEnabled) {
            DatePickerModal(
                onDateSelected = { date ->
                    date?.let {
                        onEndDatePick(it)
                        isEndDatePickerEnabled = false
                    }
                }
            ) {
                isEndDatePickerEnabled = false
            }
        }
    }
}

@Composable
fun AddMilestoneButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Button(
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.tertiaryFixedDim,
            contentColor = MaterialTheme.colorScheme.scrim
        ),
        onClick = {
            onClick()
        }
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_add),
            contentDescription = "add milestone"
        )
        Spacer(Modifier.size(8.dp))
        Text(
            text = stringResource(R.string.add_milestone_button),
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun CreateOrEditButton(
    modifier: Modifier = Modifier,
    isSaveButtonEnabled: Boolean,
    text: String,
    content: @Composable () -> Unit = {},
    onClick: () -> Unit
) {
    Button(
        modifier = modifier
            .fillMaxWidth(),
        enabled = isSaveButtonEnabled,
        shape = RoundedCornerShape(20.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.tertiary,
            contentColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        onClick = {
            onClick()
        }
    ) {
        Text(
            modifier = Modifier.padding(vertical = 16.dp),
            text = text,
            fontSize = 18.sp
        )
        content()
    }
}

@Composable
fun AppTextField(
    modifier: Modifier = Modifier,
    placeholderText: String,
    leadingIconId: Int,
    value: String,
) {
    TextField(
        modifier = modifier
            .border(
                BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceTint),
                shape = RoundedCornerShape(12.dp)
            ),
        shape = RoundedCornerShape(12.dp),
        enabled = false,
        onValueChange = {},
        value = value,
        colors = TextFieldDefaults.colors(
            disabledContainerColor = MaterialTheme.colorScheme.onPrimaryContainer,
            disabledIndicatorColor = Color.Transparent,
            focusedContainerColor = MaterialTheme.colorScheme.onPrimaryContainer,
            unfocusedContainerColor = MaterialTheme.colorScheme.onPrimaryContainer,
            unfocusedTextColor = MaterialTheme.colorScheme.onPrimary,
            focusedTextColor = MaterialTheme.colorScheme.onPrimary,
            unfocusedIndicatorColor = Color.Transparent,
            focusedIndicatorColor = MaterialTheme.colorScheme.onPrimary,
            disabledTextColor = MaterialTheme.colorScheme.onPrimary,
        ),
        placeholder = {
            Text(
                text = placeholderText,
                color = MaterialTheme.colorScheme.onPrimary
            )
        },
        leadingIcon = {
            Icon(
                painter = painterResource(leadingIconId),
                contentDescription = "set time",
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }
    )
}

@Composable
fun CompleteDialog(
    onComplete: () -> Unit,
    onDismiss: () -> Unit,
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
                Icon(
                    modifier = Modifier.size(64.dp),
                    painter = painterResource(R.drawable.ic_task_app),
                    contentDescription = "complete goal dialog",
                    tint = MaterialTheme.colorScheme.onPrimary
                )

                Spacer(Modifier.size(24.dp))

                Text(
                    text = stringResource(R.string.complete_goal_dialog),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onPrimary
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
                        text = stringResource(R.string.complete_goal_cancel_button),
                        color = MaterialTheme.colorScheme.outline
                    )
                }
                Spacer(Modifier.size(8.dp))
                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(bottomStart = 12.dp, bottomEnd = 12.dp),
                    onClick = onComplete,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.scrim.copy(alpha = 0.4f),
                    )

                ) {
                    Text(
                        modifier = Modifier.padding(vertical = 4.dp),
                        text = stringResource(R.string.complete_goal_complete_button),
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }
        }
    }
}