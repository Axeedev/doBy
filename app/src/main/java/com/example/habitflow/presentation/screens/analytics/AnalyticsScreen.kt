@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.habitflow.presentation.screens.analytics

import android.graphics.Paint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.habitflow.R
import java.time.LocalDate
import java.util.Locale

@Composable
fun AnalyticsScreen(
    viewModel: AnalyticsViewModel = hiltViewModel(),
    onBackClick: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
                title = {
                    Text(
                        modifier = Modifier
                            .padding(start = 16.dp),
                        text = "Your Activity",
                        fontWeight = FontWeight.Bold
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
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)

            ) {
                ElevatedCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp),
                    colors = CardDefaults.elevatedCardColors(
                        contentColor = Color.Unspecified,
                        containerColor = Color.White
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(all = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = "Tasks overall",
                            color = Color.Gray,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = state.completedTasksOverall.toString(),
                            fontWeight = FontWeight.Bold,
                            fontSize = 28.sp
                        )
                        val textColor = if (state.percentageDiffPastWeek < 0) Color.Red else Color(0xFF10B981).copy(alpha = 0.6f)
                        val text = if (state.percentageDiffPastWeek > 0) "+" else ""
                        Text(
                            text = "$text${state.percentageDiffPastWeek}% past 7 days",
                            fontWeight = FontWeight.Medium,
                            color = textColor
                        )
                    }
                }
                ElevatedCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp),
                    colors = CardDefaults.elevatedCardColors(
                        contentColor = Color.Unspecified,
                        containerColor = Color.White
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(all = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = "This week",
                            color = Color.Gray,
                        )
                        Text(
                            text = state.completedTasksThisWeek.toString(),
                            fontWeight = FontWeight.Bold,
                            fontSize = 28.sp
                        )
                    }
                }
            }
            Spacer(Modifier.size(24.dp))

            WeeklyBarChart(
                data = state.dailyStats
            )

        }
    }
}


@Composable
fun WeeklyBarChart(
    data: List<Pair<LocalDate, Int>>,
    modifier: Modifier = Modifier
) {
    val maxValue = (data.maxOfOrNull { it.second } ?: 0).coerceAtLeast(1)
    val numberOfGridLines = maxValue / 2 + 1

    ElevatedCard(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = Color.White),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 0.5.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, top = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                text = "Tasks completed"
            )
        }
        var screenWidth by remember { mutableFloatStateOf(0f) }
        var contentWidth by remember { mutableFloatStateOf(0f) }

        var barWidth by remember { mutableFloatStateOf(0f) }

        val visibleCount = 7
        var scrolledBy by remember { mutableFloatStateOf(0f) }
        var initialized by remember { mutableStateOf(false) }

        val transformableState = rememberTransformableState { _, panChange, _ ->

            val minScroll = -contentWidth + screenWidth
            val maxScroll = 0f

            scrolledBy = (scrolledBy + panChange.x)
                .coerceIn(minScroll, maxScroll)
        }


        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 32.dp)
                .height(220.dp)
                .transformable(transformableState)
        ) {
            val barSpacing = 16.dp.toPx()

            screenWidth = size.width
            contentWidth = barSpacing + data.size * (barWidth + barSpacing)
            if (!initialized && contentWidth > size.width) {
                scrolledBy = size.width - contentWidth
                initialized = true
            }


            val availableWidth = size.width - barSpacing * (visibleCount + 1)
            barWidth = availableWidth / visibleCount

            val chartHeight = size.height * 0.75f
            val bottomPadding = size.height * 0.2f


            val paintText = Paint().apply {
                color = android.graphics.Color.GRAY
                textSize = 36f
                textAlign = Paint.Align.CENTER
                isAntiAlias = true
            }

            translate(left = scrolledBy) {
                for (i in 0..numberOfGridLines) {
                    val y = chartHeight - (i / numberOfGridLines.toFloat()) * chartHeight
                    drawLine(
                        color = Color(0xFFF1F5F9),
                        start = Offset(0f, y),
                        end = Offset(size.width, y),
                        strokeWidth = 1.dp.toPx()
                    )
                }

                data.forEachIndexed { index, (date, value) ->

                    val barHeight = if (value == 0) 10f else  value / maxValue.toFloat() * chartHeight

                    val left = barSpacing + index * (barWidth + barSpacing)
                    val top = chartHeight - barHeight


                    drawRoundRect(
                        color = Color(0xFF10B981),
                        topLeft = Offset(left, top),
                        size = Size(barWidth, barHeight),
                        cornerRadius = CornerRadius(20f, 20f)
                    )
                    drawContext.canvas.nativeCanvas.drawText(
                        value.toString(),
                        left + barWidth / 2f,
                        top - 8.dp.toPx(),
                        paintText
                    )

                    drawContext.canvas.nativeCanvas.apply {
                        val paint = Paint().apply {
                            color = android.graphics.Color.GRAY
                            textSize = 28f
                            textAlign = Paint.Align.CENTER
                            isAntiAlias = true
                        }

                        val dayLabel = String.format(
                            locale = Locale.getDefault(),
                            "%02d",
                            date.dayOfMonth
                        )
                        drawText(
                            dayLabel,
                            left + barWidth / 2f,
                            chartHeight + bottomPadding / 2,
                            paint
                        )
                    }
                }
            }
        }
    }
}
