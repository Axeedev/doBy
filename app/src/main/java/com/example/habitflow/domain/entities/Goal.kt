package com.example.habitflow.domain.entities

data class Goal(
    val id: String,
    val category: String,
    val title: String,
    val description: String,
    val goalEndDate: String,
    val activities: List<Activity>
)
