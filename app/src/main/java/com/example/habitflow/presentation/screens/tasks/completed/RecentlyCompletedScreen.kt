@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.habitflow.presentation.screens.tasks.completed

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.habitflow.R
import com.example.habitflow.domain.entities.tasks.CompletedTask
import com.example.habitflow.presentation.utils.DateFormatter
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun RecentlyCompletedScreen(
    viewModel: CompletedTasksViewModel = hiltViewModel(),
    onBackClick: () -> Unit
){
    val state by viewModel.state.collectAsState()

    Scaffold(
        containerColor = Color.White,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White),
                title = {
                    Text(
                        text = stringResource(R.string.my_tasks_screen)
                    )
                },
                navigationIcon = {
                    Icon(
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .clip(CircleShape)
                            .clickable { onBackClick() },
                        painter = painterResource(R.drawable.ic_arrow_back),
                        contentDescription = "go back"
                    )
                }
            )
        }
    ) {paddingValues ->
        LazyColumn(
            modifier = Modifier.padding(horizontal = 16.dp),
            contentPadding = paddingValues,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            itemsIndexed(items = state.completedTasks){index, completedTask ->
                CompletedTaskCard(
                    completedTask = completedTask
                ){
                    viewModel.processCommand(CompletedTasksCommand.ClickReturnTask(completedTask.id))
                }
                if (index != state.completedTasks.lastIndex){
                    HorizontalDivider(
                        modifier = Modifier.padding(top = 8.dp),
                        thickness = 0.8.dp
                    )
                }
            }
        }
    }
}

@Composable
fun CompletedTaskCard(
    modifier: Modifier = Modifier,
    completedTask: CompletedTask,
    onRadioButtonClick: () -> Unit
){
    Card(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable {}
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.White,
            contentColor = Color.Unspecified.copy(alpha = 0.4f)
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
                selected = completedTask.isCompleted,
                onClick = onRadioButtonClick,
                colors = RadioButtonDefaults.colors(
                    selectedColor = Color(0xFF10B981)
                )
            )
            Column {
                Text(
                    textDecoration = TextDecoration.LineThrough,
                    text = completedTask.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(Color(0XFFF1F5F9))
                    ) {
                        Text(
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
                            text = stringResource(completedTask.category.titleId),
                            color = Color.Black.copy(alpha = 0.4f),
                            fontSize = 14.sp
                        )
                    }
                    Text(
                        text = DateFormatter.formatDate(completedTask.completionDate),
                    )
                    completedTask.deadlineMillis?.let { deadline ->

                        val formatter = DateTimeFormatter.ofPattern("HH:mm")
                        val time = Instant.ofEpochMilli(deadline)
                            .atZone(ZoneId.systemDefault())
                            .format(formatter)
                        Text(
                            text = time,
                            fontWeight = FontWeight.W400,
                            color = Color(0XFF94A3B8)
                        )
                    }

                }
            }
        }
    }
}