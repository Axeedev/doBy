package com.example.habitflow.presentation.screens.tasks.completed

import com.example.habitflow.domain.entities.Category
import com.example.habitflow.domain.entities.tasks.CompletedTask
import com.example.habitflow.domain.entities.tasks.Priority
import com.example.habitflow.domain.usecases.tasks.GetCompletedTaskUseCase
import com.example.habitflow.domain.usecases.tasks.ReturnTaskUseCase
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*

@OptIn(ExperimentalCoroutinesApi::class)
class CompletedTasksViewModelTest : BehaviorSpec({

    val testDispatcher = StandardTestDispatcher()

    val getCompletedTaskUseCase = mockk<GetCompletedTaskUseCase>()
    val returnTaskUseCase = mockk<ReturnTaskUseCase>()

    lateinit var viewModel: CompletedTasksViewModel

    beforeSpec {
        Dispatchers.setMain(testDispatcher)
    }

    afterSpec {
        Dispatchers.resetMain()
    }

    val fakeTasks = listOf(
        CompletedTask(
            id = 1,
            title = "Task 1",
            deadlineMillis = null,
            note = "",
            category = Category("test"),
            priority = Priority.LOW,
            isCompleted = true,
            completionDate = System.currentTimeMillis(),
        ),
        CompletedTask(id = 2,
            title = "Task 2",
            deadlineMillis = null,
            note = "",
            category = Category("test2"),
            priority = Priority.LOW,
            isCompleted = true,
            completionDate = System.currentTimeMillis(),
            )
    )

    Given("CompletedTasksViewModel") {

        every { getCompletedTaskUseCase() } returns flowOf(fakeTasks)

        When("ViewModel initialized") {
            viewModel = CompletedTasksViewModel(getCompletedTaskUseCase, returnTaskUseCase)

            testDispatcher.scheduler.advanceUntilIdle()

            Then("state should contain fake tasks") {
                viewModel.state.value.completedTasks shouldBe fakeTasks
            }
        }

        When("ClickReturnTask") {
            val taskId = 1
            coEvery { returnTaskUseCase(any()) } returns Unit

            viewModel.processCommand(CompletedTasksCommand.ClickReturnTask(taskId))
            testDispatcher.scheduler.advanceUntilIdle()

            Then("should call with correct id") {
                coVerify(exactly = 1) { returnTaskUseCase(taskId) }
            }
        }
    }

    Given("Empty list") {
        every { getCompletedTaskUseCase() } returns flowOf(emptyList())

        When("ViewModel initialized") {
            viewModel = CompletedTasksViewModel(getCompletedTaskUseCase, returnTaskUseCase)
            testDispatcher.scheduler.advanceUntilIdle()

            Then("list in state should be empty") {
                viewModel.state.value.completedTasks.isEmpty() shouldBe true
            }
        }
    }
})