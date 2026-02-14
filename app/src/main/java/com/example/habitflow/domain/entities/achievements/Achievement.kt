package com.example.habitflow.domain.entities.achievements

data class Achievement(
    val id: Int,
    val title: String,
    val description: String,
    val targetGoal: Int,
    val currentScore: Int,
    val achievementType: AchievementType,
    val iconResId: Int,
    val progress: Int
)
