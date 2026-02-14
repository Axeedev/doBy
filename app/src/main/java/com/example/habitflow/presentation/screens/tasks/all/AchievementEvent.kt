package com.example.habitflow.presentation.screens.tasks.all

sealed interface AchievementEvent {

    data object AchievementUnlocked : AchievementEvent

}