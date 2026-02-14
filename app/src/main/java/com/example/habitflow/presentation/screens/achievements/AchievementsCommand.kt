package com.example.habitflow.presentation.screens.achievements

sealed interface AchievementsCommand {


    data class ChangeFilterType(val filterChipType: FilterChipType) : AchievementsCommand
}