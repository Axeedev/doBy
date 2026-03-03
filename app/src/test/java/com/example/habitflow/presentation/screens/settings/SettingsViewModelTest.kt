package com.example.habitflow.presentation.screens.settings

import com.example.habitflow.domain.entities.settings.AppSettings
import com.example.habitflow.domain.entities.settings.SendNotificationBeforeDeadline
import com.example.habitflow.domain.usecases.settings.GetSettingsUseCase
import com.example.habitflow.domain.usecases.settings.UpdateIsDarkThemeUseCase
import com.example.habitflow.domain.usecases.settings.UpdateMorningTimeInfoUseCase
import com.example.habitflow.domain.usecases.settings.UpdateNightTimeInfoUseCase
import com.example.habitflow.domain.usecases.settings.UpdateNotificationsEnabledUseCase
import com.example.habitflow.domain.usecases.settings.UpdateNotifyBeforeUseCase
import com.example.habitflow.domain.usecases.settings.UpdateShowCalendarEventsUseCase
import com.example.habitflow.domain.usecases.settings.UpdateWifiOnlyUseCase
import com.google.firebase.auth.FirebaseAuth
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.equals.shouldBeEqual
import io.kotest.matchers.shouldBe
import io.mockk.coVerify
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsViewModelTest : BehaviorSpec({

//    isolationMode = IsolationMode.InstancePerLeaf

    val testDispatcher = StandardTestDispatcher()

    val getSettingsUseCase = mockk<GetSettingsUseCase>()

    val updateNotifyBeforeUseCase = mockk<UpdateNotifyBeforeUseCase>(relaxed = true)
    val updateWifiOnlyUseCase = mockk<UpdateWifiOnlyUseCase>(relaxed = true)
    val updateNotificationsEnabledUseCase = mockk<UpdateNotificationsEnabledUseCase>(relaxed = true)
    val updateShowCompletedTasksUseCase = mockk<UpdateShowCompletedTasksUseCase>(relaxed = true)
    val updateMorningTimeInfoUseCase = mockk<UpdateMorningTimeInfoUseCase>(relaxed = true)
    val updateNightTimeInfoUseCase = mockk<UpdateNightTimeInfoUseCase>(relaxed = true)
    val showCalendarEventsUseCase = mockk<UpdateShowCalendarEventsUseCase>(relaxed = true)
    val updateIsDarkThemeUseCase = mockk<UpdateIsDarkThemeUseCase>(relaxed = true)
    val firebaseAuth = mockk<FirebaseAuth>(relaxed = true)


    lateinit var viewModel: SettingsViewModel

    beforeSpec {
        Dispatchers.setMain(testDispatcher)
    }

    afterSpec {
        Dispatchers.resetMain()
    }

    beforeEach {
        val fakeSettings = AppSettings()

        every { getSettingsUseCase() } returns flowOf(fakeSettings)

        viewModel = SettingsViewModel(
            getSettingsUseCase, updateNotifyBeforeUseCase, updateWifiOnlyUseCase,
            updateNotificationsEnabledUseCase, updateShowCompletedTasksUseCase,
            updateMorningTimeInfoUseCase, updateNightTimeInfoUseCase,
            showCalendarEventsUseCase, updateIsDarkThemeUseCase, firebaseAuth
        )
    }

    Given("SettingsViewModel initialized") {

        When("init") {
            testDispatcher.scheduler.advanceUntilIdle()

            Then("state should contain data from getSettingsUseCase") {
                val state = viewModel.state.value
                state.wifiOnly shouldBe false
                state.notificationsEnabled shouldBe false
                state.isDarkTheme shouldBe false
            }
        }

        When("BottomSheet open") {
            val sheetType = BottomSheetType.NotifyBefore
            viewModel.processCommand(SettingsCommand.OpenSheet(sheetType))
            print(sheetType)
            testDispatcher.scheduler.advanceUntilIdle()

            Then("state should contain exact sheet type") {
                viewModel.state.value.bottomSheetType?.shouldBe(sheetType)
            }
        }

        When("BottomSheet close") {
            viewModel.processCommand(SettingsCommand.CloseSheet)
            testDispatcher.scheduler.advanceUntilIdle()

            Then("sheet state should be null") {
                viewModel.state.value.bottomSheetType shouldBe null
            }
        }

        When("wifi only change") {
            viewModel.processCommand(SettingsCommand.ChangeWifiOnly(true))
            testDispatcher.scheduler.advanceUntilIdle()

            Then("should invoke updateWifiOnlyUseCase(true)") {
                coVerify(exactly = 1) { updateWifiOnlyUseCase(true) }
            }
        }

        When("Click sign out") {
            viewModel.processCommand(SettingsCommand.SignOut)
            testDispatcher.scheduler.advanceUntilIdle()

            Then("isSignedOut should be true and firebaseAuth.signOut invoke") {
                verify(exactly = 1) { firebaseAuth.signOut() }
            }
        }

        When("dark theme set") {
            viewModel.processCommand(SettingsCommand.ClickChangeTheme(true))
            testDispatcher.scheduler.advanceUntilIdle()

            Then("should invoke updateIsDarkThemeUseCase") {
                coVerify(exactly = 1) { updateIsDarkThemeUseCase(true) }
            }
        }
    }
})