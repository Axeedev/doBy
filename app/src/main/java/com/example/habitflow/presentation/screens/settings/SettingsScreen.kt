@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.habitflow.presentation.screens.settings

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetState
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.habitflow.R
import com.example.habitflow.domain.entities.settings.AppSettings
import com.example.habitflow.domain.entities.settings.NotificationTime
import com.example.habitflow.domain.entities.settings.SendNotificationBeforeDeadline
import java.util.Locale


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    settingsViewModel: SettingsViewModel = hiltViewModel(),
    onSingOut: () -> Unit,
    onBackClick: () -> Unit
) {
    val state by settingsViewModel.state.collectAsState()
    if (state.isSignedOut) {
        LaunchedEffect(Unit) {
            onSingOut()
        }
    }

    val notificationsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { permission ->
        settingsViewModel.processCommand(SettingsCommand.ChangeNotificationsEnabled(permission))
    }
    val readCalendarLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { permission ->
        settingsViewModel.processCommand(SettingsCommand.ChangeShowCalendarEvents(permission))
    }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val sheetType = state.bottomSheetType
    if (sheetType != null) {
        BottomSheet(
            sheetState = sheetState,
            onDismissRequest = {
                settingsViewModel.processCommand(SettingsCommand.CloseSheet)

            },
            onBackClick = {
                settingsViewModel.processCommand(SettingsCommand.CloseSheet)
            },
            bottomSheetTitle = when (sheetType) {
                BottomSheetType.MorningTimeNotification -> {
                    stringResource(R.string.notifications_notify_at)
                }

                BottomSheetType.NightTimeNotification -> {
                    stringResource(R.string.notifications_notify_at)
                }

                BottomSheetType.NotifyBefore -> {
                    stringResource(R.string.notifications_notify_before)
                }
            },
            onDoneClick = {
                when (sheetType) {
                    BottomSheetType.MorningTimeNotification -> {
                        settingsViewModel.processCommand(
                            SettingsCommand.ChangeMorningTimeInfo(
                                AppSettings.morningInfoTimeItems[state.selectedMorningTimeIndex]
                            )
                        )
                    }

                    BottomSheetType.NightTimeNotification -> {
                        settingsViewModel.processCommand(
                            SettingsCommand.ChangeNightTimeInfo(
                                AppSettings.nightInfoTimeItems[state.selectedNightTimeIndex]
                            )
                        )
                    }

                    BottomSheetType.NotifyBefore -> {
                        settingsViewModel.processCommand(
                            SettingsCommand.ChangeNotifyBefore(
                                state.notifyBeforeMinutes
                            )
                        )
                    }
                }
                settingsViewModel.processCommand(SettingsCommand.CloseSheet)
            },
        ) {

            when (sheetType) {
                BottomSheetType.MorningTimeNotification -> {
                    TimeList(
                        modifier = Modifier.padding(top = 24.dp),
                        list = AppSettings.morningInfoTimeItems,
                        selectedIndex = state.selectedMorningTimeIndex
                    ) {
                        settingsViewModel.processCommand(SettingsCommand.ClickMorningTimeItem(it))
                    }
                }

                BottomSheetType.NightTimeNotification -> {
                    TimeList(
                        modifier = Modifier.padding(top = 24.dp),
                        list = AppSettings.nightInfoTimeItems,
                        selectedIndex = state.selectedNightTimeIndex
                    ) {
                        settingsViewModel.processCommand(SettingsCommand.ClickNightTimeItem(it))
                    }
                }

                BottomSheetType.NotifyBefore -> {
                    val listMinutes = SendNotificationBeforeDeadline.entries.toList()
                    LazyColumn(
                        modifier = Modifier.padding(top = 24.dp)
                    ) {
                        itemsIndexed(
                            listMinutes, key = { _, item ->
                                item.beforeMinutes
                            }
                        ) { index, item ->
                            NotifyBeforeItem(
                                minutes = item.beforeMinutes,
                                isSelected = state.selectedNotifyBeforeIndex == index,
                                shape = when (index) {
                                    listMinutes.lastIndex -> RoundedCornerShape(
                                        bottomEnd = 12.dp,
                                        bottomStart = 12.dp
                                    )

                                    0 -> RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)
                                    else -> RoundedCornerShape(0)
                                }
                            ) {
                                settingsViewModel.processCommand(
                                    SettingsCommand.ClickNotifyItem(
                                        index = index
                                    )
                                )
                            }
                        }
                    }

                }
            }


        }
    }

    Scaffold(
        containerColor = Color(0xFFF7F8FA),
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(containerColor =  Color(0xFFF7F8FA)),
                title = {
                    Text(
                        text = stringResource(R.string.settings_screen),
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    Icon(
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .clip(CircleShape)
                            .clickable {
                                onBackClick()
                            },
                        painter = painterResource(R.drawable.ic_arrow_back),
                        contentDescription = "Go back"
                    )
                }
            )
        },
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.padding(horizontal = 16.dp),
            contentPadding = paddingValues
        ) {
            item {
                Text(
                    text = stringResource(R.string.data_sync_field),
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Gray
                )
                Spacer(Modifier.size(8.dp))
                SettingsField(
                    mainText = stringResource(R.string.data_sync_wifi_only),
                    secondaryText = stringResource(R.string.data_sync_sync_data_only_via_wifi),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Switch(
                        colors = SwitchDefaults.colors(checkedTrackColor = Color(0xFF10B981)),
                        checked = state.wifiOnly,
                        onCheckedChange = {
                            settingsViewModel.processCommand(SettingsCommand.ChangeWifiOnly(it))
                        }
                    )
                }
                Spacer(Modifier.size(24.dp))
            }

            item {
                Text(
                    text = stringResource(R.string.completed_tasks_field),
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Gray
                )

                Spacer(Modifier.size(8.dp))

                SettingsField(
                    mainText = stringResource(R.string.recently_completed_screen),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Switch(
                        colors = SwitchDefaults.colors(checkedTrackColor = Color(0xFF10B981)),
                        checked = state.showCompletedTasks,
                        onCheckedChange = {
                            settingsViewModel.processCommand(
                                SettingsCommand.ChangeShowCompletedTasks(
                                    it
                                )
                            )
                        }
                    )
                }
                Spacer(Modifier.size(24.dp))

            }
            item {
                Text(
                    text = stringResource(R.string.sync_with_calendar),
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Gray
                )

                Spacer(Modifier.size(8.dp))

                SettingsField(
                    mainText = stringResource(R.string.show_events_from_calendar),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Switch(
                        colors = SwitchDefaults.colors(checkedTrackColor = Color(0xFF10B981)),
                        checked = state.showEventsFromCalendar,
                        onCheckedChange = {
                            if (it) {
                                readCalendarLauncher.launch(Manifest.permission.READ_CALENDAR)
                            } else {
                                settingsViewModel.processCommand(
                                    SettingsCommand.ChangeShowCalendarEvents(
                                        it
                                    )
                                )
                            }
                        }
                    )
                }
                Spacer(Modifier.size(24.dp))

            }

            item {

                Text(
                    text = stringResource(R.string.notifications_field),
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Gray
                )
                Spacer(Modifier.size(8.dp))
                SettingsField(
                    mainText = stringResource(R.string.notifications_enable),
                    shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)
                ) {
                    Switch(
                        colors = SwitchDefaults.colors(checkedTrackColor = Color(0xFF10B981)),
                        checked = state.notificationsEnabled,
                        onCheckedChange = {
                            if (it && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                notificationsLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                            } else {
                                settingsViewModel.processCommand(
                                    SettingsCommand.ChangeNotificationsEnabled(
                                        it
                                    )
                                )
                            }
                        }
                    )
                }
            }

            item {
                SettingsField(
                    modifier = Modifier
                        .clip(RoundedCornerShape(bottomStart = 12.dp, bottomEnd = 12.dp))
                        .clickable {
                            settingsViewModel.processCommand(
                                SettingsCommand.OpenSheet(
                                    sheetType = BottomSheetType.NotifyBefore
                                )
                            )
                        },
                    mainText = stringResource(R.string.notifications_notify_before),
                    secondaryText = stringResource(R.string.notifications_remind_about_deadline),
                    shape = RoundedCornerShape(bottomStart = 12.dp, bottomEnd = 12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${state.notifyBeforeMinutes} ${stringResource(R.string.number_of_minutes)}",
                            color = Color.Gray
                        )
                        Icon(
                            modifier = Modifier.clip(CircleShape),
                            painter = painterResource(R.drawable.ic_arrow_right),
                            contentDescription = "Open notify before settings",
                            tint = Color.Gray
                        )
                    }
                }

                Spacer(Modifier.size(24.dp))

            }
            item {
                Text(
                    text = stringResource(R.string.information_for_today_field),
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Gray
                )

                Spacer(Modifier.size(8.dp))

                SettingsField(
                    modifier = Modifier
                        .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
                        .clickable {
                            settingsViewModel.processCommand(
                                SettingsCommand.OpenSheet(
                                    sheetType = BottomSheetType.MorningTimeNotification
                                )
                            )
                        },
                    mainText = stringResource(R.string.information_for_today_morning),
                    shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = state.morningTimeFormatted,
                            color = Color.Gray
                        )
                        Icon(
                            modifier = Modifier.clip(CircleShape),
                            painter = painterResource(R.drawable.ic_arrow_right),
                            contentDescription = "Open notify before settings",
                            tint = Color.Gray
                        )
                    }
                }
                SettingsField(
                    modifier = Modifier
                        .clip(RoundedCornerShape(bottomStart = 12.dp, bottomEnd = 12.dp))
                        .clickable {
                            settingsViewModel.processCommand(
                                SettingsCommand.OpenSheet(
                                    sheetType = BottomSheetType.NightTimeNotification
                                )
                            )
                        },
                    mainText = stringResource(R.string.information_for_today_night),
                    shape = RoundedCornerShape(bottomStart = 12.dp, bottomEnd = 12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = state.nightTimeFormatted,
                            color = Color.Gray
                        )
                        Icon(
                            modifier = Modifier.clip(CircleShape),
                            painter = painterResource(R.drawable.ic_arrow_right),
                            contentDescription = "Open notify before settings",
                            tint = Color.Gray
                        )
                    }
                }
            }
            item {
                Spacer(Modifier.size(16.dp))
                SignOutButton(
                    modifier = Modifier
                        .border(1.dp, Color.Red, RoundedCornerShape(12.dp))
                        .fillMaxWidth()
                ) {
                    settingsViewModel.processCommand(SettingsCommand.SignOut)
                }
            }
        }
    }
}

@Composable
fun SignOutButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Button(
        modifier = modifier,
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),

        colors = ButtonDefaults.buttonColors(
            containerColor = Color.White,
            contentColor = Color.Red
        )
    ) {
        Text(text = stringResource(R.string.sign_out))
    }
}


@Composable
fun SettingsField(
    modifier: Modifier = Modifier,
    mainText: String,
    shape: Shape,
    secondaryText: String = "",
    secondaryContent: @Composable () -> Unit,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape),
        shape = shape,
        colors = CardDefaults.cardColors(
            containerColor = Color.White,
            contentColor = Color.Unspecified
        ),
        border = BorderStroke(1.dp, Color(0XFFEBEBEB))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = mainText,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 18.sp
                )
                if (secondaryText.isNotEmpty()) {
                    Spacer(Modifier.size(8.dp))
                    Text(
                        text = secondaryText,
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
            }
            secondaryContent()

        }
    }
}

@Composable
fun BottomSheet(
    modifier: Modifier = Modifier,
    sheetState: SheetState,
    bottomSheetTitle: String,
    onDismissRequest: () -> Unit,
    onBackClick: () -> Unit,
    onDoneClick: () -> Unit,
    content: @Composable () -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        containerColor = Color(0xFFF7F8FA),
    ) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            Row {
                Text(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .clickable(
                            indication = null,
                            interactionSource = interactionSource
                        ) {
                            onBackClick()
                        },
                    text = stringResource(R.string.notifications_back),
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(Modifier.weight(1f))

                Text(
                    text = bottomSheetTitle,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(Modifier.weight(1f))

                Text(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .clickable(
                            indication = null,
                            interactionSource = interactionSource
                        ) {
                            onDoneClick()
                        },
                    text = stringResource(R.string.notifications_done),
                    fontWeight = FontWeight.SemiBold
                )
            }
            content()
        }
    }
}

@Composable
fun NotifyBeforeItem(
    modifier: Modifier = Modifier,
    minutes: Int,
    isSelected: Boolean,
    shape: Shape,
    onSelectedChange: () -> Unit
) {

    Card(
        modifier = modifier,
        shape = shape,
        border = BorderStroke(1.dp, Color(0XFFEBEBEB))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Text(
                text = "$minutes ${stringResource(R.string.number_of_minutes)}"
            )

            RadioButton(
                colors = RadioButtonDefaults.colors(
                    selectedColor = Color(0xFF10B981)
                ),
                selected = isSelected,
                onClick = onSelectedChange
            )
        }
    }
}


@Composable
fun TimeItem(
    modifier: Modifier = Modifier,
    isSelected: Boolean,
    notificationTime: NotificationTime,
    shape: Shape,
    onSelectedChange: () -> Unit,
) {
    Card(
        modifier = modifier,
        shape = shape,
        border = BorderStroke(1.dp, Color(0XFFEBEBEB))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Text(
                text = String.format(
                    Locale.getDefault(),
                    "%02d:%02d",
                    notificationTime.hour,
                    notificationTime.minute
                ),
            )

            RadioButton(
                colors = RadioButtonDefaults.colors(
                    selectedColor = Color(0xFF10B981)
                ),
                selected = isSelected,
                onClick = onSelectedChange
            )
        }
    }
}

@Composable
fun TimeList(
    modifier: Modifier = Modifier,
    list: List<NotificationTime>,
    selectedIndex: Int,
    onClick: (Int) -> Unit,
) {
    LazyColumn(
        modifier = modifier
    ) {
        itemsIndexed(
            list,
            key = { _, time ->
                time.hour
            }
        ) { index, time ->
            TimeItem(
                isSelected = selectedIndex == index,
                notificationTime = time,
                shape = when (index) {
                    list.lastIndex -> {
                        RoundedCornerShape(bottomEnd = 12.dp, bottomStart = 12.dp)

                    }

                    0 -> {
                        RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)
                    }

                    else -> {
                        RoundedCornerShape(0)
                    }
                }
            ) {
                onClick(index)
            }
        }
    }
}