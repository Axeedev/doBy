package com.example.habitflow.presentation.screens.tasks

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.habitflow.domain.entities.Task

@Composable
fun TasksScreen(
    tasksViewModel: TasksViewModel = hiltViewModel(),
    onAddTaskClick: () -> Unit
) {
    val state by tasksViewModel.state.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(all = 50.dp)
    ) {
        Button(
            modifier = Modifier
                .fillMaxWidth(),
            onClick = {
                onAddTaskClick()
            }
        ) {
            Text("Add Task")
        }
        Text(
            text = "Hello Rohan!"
        )
        Text(
            text = "Have a nice day."
        )
        Text(
            text = "Pinned"
        )

        LazyRow {
            items(state.todayTasks, key = { it.id }) { task ->
                PinnedTaskCard(
                    task = task
                )
            }
        }

        LazyColumn() {
            item {
                Text(
                    text = "Today"
                )
                Text(
                    text = "Tomorrow"
                )
                Text(
                    text = "Next week"
                )
                Text(
                    text = "Later"
                )
            }
        }
    }
}

@Composable
fun PinnedTaskCard(
    modifier: Modifier = Modifier,
    task: Task
) {
    Card(modifier = modifier) {
        Text(
            text = task.title
        )
        Text(
            text = task.note,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = task.date
        )
    }
}