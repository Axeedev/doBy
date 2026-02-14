package com.example.habitflow.presentation.screens.achievements

import com.example.habitflow.domain.entities.achievements.Achievement

data class AchievementsScreenState(
    val achievements: List<Achievement> = listOf(),
    val selectedType: FilterChipType = FilterChipType.ALL
)