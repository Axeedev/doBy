package com.example.habitflow.presentation.screens.tasks.all

import com.example.habitflow.domain.entities.Category
import com.example.habitflow.domain.entities.tasks.Priority
import com.example.habitflow.domain.entities.tasks.Task
import com.example.habitflow.presentation.screens.tasks.TaskDeadlineSection
import com.example.habitflow.presentation.screens.tasks.TimeEntity

data class TasksScreenState(
    val taskId: Int? = null,
    val tasksMapSections: Map<TaskDeadlineSection, List<Task>> = mapOf(),
    val title: String = "",
    val date: Long? = null,
    val remindAtMinutesOfDay: TimeEntity? = null,
    val description: String = "",
    val priority: Priority = Priority.LOW,
    val category: Category = Category.defaultCategories.first(),
    val buttonText: String = "Create task",
    val isRefreshLoading: Boolean = false,
    val categories: List<Category> = Category.defaultCategories,
    val newCategoryName: String = "",
    val isVoiceRecording: Boolean = false,
    val showBottomSheet: Boolean = false,
) {
    val isButtonEnabled
        get() = title.trim().isNotEmpty()

    val isAddCategoryButtonEnabled
        get() = newCategoryName.trim().isNotEmpty()
}
