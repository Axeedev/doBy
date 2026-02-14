package com.example.habitflow.presentation.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed class Screen(val label: String) : NavKey {

    @Serializable
    data object CreateTask : Screen("Create task")

    @Serializable
    data object Tasks : Screen("Tasks")

    @Serializable
    data object Goals : Screen("Goals")

    @Serializable
    data class EditGoal(val id: Int) : Screen("Edit goal")

    @Serializable
    data object CreateGoal : Screen("Create goal")

    @Serializable
    data object Achievements : Screen("Achievements")

    @Serializable
    data object RecentlyCompleted : Screen("Recently completed")

    @Serializable
    data object Settings : Screen("Settings")

    @Serializable
    data object Login : Screen("Login")

    @Serializable
    data object Signup : Screen("Signup")

    @Serializable
    data object Analytics : Screen("Analytics")

}

data class ScreensForDrawer(
    val screen: Screen,
    val iconId: Int
)