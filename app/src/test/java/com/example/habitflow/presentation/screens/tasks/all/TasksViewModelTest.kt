package com.example.habitflow.presentation.screens.tasks.all

import androidx.work.WorkManager
import com.example.habitflow.domain.entities.Category
import com.example.habitflow.domain.entities.tasks.Priority
import com.example.habitflow.domain.entities.tasks.Task
import com.example.habitflow.domain.usecases.achievements.OnTaskCompletedUseCase
import com.example.habitflow.domain.usecases.tasks.AddTaskToCompletedUseCase
import com.example.habitflow.domain.usecases.tasks.AddTaskUseCase
import com.example.habitflow.domain.usecases.tasks.DeleteTaskUseCase
import com.example.habitflow.domain.usecases.tasks.EditTaskUseCase
import com.example.habitflow.domain.usecases.tasks.GetTasksUseCase
import com.example.habitflow.domain.usecases.tasks.ReturnTaskUseCase
import com.example.habitflow.domain.usecases.voice.StartVoiceRecordingUseCase
import com.example.habitflow.domain.usecases.voice.StopRecordingUseCase
import com.example.habitflow.presentation.screens.tasks.TimeEntity
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
import java.time.LocalTime
import java.time.ZoneId

@OptIn(ExperimentalCoroutinesApi::class)
class TasksViewModelTest : BehaviorSpec({


    val testDispatcher = StandardTestDispatcher()

    // Моки зависимостей
    val getTasksUseCase = mockk<GetTasksUseCase>()
    val addTaskUseCase = mockk<AddTaskUseCase>()
    val editTaskUseCase = mockk<EditTaskUseCase>()
    val addTaskToCompletedUseCase = mockk<AddTaskToCompletedUseCase>()
    val returnTaskUseCase = mockk<ReturnTaskUseCase>()
    val deleteTaskUseCase = mockk<DeleteTaskUseCase>()
    val onTaskCompletedUseCase = mockk<OnTaskCompletedUseCase>()
    val startVoiceRecordingUseCase = mockk<StartVoiceRecordingUseCase>()
    val stopRecordingUseCase = mockk<StopRecordingUseCase>()
    val workManager = mockk<WorkManager>()


    every { getTasksUseCase() } returns flowOf(emptyList())

    lateinit var viewModel: TasksViewModel

    beforeSpec {
        Dispatchers.setMain(testDispatcher)
    }

    afterSpec {
        Dispatchers.resetMain()
    }
    Given("Viewmodel is initialized"){
        viewModel = TasksViewModel(
            getTasksUseCase = getTasksUseCase,
            addTaskUseCase = addTaskUseCase,
            editTaskUseCase = editTaskUseCase,
            addTaskToCompletedUseCase = addTaskToCompletedUseCase,
            returnTaskUseCase = returnTaskUseCase,
            deleteTaskUseCase = deleteTaskUseCase,
            onTaskCompletedUseCase = onTaskCompletedUseCase,
            startVoiceRecordingUseCase = startVoiceRecordingUseCase,
            stopRecordingUseCase = stopRecordingUseCase,
            workManager = workManager
        )
        When("state is initialized"){
            Then("State should contain initial values"){
                val state = viewModel.state.value
                state.title shouldBe ""
                state.tasksMapSections.isEmpty() shouldBe true
                state.showBottomSheet shouldBe false
                state.isVoiceRecording shouldBe false
                state.isRefreshLoading shouldBe false
                state.description shouldBe ""
                state.date shouldBe null
                state.category shouldBe Category.defaultCategories.first()
                state.categories.size shouldBe Category.defaultCategories.size
                state.buttonText shouldBe "Create task"
                state.remindAtMinutesOfDay shouldBe null
                state.priority shouldBe Priority.LOW
            }
        }
        When("Input task title"){

            val title = "title"

            viewModel.processCommand(TasksCommand.InputTitle(title))

            Then("state should contain correct title"){
                val state = viewModel.state.value
                state.title shouldBe title
            }
        }
        When("Input task description"){
            val description = "description"

            viewModel.processCommand(TasksCommand.InputDescription(description))

            Then("state should contain correct description"){
                val state = viewModel.state.value
                state.description shouldBe description
            }
        }
        When("Choose category"){
            val category = Category.defaultCategories.last()
            viewModel.processCommand(TasksCommand.ChangeCategory(category))
            Then("state should contain correct category"){
                val state = viewModel.state.value
                state.category shouldBe category

            }
        }
        When("Add another category"){
            val newCategory = Category("New category")
            viewModel.processCommand(TasksCommand.AddNewCategory(newCategory.name))
            Then("state should contain correct category"){
                val state = viewModel.state.value
                state.category.name shouldBe newCategory.name
            }
        }
        When("Choose date"){
            val date = LocalDate.now()
                .atTime(LocalTime.MAX)
                .atZone(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli()
            viewModel.processCommand(TasksCommand.InputDate(date))
            Then("state should contain correct date"){
                val state = viewModel.state.value
                state.date shouldBe date
            }
        }
        When("Choose time"){

            val timeEntity = TimeEntity(10, 30)
            viewModel.processCommand(TasksCommand.InputDeadline(timeEntity))
            Then("state should contain correct time"){
                val state = viewModel.state.value
                state.remindAtMinutesOfDay shouldBe timeEntity
            }
        }
        When("Start voice input"){

            viewModel.processCommand(TasksCommand.StartVoiceInput)
            Then("isVoiceRecording should be true"){
                val state = viewModel.state.value
                state.isVoiceRecording shouldBe true
            }
        }
        When("stop voice input"){

            viewModel.processCommand(TasksCommand.StopVoiceInput)
            Then("isVoiceRecording should be true false"){
                val state = viewModel.state.value
                state.isVoiceRecording shouldBe false
            }
        }
        When("Click fab add task"){
            viewModel.processCommand(TasksCommand.ClickButtonAddTask)
            Then("showBottomSheet should be true"){
                val state =  viewModel.state.value
                state.showBottomSheet shouldBe  true
            }
        }
        When("Click delete task"){
            viewModel.processCommand(TasksCommand.DeleteTask(0))
            Then("showBottomSheet should be false"){
                val state =  viewModel.state.value
                state.showBottomSheet shouldBe false
            }
        }
        When("Click add task"){
            viewModel.processCommand(TasksCommand.AddTask)
            Then("showBottomSheet should be false"){
                val state =  viewModel.state.value
                state.showBottomSheet shouldBe false
            }
        }
        When("Input category name"){
            val name = "category"
            viewModel.processCommand(TasksCommand.InputCategoryName(name))
            Then("state should contain correct category name"){

                val state =  viewModel.state.value
                state.newCategoryName shouldBe name
            }
        }
        When("Click task"){
            val task = Task(
                id = 1,
                title = "title",
                deadlineMillis = null,
                description = "note",
                category = Category("test"),
                priority = Priority.HIGH
            )
            viewModel.processCommand(TasksCommand.ClickTask(task))
            Then("State must contain fields which are equal given task"){
                val state = viewModel.state.value
                state.title shouldBe task.title
                state.taskId shouldBe 1
                state.description shouldBe task.description
                state.category.name shouldBe task.category.name
                state.priority shouldBe task.priority
            }
        }

    }
})