package com.example.habitflow.domain.entities.achievements

data class Achievement(
    val id: Int,
    val titleId: Int,
    val descriptionId: Int,
    val targetGoal: Int,
    val currentScore: Int,
    val achievementType: AchievementType,
    val iconResId: Int,
){
    val progressPercentage
        get() = (currentScore.toFloat() / targetGoal.toFloat() * 100).toInt()
}