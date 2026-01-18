package com.example.habitflow.presentation.screens.goals.create

import android.net.Uri
import com.example.habitflow.domain.entities.GoalCategory
import com.example.habitflow.domain.entities.Milestone


data class CreateGoalScreenState(
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
