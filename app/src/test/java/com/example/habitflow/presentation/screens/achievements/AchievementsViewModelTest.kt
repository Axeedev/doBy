package com.example.habitflow.presentation.screens.achievements

import com.example.habitflow.domain.entities.achievements.Achievement
import com.example.habitflow.domain.usecases.achievements.GetAchievementsUseCase
import com.example.habitflow.domain.usecases.achievements.GetCurrentStreakUseCase
import com.example.habitflow.domain.usecases.achievements.GetLockedAchievementsUseCase
import com.example.habitflow.domain.usecases.achievements.GetUnlockedAchievementsUseCase
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*

@OptIn(ExperimentalCoroutinesApi::class)
class AchievementsViewModelTest : BehaviorSpec({

    isolationMode = IsolationMode.InstancePerLeaf
    val testDispatcher = StandardTestDispatcher()

    beforeSpec {
        Dispatchers.setMain(testDispatcher)
    }

    afterSpec {
        Dispatchers.resetMain()
    }

    Given("AchievementsViewModel initialized") {
        val getAchievementsUseCase = mockk<GetAchievementsUseCase>()
        val getLockedAchievementsUseCase = mockk<GetLockedAchievementsUseCase>()
        val getUnlockedAchievementsUseCase = mockk<GetUnlockedAchievementsUseCase>()
        val getCurrentStreakUseCase = mockk<GetCurrentStreakUseCase>()

        val allAchievements = listOf(mockk<Achievement>(), mockk<Achievement>())
        val lockedAchievements = listOf(mockk<Achievement>())
        val unlockedAchievements = listOf(mockk<Achievement>())
        val streakValue = 5

        every { getAchievementsUseCase() } returns flowOf(allAchievements)
        every { getLockedAchievementsUseCase() } returns flowOf(lockedAchievements)
        every { getUnlockedAchievementsUseCase() } returns flowOf(unlockedAchievements)
        every { getCurrentStreakUseCase() } returns flowOf(streakValue)

        val viewModel = AchievementsViewModel(
            getAchievementsUseCase,
            getLockedAchievementsUseCase,
            getUnlockedAchievementsUseCase,
            getCurrentStreakUseCase
        )

        When("init block call") {
            testDispatcher.scheduler.advanceUntilIdle()

            Then("state should contain initial values") {
                val state = viewModel.state.value

                state.achievements shouldBe lockedAchievements
                state.currentStreak shouldBe streakValue
                state.selectedType shouldBe FilterChipType.IN_PROGRESS
            }
        }

        When("change filter type on 'completed'") {
            testDispatcher.scheduler.advanceUntilIdle()

            viewModel.processCommand(AchievementsCommand.ChangeFilterType(FilterChipType.COMPLETED))
            testDispatcher.scheduler.advanceUntilIdle()

            Then("state should contain updated selected type and a new list of achievements") {
                val state = viewModel.state.value
                state.selectedType shouldBe FilterChipType.COMPLETED
                state.achievements shouldBe unlockedAchievements
            }
        }

        When("change filter type on 'all'") {
            viewModel.processCommand(AchievementsCommand.ChangeFilterType(FilterChipType.ALL))
            testDispatcher.scheduler.advanceUntilIdle()

            Then("state should contain updated selected type and a new list of achievements") {
                val state = viewModel.state.value
                state.selectedType shouldBe FilterChipType.ALL
                state.achievements shouldBe allAchievements
            }
        }
    }
})