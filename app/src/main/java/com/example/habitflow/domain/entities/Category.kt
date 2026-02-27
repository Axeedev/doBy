package com.example.habitflow.domain.entities

import java.util.UUID

data class Category(
    val name: String,
    val id: String = UUID.randomUUID().toString()
) {
    companion object {
        const val CALENDAR_NAME = "Calendar"
        val defaultCategories = listOf(
            Category("Career"),
            Category("Education"),
            Category("Health"),
        )

    }
}