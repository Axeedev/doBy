package com.example.habitflow.domain.entities

data class Goal(
    val id: Int,
    val category: GoalCategory,
    val title: String,
    val description: String,
    val goalStartDate: String,
    val goalEndDate: String,
    val milestones: List<Milestone>,
    val coverUri: String? = null
)
