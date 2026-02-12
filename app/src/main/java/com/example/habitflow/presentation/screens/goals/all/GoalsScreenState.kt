package com.example.habitflow.presentation.screens.goals.all

import com.example.habitflow.domain.entities.goals.Goal

data class GoalsScreenState(
    val goals: List<Goal> = listOf()
)