package com.example.habitflow.domain.entities.goals

import java.util.UUID

data class GoalCategory(
    val name: String,
    val id: String = UUID.randomUUID().toString()
) {
    companion object {
        const val CALENDAR_NAME = "Calendar"
        val defaultCategories = listOf(
            GoalCategory("Career"),
            GoalCategory("Education"),
            GoalCategory("Health"),
        )

    }
}