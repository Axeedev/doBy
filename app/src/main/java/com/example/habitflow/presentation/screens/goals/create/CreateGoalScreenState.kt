package com.example.habitflow.presentation.screens.goals.create

import android.net.Uri
import com.example.habitflow.domain.entities.Category
import com.example.habitflow.domain.entities.goals.Milestone


data class CreateGoalScreenState(
    val goalId: Int? = null,
    val title: String = "",
    val coverUri: Uri? = null,
    val category: Category = Category.defaultCategories.first(),
    val description: String = "",
    val startDate: Long = System.currentTimeMillis(),
    val endDate: Long = System.currentTimeMillis(),
    val milestones: List<Milestone> = listOf(),
    val categories: List<Category> = Category.defaultCategories,
    val newCategoryName: String = "",
    val isCompleteDialogOpened: Boolean = false,
    val isAddCategoryDialogOpened: Boolean = false
){
    val isSaveButtonEnabled: Boolean
        get() = title.trim().isNotEmpty() && milestones.all { it.title.isNotEmpty() }

    val isAddCategoryButtonEnabled
        get() = newCategoryName.trim().isNotEmpty()
}
