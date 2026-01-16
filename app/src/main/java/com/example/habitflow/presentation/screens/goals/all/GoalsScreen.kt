@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.habitflow.presentation.screens.goals.all

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.habitflow.R
import com.example.habitflow.domain.entities.Goal
import com.example.habitflow.domain.entities.GoalCategory
import com.example.habitflow.domain.entities.Milestone

val items = buildList {
    repeat(10) {
        add(
            Goal(
                it,
                category = GoalCategory.EDUCATION,
                title = "Выучить испанский язык",
                description = "Достичь уровня B2 к лету. Заниматься каждый день по 30 минут в приложении и с репетитором",
                goalStartDate = "",
                goalEndDate = "до 15 июня",
                milestones = listOf(Milestone(0, "")),
            )
        )
    }
}


@Composable
fun GoalsScreen(
    viewModel: GoalsViewModel = hiltViewModel(),
    onCreateButtonClick: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    Scaffold(
        topBar = {
            TopAppBar(
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
                onClick = onCreateButtonClick
            ) {
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
            items(items = items, key = { it.id }) { goal ->
                GoalCard(goal)
            }
        }
    }
}

@Composable
fun GoalCard(goal: Goal) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.onPrimary,
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ){
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFDCFCE7)),
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
                    modifier = Modifier.clip(CircleShape),
                    painter = painterResource(R.drawable.ic_more_horiz),
                    tint = MaterialTheme.colorScheme.onPrimaryFixedVariant,
                    contentDescription = "more"
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
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column() {
                        Text(
                            text = "Прогресс 35 %",
                            color = MaterialTheme.colorScheme.onPrimaryFixedVariant
                        )
                        Text(
                            text = "2 из 8 шагов",
                            color = MaterialTheme.colorScheme.onPrimaryFixedVariant
                        )
                    }
                }
                Spacer(modifier = Modifier.size(16.dp))
            }
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_calendar),
                    contentDescription = "goal end date",
                    tint = MaterialTheme.colorScheme.onPrimaryFixedVariant
                )
                Spacer(Modifier.size(8.dp))
                Text(
                    text = goal.goalEndDate,
                    color = MaterialTheme.colorScheme.onPrimaryFixedVariant
                )
            }
        }
    }
}