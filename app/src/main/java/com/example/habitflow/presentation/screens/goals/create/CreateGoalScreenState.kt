package com.example.habitflow.presentation.screens.goals.create

import android.net.Uri
import com.example.habitflow.domain.entities.goals.GoalCategory
import com.example.habitflow.domain.entities.goals.Milestone


data class CreateGoalScreenState(
    val goalId: Int? = null,
    val title: String = "",
    val coverUri: Uri? = null,
    val goalCategory: GoalCategory = GoalCategory.SPORTS,
    val description: String = "",
    val startDate: Long = System.currentTimeMillis(),
    val endDate: Long = System.currentTimeMillis(),
    val milestones: List<Milestone> = listOf()
){
    val isSaveButtonEnabled: Boolean
        get() = title.isNotEmpty() && milestones.all { it.title.isNotEmpty() }
}
