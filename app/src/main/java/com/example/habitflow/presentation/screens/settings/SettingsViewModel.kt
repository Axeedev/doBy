package com.example.habitflow.presentation.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.habitflow.domain.entities.settings.AppSettings
import com.example.habitflow.domain.entities.settings.SendNotificationBeforeDeadline
import com.example.habitflow.domain.usecases.settings.GetSettingsUseCase
import com.example.habitflow.domain.usecases.settings.UpdateMorningTimeInfoUseCase
import com.example.habitflow.domain.usecases.settings.UpdateNightTimeInfoUseCase
import com.example.habitflow.domain.usecases.settings.UpdateNotificationsEnabledUseCase
import com.example.habitflow.domain.usecases.settings.UpdateNotifyBeforeUseCase
import com.example.habitflow.domain.usecases.settings.UpdateShowCompletedTasksUseCase
import com.example.habitflow.domain.usecases.settings.UpdateWifiOnlyUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val getSettingsUseCase: GetSettingsUseCase,
    private val updateNotifyBeforeUseCase: UpdateNotifyBeforeUseCase,
    private val updateWifiOnlyUseCase: UpdateWifiOnlyUseCase,
    private val updateNotificationsEnabledUseCase: UpdateNotificationsEnabledUseCase,
    private val updateShowCompletedTasksUseCase: UpdateShowCompletedTasksUseCase,
    private val updateMorningTimeInfoUseCase: UpdateMorningTimeInfoUseCase,
    private val updateNightTimeInfoUseCase: UpdateNightTimeInfoUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(SettingsScreenState())
    val state
        get() = _state.asStateFlow()


    init {
        viewModelScope.launch {
            getSettingsUseCase()
                .collect { settings ->
                    _state.update { previous ->
                        previous.copy(
                            wifiOnly = settings.wifiOnly,
                            notificationsEnabled = settings.notificationsEnabled,
                            notifyBeforeMinutes = settings.sendNotificationBeforeDeadline.beforeMinutes,
                            showCompletedTasks = settings.showCompletedTasksOnMainScreen,
                            selectedNotifyBeforeIndex = SendNotificationBeforeDeadline.entries.indexOf(settings.sendNotificationBeforeDeadline),
                        )
                    }
                }
        }
    }

    fun processCommand(settingsCommand: SettingsCommand){
        viewModelScope.launch {
            when (settingsCommand) {
                is SettingsCommand.ChangeNotificationsEnabled -> {
                    updateNotificationsEnabledUseCase(settingsCommand.enabled)
                }

                is SettingsCommand.ChangeNotifyBefore -> {
                    updateNotifyBeforeUseCase(settingsCommand.newBefore)
                }

                is SettingsCommand.ChangeWifiOnly -> {
                    updateWifiOnlyUseCase(settingsCommand.isWifiOnly)
                }

                is SettingsCommand.ClickNotifyItem -> {
                    _state.update {
                        it.copy(
                            selectedNotifyBeforeIndex = settingsCommand.index,
                            notifyBeforeMinutes = SendNotificationBeforeDeadline.entries[settingsCommand.index].beforeMinutes
                        )
                    }
                }

                is SettingsCommand.ChangeShowCompletedTasks -> {
                    updateShowCompletedTasksUseCase(settingsCommand.shouldShow)
                }

                is SettingsCommand.OpenSheet -> {
                    _state.update {
                        it.copy(bottomSheetType = settingsCommand.sheetType)
                    }
                }

                SettingsCommand.CloseSheet -> {
                    _state.update { it.copy(bottomSheetType = null) }
                }

                is SettingsCommand.ClickMorningTimeItem -> {
                    _state.update {
                        it.copy(selectedMorningTimeIndex = settingsCommand.index)
                    }
                }
                is SettingsCommand.ClickNightTimeItem -> {
                    _state.update {
                        it.copy(selectedNightTimeIndex = settingsCommand.index)
                    }
                }

                is SettingsCommand.ChangeMorningTimeInfo -> {
                    updateMorningTimeInfoUseCase(settingsCommand.notificationTime)
                }
                is SettingsCommand.ChangeNightTimeInfo -> {
                    updateNightTimeInfoUseCase(settingsCommand.notificationTime)
                }
            }
        }
    }


}