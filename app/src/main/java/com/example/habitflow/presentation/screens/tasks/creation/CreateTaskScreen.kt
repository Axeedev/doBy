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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.habitflow.R
import com.example.habitflow.presentation.screens.tasks.Category

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateTaskScreen(
    viewModel: CreateTaskViewModel = hiltViewModel(),
    onBackClick: () -> Unit
){

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
                            .clickable{
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
                }
            )
        },
        containerColor = MaterialTheme.colorScheme.primary
    ) {paddingValues ->
        Column(
            modifier = Modifier.fillMaxSize()
                .padding(top = paddingValues.calculateTopPadding())
                .drawBehind{
                    drawCircle(
                        color = Color(0xFF4A3BCF),
                        center = Offset(x = this.size.width, y = 0f),
                        radius = size.minDimension / 2.5f
                    )
                    drawCircle(
                        color = Color(0xFF4A3BCF),
                        center = Offset(x = 0f, y = this.size.height/3)
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
                supportingText = "Code changes"
            ) {
                viewModel.processCommand(CreateTaskViewModel.CreateTaskCommand.InputTitle(it))
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
            CreateTaskTextField(
                modifier = Modifier
                    .padding(horizontal = 8.dp),
                value = state.date,
                supportingText = "Oct 24, 2020"
            ) {
                viewModel.processCommand(CreateTaskViewModel.CreateTaskCommand.InputDate(it))
            }
            HorizontalDivider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 8.dp),
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Spacer(Modifier.size(16.dp))

            LazyColumn (
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer),
            ) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 24.dp)
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Start Time",
                                fontSize = 20.sp,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                            Spacer(Modifier.size(12.dp))
                            Text(
                                text = "01:22 pm",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "End Time",
                                fontSize = 20.sp,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                            Spacer(Modifier.size(12.dp))
                            Text(
                                text = "03:20 pm",
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
                                CreateTaskViewModel.CreateTaskCommand.InputDescription(
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
                        modifier = Modifier.fillMaxWidth()
                            .padding(horizontal = 24.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Category.entries.forEach { category ->
                            FilterChip(
                                modifier = Modifier
                                    .heightIn(min = 64.dp),
                                shape = RoundedCornerShape(50),
                                border = null,
                                selected = state.category == category,
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
                                        CreateTaskViewModel.CreateTaskCommand.ChangeCategory(
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
                        onClick = {}
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
    supportingText: String,
    onValueChange: (String) -> Unit
){
    TextField(
        colors = TextFieldDefaults
            .colors(
                unfocusedContainerColor = Color.Transparent,
                focusedContainerColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
        placeholder = {
            Text(
                fontSize = 25.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.scrim,
                text = supportingText
            )
        },
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