package com.example.habitflow.presentation.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed class Screen : NavKey{

    @Serializable
    data object CreateTask : Screen()

    @Serializable
    data object Tasks : Screen()

    @Serializable
    data object Goals: Screen()

    @Serializable
    data class EditGoal(val id: Int) : Screen()

    @Serializable
    data object CreateGoal: Screen()

}