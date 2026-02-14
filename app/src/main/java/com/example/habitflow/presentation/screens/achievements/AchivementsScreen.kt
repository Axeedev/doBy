@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.habitflow.presentation.screens.achievements

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.habitflow.R
import com.example.habitflow.domain.entities.achievements.Achievement
import com.example.habitflow.domain.entities.achievements.AchievementType

@Composable
fun AchievementsScreen(
    viewModel: AchievementsViewModel = hiltViewModel(),
    onBackClick: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                ),
                title = {
                    Text(
                        modifier = Modifier
                            .padding(start = 16.dp),
                        text = "Achievements"
                    )
                },
                navigationIcon = {
                    Icon(
                        modifier = Modifier
                            .padding(start = 16.dp)
                            .clip(CircleShape)
                            .clickable {
                                onBackClick()
                            },
                        painter = painterResource(R.drawable.ic_arrow_back),
                        contentDescription = "go back"
                    )
                }
            )
        },
        containerColor = Color.White
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = paddingValues,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    FilterChipType.entries.forEach { filterType ->
                        FilterChip(
                            shape = CircleShape,
                            selected = state.selectedType == filterType,
                            onClick = {
                                viewModel.processCommand(
                                    AchievementsCommand.ChangeFilterType(
                                        filterType
                                    )
                                )
                            },
                            label = {
                                Text(
                                    text = filterType.title
                                )
                            },
                            border = BorderStroke(1.dp, Color(0xFFE7ECF2)),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(0xFF0F172A),
                                selectedLabelColor = Color.White,
                                disabledLabelColor = Color(0xFF4C5A6D),
                                disabledContainerColor = Color.White
                            )
                        )
                    }

                }
            }
            items(
                items = state.achievements,
                key = { it.id }
            ) { achievement ->
                AchievementCard(achievement = achievement)
            }
        }
    }
}

@Composable
fun AchievementCard(
    modifier: Modifier = Modifier,
    achievement: Achievement
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(12.dp),
                spotColor = Color.Gray.copy(alpha = 0.6f)
            ),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            contentColor = Color.Unspecified,
            containerColor = Color.White
        ),
        border = BorderStroke(0.5.dp, Color(0xFFF3F7FA))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            val (primary, secondary) = when (achievement.achievementType) {
                AchievementType.TASKS_COMPLETED -> {
                    AchievementColorElement(
                        primaryColor = Color(0xFF4ED07E),
                        secondaryColor = Color(0xFFF3FDF6)
                    )
                }

                AchievementType.TIMING -> {
                    AchievementColorElement(
                        primaryColor = Color(0xFFF97316),
                        secondaryColor = Color(0xFFFFF7ED)
                    )
                }

                AchievementType.STREAK -> {
                    AchievementColorElement(
                        primaryColor = Color(0xFFF97316),
                        secondaryColor = Color(0xFFFFF7ED)
                    )
                }

                AchievementType.SETTING_GOALS -> {

                    AchievementColorElement(
                        primaryColor = Color(0xFFF97316),
                        secondaryColor = Color(0xFFFFF7ED)
                    )

                }

                AchievementType.PRODUCTIVITY -> {
                    AchievementColorElement(
                        primaryColor = Color(0xFFF97316),
                        secondaryColor = Color(0xFFFFF7ED)
                    )
                }
            }

            Row {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(color = secondary)
                ) {
                    Icon(
                        modifier = Modifier
                            .padding(all = 16.dp),
                        painter = painterResource(achievement.iconResId),
                        contentDescription = "achievement",
                        tint = primary
                    )
                }
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .padding(start = 16.dp),
                ) {
                    Text(
                        text = achievement.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp,
                        color = Color(0xFF181F32)
                    )
                    Spacer(Modifier.size(8.dp))

                    Text(
                        text = achievement.description,
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp,
                        color = Color(0xFF718095)
                    )

                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${achievement.currentScore}/${achievement.targetGoal}",
                    color = primary,
                    fontWeight = FontWeight.Medium
                )

                Text(
                    text = if (achievement.progress != 100) "${achievement.progress}%" else "Completed",
                    color = primary,
                    fontWeight = FontWeight.Medium
                )
            }
            LinearProgressIndicator(
                modifier = Modifier
                    .fillMaxWidth()
                    .size(6.dp),
                strokeCap = StrokeCap.Round,
                gapSize = 0.dp,
                color = Color(0xFF007AFF),
                trackColor = secondary,
                progress = {
                    achievement.progress.toFloat() / 100f
                }

            )
        }
    }
}
