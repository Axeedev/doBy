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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
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
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                ),
                title = {
                    Text(
                        modifier = Modifier
                            .padding(start = 16.dp),
                        text = stringResource(R.string.achievements_screen),
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary
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
                        contentDescription = "go back",
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            )
        },
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = paddingValues,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                CurrentStreakCard(streak = state.currentStreak.toString())
            }
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
                                    command = AchievementsCommand.ChangeFilterType(
                                        filterChipType = filterType
                                    )
                                )
                            },
                            label = {
                                Text(
                                    text = stringResource(filterType.titleId)
                                )
                            },
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceTint),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.onSecondaryFixedVariant,
                                selectedLabelColor = Color.White,
                                disabledLabelColor = Color(0xFF4C5A6D),
                                disabledContainerColor = Color.White,
                                containerColor = MaterialTheme.colorScheme.onSecondaryFixed
                            )
                        )
                    }

                }
            }
            if (state.achievements.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = 40.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Icon(
                            modifier = Modifier
                                .size(120.dp),
                            painter = painterResource(R.drawable.ic_achievements),
                            contentDescription = "no achievements",
                            tint = MaterialTheme.colorScheme.surfaceBright
                        )
                        Spacer(Modifier.size(16.dp))
                        Text(
                            text = stringResource(R.string.no_achievements_in_this_section),
                            color = MaterialTheme.colorScheme.onPrimaryFixed,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 18.sp
                        )

                    }
                }
            } else {
                items(
                    items = state.achievements,
                    key = { it.id }
                ) { achievement ->
                    AchievementCard(achievement = achievement)
                }
            }
        }
    }
}

@Composable
fun CurrentStreakCard(
    modifier: Modifier = Modifier,
    streak: String
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primary,
        ),
        border = BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.surfaceTint,
        ),
        shape = RoundedCornerShape(12.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(all = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .weight(7f)
                    .fillMaxHeight()
            ) {
                Text(
                    text = stringResource(R.string.current_streak),
                    color = MaterialTheme.colorScheme.surfaceBright,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(Modifier.size(16.dp))
                Text(
                    text = streak,
                    fontSize = 30.sp,
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontWeight = FontWeight.ExtraBold
                )
                Spacer(Modifier.size(16.dp))
                Text(
                    text = stringResource(R.string.current_streak_description),
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Medium
                )
            }

            Icon(
                modifier = Modifier
                    .size(80.dp)
                    .weight(3f),
                painter = painterResource(R.drawable.ic_fire),
                contentDescription = "current streak",
                tint = Color(0xFFF97316)
            )
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
                shape = RoundedCornerShape(20.dp),
                spotColor = Color.Gray.copy(alpha = 0.6f)
            ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.onPrimaryContainer
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceTint),
        shape = RoundedCornerShape(12.dp),
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

                AchievementType.GOALS_COMPLETED -> {
                    AchievementColorElement(
                        primaryColor = Color(0xFF4ED07E),
                        secondaryColor = Color(0xFFF3FDF6)
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
                        text = stringResource(achievement.titleId),
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Spacer(Modifier.size(8.dp))

                    Text(
                        text = stringResource(achievement.descriptionId),
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
                    text = if (achievement.progressPercentage != 100) "${achievement.progressPercentage}%" else "Completed",
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
                    achievement.progressPercentage.toFloat() / 100f
                }

            )
        }
    }
}
