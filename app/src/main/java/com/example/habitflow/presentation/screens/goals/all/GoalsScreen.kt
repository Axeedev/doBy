@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.habitflow.presentation.screens.goals.all

import android.util.Log
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.example.habitflow.R
import com.example.habitflow.domain.entities.goals.Goal
import com.example.habitflow.presentation.utils.DateFormatter
import kotlin.math.roundToInt


@Composable
fun GoalsScreen(
    viewModel: GoalsViewModel = hiltViewModel(),
    onEditGoalClick: (Int) -> Unit,
    onBackClick: () -> Unit,
    onCreateButtonClick: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    Scaffold(
        containerColor = Color(0xFFF7F8FA),
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
                navigationIcon = {
                Icon(
                    painter = painterResource(R.drawable.ic_arrow_back),
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .clip(CircleShape)
                        .clickable {
                            onBackClick()
                        },
                    contentDescription = "go back"
                )
            },
                title = {
                    Text(
                        text = stringResource(R.string.goals_screen),
                        fontWeight = FontWeight.Bold
                    )
                },
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
                    text = pluralStringResource(
                        R.plurals.goals_count,
                        state.goals.size,
                        state.goals.size
                    ),
                    color = MaterialTheme.colorScheme.onPrimaryFixedVariant
                )
            }
            item {
                Text(
                    text = stringResource(R.string.active_goals),
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
                    .clip(RoundedCornerShape(topEnd = 12.dp, topStart = 12.dp)),
                model = uri.toUri(),
                contentScale = ContentScale.FillWidth,
                contentDescription = "cover image"
            )
        }
        Column(modifier = Modifier.padding(16.dp)) {
//            Row(
//                modifier = Modifier.fillMaxWidth(),
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                Box(
//                    modifier = Modifier
//                        .clip(RoundedCornerShape(12.dp))
//                        .background(Color(0xFFE6FFF6))
//                        .weight(1f),
//                    contentAlignment = Alignment.Center,
//
//                    ) {
//                    Row(
//                        modifier = Modifier
//                            .padding(horizontal = 8.dp, vertical = 8.dp),
//                        verticalAlignment = Alignment.CenterVertically
//                    ) {
//                        Icon(
//                            painter = painterResource(R.drawable.ic_book),
//                            contentDescription = "goal category",
//                            tint = Color(0xFF15803D)
//                        )
//                        Spacer(modifier = Modifier.width(8.dp))
//                        Text(
//                            text = goal.category.title,
//                            color = Color(0xFF15803D)
//                        )
//                    }
//                }


//            }

            Text(
                text = goal.title,
                fontWeight = FontWeight.SemiBold,
                fontSize = 20.sp,
                color = MaterialTheme.colorScheme.primaryFixed
            )
            Spacer(modifier = Modifier.size(16.dp))
            Row {
                Icon(
                    painter = painterResource(R.drawable.ic_calendar),
                    contentDescription = "goal end date",
                    tint = MaterialTheme.colorScheme.onPrimaryFixedVariant
                )
                Spacer(Modifier.size(8.dp))
                Text(
                    text = "${stringResource(R.string.until)} ${DateFormatter.formatDate(goal.goalEndDate)}",
                    color = MaterialTheme.colorScheme.onPrimaryFixedVariant
                )
            }
            Spacer(Modifier.size(16.dp))
            if (goal.milestones.isNotEmpty()) {
                val completed = goal.milestones.count { it.isCompleted }
                val progress = (completed.toFloat()/goal.milestones.size.toFloat())
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "${stringResource(R.string.progress)} ${(progress * 100).roundToInt()} %",
                            color = MaterialTheme.colorScheme.onPrimaryFixedVariant
                        )
                        Text(
                            text = "$completed of ${goal.milestones.size} steps",
                            color = MaterialTheme.colorScheme.onPrimaryFixedVariant
                        )
                    }
                    LinearProgressIndicator(
                        progress = { progress }
                    )
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
                        containerColor = Color(0xFFF7F8FA)
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
                        color = MaterialTheme.colorScheme.secondary,
                        text = stringResource(R.string.update_progress)
                    )
                }
            }
            Spacer(modifier = Modifier.size(8.dp))
        }
    }
}