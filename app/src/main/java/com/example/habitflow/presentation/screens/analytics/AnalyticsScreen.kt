@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.habitflow.presentation.screens.analytics

import android.graphics.Paint
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.habitflow.R
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale
import kotlin.math.ceil
import kotlin.math.roundToInt

private val menuItems = listOf(
    MenuItem(
        chartType = ChartType.BAR_CHART,
        iconId = R.drawable.ic_bar_chart
    ),
    MenuItem(
        chartType = ChartType.HEATMAP,
        iconId = R.drawable.ic_heatmap
    )
)

private val intensityColors = listOf(
    Color.Gray.copy(alpha = 0.4f),
    Color(0xFF00ad00),
    Color(0xFF00d100),
    Color(0xFF00f000),
    Color(0xFFb3ffb3)
)

private const val VISIBLE_COUNT = 7


@Composable
fun AnalyticsScreen(
    viewModel: AnalyticsViewModel = hiltViewModel(),
    onBackClick: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        containerColor = Color(0xFFF7F8FA),
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFF7F8FA)),
                title = {
                    Text(
                        modifier = Modifier
                            .padding(start = 16.dp),
                        text = stringResource(R.string.analytics_screen),
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
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        contentColor = Color.Unspecified,
                        containerColor = Color.White
                    ),
                    border = BorderStroke(1.dp, Color(0XFFEBEBEB))
                ) {
                    Column(
                        modifier = Modifier.padding(all = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.analytics_tasks_overall),
                            color = Color.Gray,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = state.completedTasksOverall.toString(),
                            fontWeight = FontWeight.Bold,
                            fontSize = 28.sp
                        )
                        val currentPercentage = state.percentageDiffPastWeek

                        val (color, iconId) = when{
                            currentPercentage == 0 -> TrendItem(Color.Gray, R.drawable.ic_flat)
                            currentPercentage > 0 -> TrendItem(Color(0xFF10B981), R.drawable.ic_upwards)
                            else -> TrendItem(Color(0xFFdc143c), R.drawable.ic_downwards)
                        }

                        val text = if (state.percentageDiffPastWeek > 0) "+" else ""
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painter = painterResource(iconId),
                                contentDescription = "trend",
                                tint = color
                            )
                            Spacer(Modifier.size(8.dp))
                            Text(
                                text = "$text${state.percentageDiffPastWeek}% ${stringResource(R.string.analytics_past_7_days)}",
                                fontWeight = FontWeight.Medium,
                                color = color
                            )
                        }
                    }
                }
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        contentColor = Color.Unspecified,
                        containerColor = Color.White
                    ),
                    border = BorderStroke(1.dp, Color(0XFFEBEBEB))
                ) {
                    Column(
                        modifier = Modifier.padding(all = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.analytics_this_week),
                            color = Color.Gray,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = state.completedTasksThisWeek.toString(),
                            fontWeight = FontWeight.Bold,
                            fontSize = 28.sp
                        )
                        Text(
                            text = stringResource(R.string.analytics_tasks_completed),
                            color = Color.Gray,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
            Spacer(Modifier.size(24.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                var expanded by remember { mutableStateOf(false) }
                Column(
                    modifier = Modifier.weight(1f)
                ){
                    Text(
                        text = stringResource(state.selectedChartType.titleId) ,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                    if (state.selectedChartType == ChartType.HEATMAP) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.analytics_heat_map_less)
                            )
                            intensityColors.forEach { color ->
                                Box(
                                    modifier = Modifier
                                        .size(20.dp)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(color)
                                )
                            }
                            Text(
                                text = stringResource(R.string.analytics_heat_map_more)
                            )
                        }
                    }
                }

                Column(
                    modifier = Modifier
                ) {
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = {
                            expanded = false
                        },
                        containerColor = Color.Gray.copy(alpha = 0.887f),
                        shape = RoundedCornerShape(20.dp),
                        border = null,
                        shadowElevation = 0.dp,
                        tonalElevation = 0.dp
                    ) {
                        menuItems.forEach { item ->
                            DropdownMenuItem(
                                text = {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = stringResource(item.chartType.titleId),
                                            color = Color.White
                                        )
                                    }
                                },
                                leadingIcon = {
                                    Icon(
                                        painter = painterResource(item.iconId),
                                        contentDescription = "type of bar",
                                        tint = Color.White
                                    )

                                },
                                onClick = {
                                    viewModel.processCommand(
                                        AnalyticsCommand.SwitchSelectedChartType(
                                            item.chartType
                                        )
                                    )
                                    expanded = !expanded
                                }
                            )
                        }
                    }
                    Icon(
                        modifier = Modifier
                            .clip(CircleShape)
                            .clickable {
                                expanded = !expanded
                            },
                        painter = painterResource(R.drawable.ic_menu),
                        contentDescription = "open menu",
                        tint = Color.Black
                    )
                }
            }
            Spacer(Modifier.size(24.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth(),
            ) {
                when (state.selectedChartType) {
                    ChartType.BAR_CHART -> {
                        WeeklyBarChart(
                            modifier = Modifier
                                .padding(horizontal = 16.dp),
                            data = state.dailyStats,
                            maxValue = state.maxValue
                        )
                    }

                    ChartType.HEATMAP -> {
                        HeatmapGrid(
                            statsMap = state.statsMap,
                            months = state.months,
                        )
                    }
                }
            }

        }
    }
}

@Composable
fun WeeklyBarChart(
    data: List<Pair<LocalDate, Int>>,
    modifier: Modifier = Modifier,
    maxValue: Int,
) {
    val numberOfGridLines = maxValue / 2 + 1

    Card(
        modifier = modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White,
            contentColor = Color.Unspecified
        ),
        border = BorderStroke(1.dp, Color(0XFFEBEBEB))
    ) {
        var screenWidth by remember { mutableFloatStateOf(0f) }
        var contentWidth by remember { mutableFloatStateOf(0f) }

        var barWidth by remember { mutableFloatStateOf(0f) }

        var visibleCount by remember { mutableIntStateOf(VISIBLE_COUNT) }
        var scrolledBy by remember { mutableFloatStateOf(0f) }
        LaunchedEffect(contentWidth, screenWidth, data.size) {
            scrolledBy = if (contentWidth > screenWidth) {
                screenWidth - contentWidth
            } else {
                0f
            }
        }
        val transformableState = rememberTransformableState { zoomChange, panChange, _ ->

            visibleCount = (visibleCount / zoomChange)
                .roundToInt()
                .coerceIn(5, 10)

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

                    val barHeight =
                        if (value == 0) 10f else value / maxValue.toFloat() * chartHeight

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
                            color = android.graphics.Color.BLACK
                            textSize = 28f
                            textAlign = Paint.Align.CENTER
                            isAntiAlias = true
                        }

                        val dayLabel = String.format(
                            locale = Locale.getDefault(),
                            "%02d",
                            date.dayOfMonth
                        )
                        val monthLabel = date.month.getDisplayName(
                            TextStyle.SHORT,
                            Locale.getDefault()
                        )

                        drawText(
                            dayLabel,
                            left + barWidth / 2f,
                            chartHeight + bottomPadding / 2,
                            paint
                        )
                        drawText(
                            monthLabel,
                            left + barWidth / 2f,
                            chartHeight + bottomPadding,
                            paint
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun HeatmapGrid(
    modifier: Modifier = Modifier,
    statsMap: Map<LocalDate, Int>,
    months: List<YearMonth>

) {
    val listState = rememberLazyListState()
    LaunchedEffect(Unit) {
        listState.scrollToItem(months.lastIndex)
    }
    Card(
        modifier = modifier
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(
            contentColor = Color.Unspecified,
            containerColor = Color.White
        ),
        border = BorderStroke(1.dp, Color(0XFFEBEBEB))
    ) {
        LazyRow(
            modifier = Modifier
                .padding(vertical = 16.dp),
            state = listState,
            horizontalArrangement = Arrangement
                .spacedBy(32.dp),
            contentPadding = PaddingValues(horizontal = 16.dp)
        ) {
            items(months, key = { it.monthValue }) { month ->
                HeatMapMonth(
                    month = month,
                    data = statsMap
                )
            }
        }
    }
}

@Composable
fun HeatMapMonth(
    month: YearMonth,
    data: Map<LocalDate, Int>
) {

    val daysInMonth = month.lengthOfMonth()
    val firstDay = month.atDay(1)
    val firstDayOffset = firstDay.dayOfWeek.value - 1
    val today = LocalDate.now()

    val totalCells = firstDayOffset + daysInMonth
    val numberOfColumns = ceil(totalCells / 7f).toInt()

    Column(

        horizontalAlignment = Alignment.Start
    ) {
        Text(
            modifier = Modifier
                .padding(start = 8.dp, bottom = 8.dp),
            text = month.month.getDisplayName(
                TextStyle.SHORT,
                Locale.getDefault(),
            ),
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            repeat(numberOfColumns) { week ->
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    repeat(7) { row ->
                        val index = week * 7 + row
                        val dayNumber = index - firstDayOffset + 1

                        val date =
                            if (dayNumber in 1..daysInMonth)
                                firstDay.plusDays((dayNumber - 1).toLong())
                            else null

                        val count = date?.let { data[it] } ?: 0

                        val color = when {
                            date == null -> {
                                Color.Transparent
                            }

                            count == 0 -> {
                                Color.Gray.copy(alpha = 0.4f)
                            }

                            count == 1 -> {
                                Color(0xFF00ad00)
                            }


                            count == 2 -> {
                                Color(0xFF00d100)
                            }


                            count == 3 -> {
                                Color(0xFF00f000)
                            }



                            else -> {
                                Color(0xFFb3ffb3)
                            }
                        }

                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .background(
                                    color = color,
                                    shape = RoundedCornerShape(6.dp)
                                )
                        ) {
                            if (date == today) {
                                Box(
                                    modifier = Modifier
                                        .size(5.dp)
                                        .clip(CircleShape)
                                        .background(Color.Red)
                                        .align(Alignment.Center)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}