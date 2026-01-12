package com.example.habitflow.presentation.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed class Screen : NavKey{

    @Serializable
    data object CreateTask : Screen()

    @Serializable
    data object Tasks : Screen()

}