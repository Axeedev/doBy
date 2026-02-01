package com.example.habitflow.presentation.screens.settings

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.habitflow.R
import com.example.habitflow.domain.entities.SendNotificationBeforeDeadline
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    settingsViewModel: SettingsViewModel = hiltViewModel(),
    onBackClick: () -> Unit
) {
    val state by settingsViewModel.state.collectAsState()
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { permission ->
        settingsViewModel.processCommand(SettingsCommand.ChangeNotificationsEnabled(permission))
    }


    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var isBottomSheetOpen by remember { mutableStateOf(false) }

    if (isBottomSheetOpen) {
        ModalBottomSheet(
            onDismissRequest = {
                isBottomSheetOpen = false
            },
            sheetState = sheetState,
            containerColor = Color(0xFFF7F8FA),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                Row {
                    Text(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .clickable {
                                scope.launch {
                                    sheetState.hide()
                                }
                            },
                        text = "Back",
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(Modifier.weight(1f))

                    Text(
                        text = "Notify before",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(Modifier.weight(1f))

                    Text(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .clickable {
                                settingsViewModel.processCommand(SettingsCommand.ChangeNotifyBefore(state.notifyBeforeMinutes))
                                scope.launch {
                                    sheetState.hide()
                                }
                            },
                        text = "Done",
                        fontWeight = FontWeight.SemiBold
                    )
                }

                val listMinutes = SendNotificationBeforeDeadline.entries.toList()

                LazyColumn(
                    modifier = Modifier.padding(top = 24.dp)
                ){
                    itemsIndexed(listMinutes) { index, item ->
                        NotifyBeforeItem(
                            minutes = item.beforeMinutes,
                            isSelected = state.selectedIndex == index,
                            shape = when (index) {
                                listMinutes.lastIndex -> RoundedCornerShape(bottomEnd = 12.dp, bottomStart = 12.dp)
                                0 -> RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)
                                else -> RoundedCornerShape(0)
                            }
                        ){
                            settingsViewModel.processCommand(SettingsCommand.ClickNotifyItem(index))
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
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
                title = {
                    Text(
                        text = "Settings"
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
                    text = "Data sync",
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Gray
                )

                Spacer(Modifier.size(8.dp))

                SettingsField(
                    mainText = "Wi-Fi only",
                    secondaryText = "Sync data only via Wi-fi",
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
                    text = "Notifications",
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Gray
                )
                Spacer(Modifier.size(8.dp))
                SettingsField(
                    mainText = "Enable notifications",
                    shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)
                ) {
                    Switch(
                        colors = SwitchDefaults.colors(checkedTrackColor = Color(0xFF10B981)),
                        checked = state.notificationsEnabled,
                        onCheckedChange = {
                            if (it && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
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
                        .clickable {
                            isBottomSheetOpen = true
                        },
                    mainText = "Notify before",
                    secondaryText = "Remind about deadline",
                    shape = RoundedCornerShape(bottomStart = 12.dp, bottomEnd = 12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${state.notifyBeforeMinutes} minutes",
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

        }
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
            .fillMaxWidth(),
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
fun NotifyBeforeItem(
    minutes: Int,
    isSelected: Boolean,
    shape: Shape,
    onSelectedChange: () -> Unit
) {

    Card(
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
                text = "$minutes minutes"
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