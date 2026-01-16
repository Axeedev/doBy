package com.example.habitflow.presentation.screens.tasks

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.habitflow.R
import com.example.habitflow.domain.entities.Task

@Composable
fun TasksScreen(
    tasksViewModel: TasksViewModel = hiltViewModel(),
    onAddTaskClick: () -> Unit
) {
    val state by tasksViewModel.state.collectAsState()
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    onAddTaskClick()
                }
            ) { }
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Text(
                modifier = Modifier
                    .padding(start = 24.dp),
                text = "Hello Rohan!",
                color = MaterialTheme.colorScheme.secondary,
                fontWeight = FontWeight.Medium,
                fontSize = 30.sp
            )
            Spacer(Modifier.size(8.dp))
            Text(
                modifier = Modifier
                    .padding(start = 24.dp),
                text = "Have a nice day.",
                color = MaterialTheme.colorScheme.tertiary,
                fontWeight = FontWeight.Normal,
                fontSize = 16.sp
            )
            Spacer(Modifier.size(32.dp))
            Text(
                modifier = Modifier
                    .padding(start = 28.dp),
                text = "Pinned",
                color = MaterialTheme.colorScheme.secondary,
                fontWeight = FontWeight.Medium,
                fontSize = 22.sp
            )

            Spacer(Modifier.size(24.dp))
            LazyRow(
                contentPadding = PaddingValues(horizontal = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(items = state.todayTasks, key = { it.id }) { task ->
                    PinnedTaskCard(
                        task = task,
                        backgroundColor = MaterialTheme.colorScheme.primary
                    )
                }
            }
            Spacer(Modifier.size(32.dp))
            LazyColumn(
                modifier = Modifier
                    .padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Text(
                        text = "Today",
                        color = MaterialTheme.colorScheme.secondary,
                        fontWeight = FontWeight.Medium,
                        fontSize = 22.sp
                    )
                }
                items(state.todayTasks, key = { it.id }) {
                    TaskCard(task = it)
                }
            }
        }
    }

}

@Composable
fun PinnedTaskCard(
    modifier: Modifier = Modifier,
    backgroundColor: Color,
    task: Task
) {
    Column(
        modifier = modifier
            .widthIn(min = 200.dp, max = 260.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .drawBehind {
                drawCircle(
                    color = Color(0xFF4A3BCF),
                    center = Offset(x = this.size.width, y = 0f),
                    radius = size.minDimension / 2.5f
                )
                drawCircle(
                    color = Color(0xFF4A3BCF),
                    center = Offset(x = 0f, y = this.size.height / 3)
                )
            },
    ) {
        Row(
            Modifier
                .padding(vertical = 24.dp, horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        )
        {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.5f))
            ) {
                Icon(
                    modifier = Modifier.padding(all = 8.dp),
                    painter = painterResource(R.drawable.ic_idea),
                    contentDescription = "idea",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
            Text(
                modifier = Modifier
                    .padding(start = 16.dp),
                text = task.title,
                color = MaterialTheme.colorScheme.onPrimary,
                fontSize = 16.sp
            )
        }
        Spacer(Modifier.size(8.dp))
        Text(
            modifier = Modifier.padding(horizontal = 16.dp),
            text = task.title,
            maxLines = 3,
            fontWeight = FontWeight.SemiBold,
            overflow = TextOverflow.Ellipsis,
            fontSize = 24.sp,
            color = MaterialTheme.colorScheme.onPrimary
        )
        Spacer(Modifier.size(24.dp))
        Text(
            modifier = Modifier.padding(
                start = 16.dp,
                bottom = 24.dp
            ),
            color = MaterialTheme.colorScheme.onPrimary,
            text = task.date
        )
    }
}

@Composable
fun TaskCard(
    modifier: Modifier = Modifier,
    task: Task
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.onPrimary)
            .fillMaxWidth()
    ) {
        Row(
            Modifier
                .padding(vertical = 24.dp, horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                Color(0xFF9C2CF3), Color(0xFF3A49F9)
                            )
                        )
                    )
                    .weight(1f)
            ) {
                Icon(
                    modifier = Modifier
                        .padding(all = 24.dp),
                    painter = painterResource(R.drawable.ic_task),
                    contentDescription = "Task card"
                )
            }
            Column(
                modifier = Modifier
                    .padding(start = 32.dp)
                    .weight(5f)
            ) {

                Text(
                    text = task.title,
                    color = MaterialTheme.colorScheme.primaryFixedDim,
                    fontWeight = FontWeight.Medium,
                    fontSize = 24.sp
                )

                Spacer(Modifier.size(8.dp))

                Text(
                    text = task.date,
                    color = MaterialTheme.colorScheme.tertiary
                )

            }

        }

    }
}