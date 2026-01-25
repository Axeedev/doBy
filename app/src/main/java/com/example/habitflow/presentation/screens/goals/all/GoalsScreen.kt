@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.habitflow.presentation.screens.goals.all

import android.util.Log
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
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
import androidx.core.net.toUri
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.example.habitflow.R
import com.example.habitflow.domain.entities.Goal
import com.example.habitflow.presentation.utils.DateFormatter


@Composable
fun GoalsScreen(
    viewModel: GoalsViewModel = hiltViewModel(),
    onEditGoalClick: (Int) -> Unit,
    onBackClick: () -> Unit,
    onCreateButtonClick: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                Icon(
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .clickable{
                            onBackClick()
                        },
                    painter = painterResource(R.drawable.ic_arrow_back),
                    contentDescription = "go back"
                )
            },
                title = {
                    Text(
                        text = "My goals",
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFF3F4F6)
                )
            )
        },

        floatingActionButton = {
            FloatingActionButton(
                onClick = onCreateButtonClick,
                containerColor = MaterialTheme.colorScheme.primaryFixedDim,
                contentColor = Color.White
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_add),
                    contentDescription = "add goal"
                )

            }
        },
        containerColor = Color(0xFFF3F4F6)
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = paddingValues
        ) {
            item {
                Text(
                    text = "${state.goals.size} active goals",
                    color = MaterialTheme.colorScheme.onPrimaryFixedVariant
                )
            }
            item {
                Text(
                    text = "Active goals",
                    color = MaterialTheme.colorScheme.onPrimaryFixedVariant
                )
            }
            items(items = state.goals, key = { it.id }) { goal ->
                GoalCard(
                    goal = goal,
                    onDeleteClick = {
                        viewModel.processCommand(GoalsCommand.DeleteCommand(goal.id))
                    }
                ) {
                    onEditGoalClick(goal.id)
                }
            }
        }
    }
}

@Composable
fun GoalCard(
    goal: Goal,
    onDeleteClick:() -> Unit,
    onGoalClick: () -> Unit
) {
    ElevatedCard(
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.onPrimary,
        ),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 1.dp)
    ) {
        Log.d("GoalCard", goal.toString())

        goal.coverUri?.let { uri ->
            AsyncImage(
                modifier = Modifier
                    .heightIn(min = 150.dp, max = 150.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp)),
                model = uri.toUri(),
                contentScale = ContentScale.FillWidth,
                contentDescription = "cover image"
            )
        }
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFE6FFF6))
                        .weight(1f),
                    contentAlignment = Alignment.Center,

                    ) {
                    Row(
                        modifier = Modifier
                            .padding(horizontal = 8.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_book),
                            contentDescription = "goal category",
                            tint = Color(0xFF15803D)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = goal.category.title,
                            color = Color(0xFF15803D)
                        )
                    }
                }

                Icon(
                    painter = painterResource(R.drawable.ic_calendar),
                    contentDescription = "goal end date",
                    tint = MaterialTheme.colorScheme.onPrimaryFixedVariant
                )
                Spacer(Modifier.size(8.dp))
                Text(
                    text = DateFormatter.formatDate(goal.goalEndDate),
                    color = MaterialTheme.colorScheme.onPrimaryFixedVariant
                )
            }

            Spacer(modifier = Modifier.size(16.dp))
            Text(
                text = goal.title,
                fontWeight = FontWeight.SemiBold,
                fontSize = 20.sp,
                color = MaterialTheme.colorScheme.primaryFixed
            )
            Spacer(modifier = Modifier.size(16.dp))
            Text(
                text = goal.description,
                color = MaterialTheme.colorScheme.onPrimaryFixedVariant
            )
            Spacer(modifier = Modifier.size(16.dp))
            if (goal.milestones.isNotEmpty()) {
                val completed = goal.milestones.count { it.isCompleted }
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Progress ${(completed.toFloat()/goal.milestones.size.toFloat())*100} %",
                            color = MaterialTheme.colorScheme.onPrimaryFixedVariant
                        )
                        Text(
                            text = "$completed of ${goal.milestones.size} steps",
                            color = MaterialTheme.colorScheme.onPrimaryFixedVariant
                        )
                    }
                }
                Spacer(modifier = Modifier.size(16.dp))
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    modifier = Modifier
                        .weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primaryFixedDim
                    ),
                    onClick = {
                        onGoalClick()
                    }
                ) {
                    Text(
                        modifier = Modifier
                            .padding(vertical = 4.dp),
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp,
                        text = "Upgrade Progress"
                    )
                }
                Box(
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.background)
                        .clickable {},
                    contentAlignment = Alignment.Center
                ) {
                    var isExpanded by remember { mutableStateOf(false) }
                    Box(
                        Modifier
                            .padding(12.dp)
                    ) {
                        Icon(
                            modifier = Modifier.clickable {
                                isExpanded = !isExpanded
                            },
                            painter = painterResource(R.drawable.ic_more_horiz),
                            contentDescription = "more options"
                        )
                        DropdownMenu(
                            expanded = isExpanded,
                            onDismissRequest = { isExpanded = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Delete") },
                                leadingIcon = {
                                    Icon(
                                        painter = painterResource(R.drawable.ic_delete),
                                        contentDescription = "delete goal"
                                    )
                                },
                                onClick = {
                                    onDeleteClick()
                                }
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.size(8.dp))
        }
    }
}