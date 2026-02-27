package com.example.habitflow.domain.entities.goals

import com.example.habitflow.domain.entities.Category

data class Goal(
    val id: Int,
    val category: Category,
    val title: String,
    val description: String,
    val goalStartDate: Long,
    val goalEndDate: Long,
    val milestones: List<Milestone>,
    val coverUri: String? = null
)
