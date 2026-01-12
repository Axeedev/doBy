package com.example.habitflow.presentation.screens.tasks.creation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
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
                navigationIcon = {
                    Icon(
                        modifier = Modifier.clip(CircleShape)
                            .clickable{
                                onBackClick()
                            },
                        painter = painterResource(R.drawable.ic_arrow_back),
                        contentDescription = "Go back"
                    )
                },
                title = {
                    Text(
                        text = "Create a Task"
                    )
                }
            )
        }
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
                .padding(horizontal = 24.dp)
        ) {
            Text(
                text = "Title"
            )
            CreateTaskTextField(
                value = state.title
            ) {
                viewModel.processCommand(CreateTaskViewModel.CreateTaskCommand.InputTitle(it))
            }

            Text(
                text = "Date"
            )

            CreateTaskTextField(
                value = state.date
            ) {
                viewModel.processCommand(CreateTaskViewModel.CreateTaskCommand.InputDate(it))
            }

            Row() {
                Column() {
                    Text(
                        text = "Start Time"
                    )
                    Text(
                        text = "01:22 pm"
                    )
                }
                Column() {
                    Text(
                        text = "End Time"
                    )
                    Text(
                        text = "03:20 pm"
                    )
                }
                HorizontalDivider()
            }
            Text(
                text = "Description"
            )
            TextField(
                value = state.description,
                {
                    viewModel.processCommand(CreateTaskViewModel.CreateTaskCommand.InputDescription(it))
                }
            )
            HorizontalDivider()
            Text(
                text = "Category"
            )
            Row{
                Category.entries.forEach { category ->
                    FilterChip(
                        selected = state.category == category,
                        label = {
                            Text(
                                text = category.title
                            )
                        },
                        onClick = {
                            viewModel.processCommand(CreateTaskViewModel.CreateTaskCommand.ChangeCategory(category))
                        }
                    )
                }
            }
            Button(
                {}
            ) {
                Text("Create Task")
            }
        }
    }
}

@Composable
fun CreateTaskTextField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit
){
    TextField(
        modifier = modifier,
        value = value,
        onValueChange = onValueChange
    )
}