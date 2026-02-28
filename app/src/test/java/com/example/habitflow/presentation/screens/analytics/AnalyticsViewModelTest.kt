package com.example.habitflow.presentation.screens.analytics

import com.example.habitflow.domain.usecases.analytics.GetCountOfCompletedTasksForWeekUseCase
import com.example.habitflow.domain.usecases.analytics.GetDailyStatsUseCase
import com.example.habitflow.domain.usecases.analytics.GetWeeklyDifferencePercentageUseCase
import com.example.habitflow.domain.usecases.tasks.GetNumberOfCompletedTasksUseCase
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId

@OptIn(ExperimentalCoroutinesApi::class)
class AnalyticsViewModelTest : BehaviorSpec({

    isolationMode = IsolationMode.InstancePerLeaf
    val testDispatcher = StandardTestDispatcher()

    beforeSpec {
        Dispatchers.setMain(testDispatcher)
    }

    afterSpec {
        Dispatchers.resetMain()
    }

    Given("AnalyticsViewModel initialized") {
        val getDailyStatsUseCase = mockk<GetDailyStatsUseCase>()
        val getNumberOfCompletedTasksUseCase = mockk<GetNumberOfCompletedTasksUseCase>()
        val getWeeklyDifferencePercentageUseCase = mockk<GetWeeklyDifferencePercentageUseCase>()
        val getCountOfCompletedTasksForWeekUseCase = mockk<GetCountOfCompletedTasksForWeekUseCase>()
        val overallCompleted = 100
        val weeklyDiff = 15
        val weeklyCount = 20

        every { getNumberOfCompletedTasksUseCase() } returns flowOf(overallCompleted)
        every { getWeeklyDifferencePercentageUseCase() } returns flowOf(weeklyDiff)
        every { getCountOfCompletedTasksForWeekUseCase() } returns flowOf(weeklyCount)

        val viewModel = AnalyticsViewModel(
            getDailyStatsUseCase,
            getNumberOfCompletedTasksUseCase,
            getWeeklyDifferencePercentageUseCase,
            getCountOfCompletedTasksForWeekUseCase
        )

        When("init block call") {
            testDispatcher.scheduler.advanceUntilIdle()

            Then("state should contain data from flows") {
                val state = viewModel.state.value

                state.completedTasksOverall shouldBe overallCompleted
                state.percentageDiffPastWeek shouldBe weeklyDiff
                state.completedTasksThisWeek shouldBe weeklyCount

                state.dailyStats.isNotEmpty() shouldBe true
                state.dailyStats.first().second shouldBe 0
            }

            Then("state should contain correct max value") {
                val state = viewModel.state.value
                state.maxValue shouldBe 1
            }
        }

        When("change type of chart") {
            testDispatcher.scheduler.advanceUntilIdle()

            val newChartType = ChartType.BAR_CHART
            viewModel.processCommand(AnalyticsCommand.SwitchSelectedChartType(newChartType))
            testDispatcher.scheduler.runCurrent()

            Then("should update selectedChartType") {
                viewModel.state.value.selectedChartType shouldBe newChartType
            }
        }
    }
})