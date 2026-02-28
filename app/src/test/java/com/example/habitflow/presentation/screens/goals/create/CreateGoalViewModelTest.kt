package com.example.habitflow.presentation.screens.goals.create

import com.example.habitflow.domain.entities.Category
import com.example.habitflow.domain.entities.goals.Goal
import com.example.habitflow.domain.entities.goals.Milestone
import com.example.habitflow.domain.usecases.goals.AddGoalUseCase
import com.example.habitflow.domain.usecases.goals.CompleteGoalUseCase
import com.example.habitflow.domain.usecases.goals.GetGoalByIdUseCase
import com.example.habitflow.domain.usecases.goals.UpdateGoalUseCase
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain

@OptIn(ExperimentalCoroutinesApi::class)
class CreateGoalViewModelTest : BehaviorSpec({

    val dispatcher = StandardTestDispatcher()

    lateinit var addGoalUseCase: AddGoalUseCase
    lateinit var updateGoalUseCase: UpdateGoalUseCase
    lateinit var completeGoalUseCase: CompleteGoalUseCase
    lateinit var getGoalByIdUseCase: GetGoalByIdUseCase

    fun createViewModel(id: Int? = null) =
        CreateGoalViewModel(
            addGoalUseCase,
            updateGoalUseCase,
            completeGoalUseCase,
            getGoalByIdUseCase,
            id
        )

    beforeTest {

        addGoalUseCase = mockk(relaxed = true)
        updateGoalUseCase = mockk(relaxed = true)
        completeGoalUseCase = mockk(relaxed = true)
        getGoalByIdUseCase = mockk()
    }

    afterTest {
        Dispatchers.resetMain()
        clearAllMocks()
    }

    Given("ViewModel in create mode") {

        val viewModel = createViewModel()

        When("InputTitle is called") {

            Then("title should update") {
                viewModel.processCommand(
                    CreateGoalCommand.InputTitle("New Goal")
                )

                viewModel.state.value.title shouldBe "New Goal"
            }
        }

        When("ClickAddMilestone is called") {

            Then("milestone should be added") {
                viewModel.processCommand(
                    CreateGoalCommand.ClickAddMilestone
                )

                viewModel.state.value.milestones.size shouldBe 1
            }
        }

        When("ClickCreateGoal is called") {

            Then("addGoalUseCase should be invoked") {

                runTest {

                    Dispatchers.setMain(StandardTestDispatcher(testScheduler))
                    val viewModel = createViewModel()

                    viewModel.processCommand(
                        CreateGoalCommand.InputTitle("Goal")
                    )

                    viewModel.processCommand(
                        CreateGoalCommand.ClickCreateGoal
                    )

                    advanceUntilIdle()

                    coVerify {
                        addGoalUseCase(match { it.title == "Goal" })
                    }

                    Dispatchers.resetMain()
                }
            }
        }
    }

    Given("ViewModel in edit mode with id") {

        val goal = Goal(
            id = 5,
            title = "Existing",
            description = "Desc",
            category = Category.defaultCategories.first(),
            goalStartDate = 1L,
            goalEndDate = 2L,
            milestones = listOf(
                Milestone(1, "Step", false)
            ),
            coverUri = null
        )

        coEvery { getGoalByIdUseCase(5) } returns goal

        val viewModel = createViewModel(5)

        When("initialized") {

            Then("state should be populated") {

                dispatcher.scheduler.advanceUntilIdle()

                viewModel.state.value.title shouldBe "Existing"
                viewModel.state.value.milestones.size shouldBe 1
            }
        }

        When("ClickUpdateGoal is called") {

            Then("updateGoalUseCase should be invoked") {

                runTest {
                    Dispatchers.setMain(StandardTestDispatcher(testScheduler))

                    val viewModel = createViewModel()
                    viewModel.processCommand(
                        CreateGoalCommand.InputTitle("Goal")
                    )
                    viewModel.processCommand(
                        CreateGoalCommand.ClickUpdateGoal
                    )


                    advanceUntilIdle()

                    coVerify {
                        updateGoalUseCase(match { it.title == "Goal" })
                    }

                    Dispatchers.resetMain()
                }
            }
        }

        When("ClickCompleteGoal is called") {

            Then("completeGoalUseCase should be invoked") {

                runTest {
                    Dispatchers.setMain(StandardTestDispatcher(testScheduler))

                    val viewModel = createViewModel()

                    viewModel.processCommand(
                        CreateGoalCommand.InputTitle("Goal")
                    )

                    viewModel.processCommand(
                        CreateGoalCommand.ClickCreateGoal
                    )

                    advanceUntilIdle()

                    coVerify {
                        addGoalUseCase(match { it.title == "Goal" })
                    }

                    Dispatchers.resetMain()
                }
            }
        }
    }

    Given("Milestones exist") {

        val viewModel = createViewModel()

        viewModel.processCommand(CreateGoalCommand.ClickAddMilestone)

        When("InputMilestoneTitle is called") {

            Then("title should update") {
                viewModel.processCommand(
                    CreateGoalCommand.InputMilestoneTitle(
                        index = 0,
                        title = "Step 1"
                    )
                )

                viewModel.state.value.milestones[0].title shouldBe "Step 1"
            }
        }

        When("ChangeMilestoneCompletedStatusAt is called") {

            Then("status should toggle") {
                viewModel.processCommand(
                    CreateGoalCommand.ChangeMilestoneCompletedStatusAt(0)
                )

                viewModel.state.value.milestones[0].isCompleted shouldBe true
            }
        }

        When("RemoveMilestoneAt is called") {

            Then("milestone should be removed") {
                viewModel.processCommand(
                    CreateGoalCommand.RemoveMilestoneAt(0)
                )

                viewModel.state.value.milestones shouldBe emptyList()
            }
        }
    }
})