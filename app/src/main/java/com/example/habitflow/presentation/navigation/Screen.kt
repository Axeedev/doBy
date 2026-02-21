package com.example.habitflow.presentation.navigation

import androidx.navigation3.runtime.NavKey
import com.example.habitflow.R
import kotlinx.serialization.Serializable

@Serializable
sealed class Screen(val labelId: Int) : NavKey {

    @Serializable
    data object Tasks : Screen(R.string.tasks_nav_drawer)

    @Serializable
    data object Goals : Screen(R.string.goals_nav_drawer)

    @Serializable
    data class EditGoal(val id: Int) : Screen(R.string.goals_nav_drawer)

    @Serializable
    data object CreateGoal : Screen(R.string.goals_nav_drawer)

    @Serializable
    data object Achievements : Screen(R.string.achievements_nav_drawer)

    @Serializable
    data object RecentlyCompleted : Screen(R.string.recently_completed_naw_drawer)

    @Serializable
    data object Settings : Screen(R.string.settings_screen)

    @Serializable
    data object Login : Screen(R.string.settings_screen)

    @Serializable
    data object Signup : Screen(R.string.settings_screen)

    @Serializable
    data object Analytics : Screen(R.string.analytics_nav_drawer)

}

data class ScreensForDrawer(
    val screen: Screen,
    val iconId: Int
)