package com.example.habitflow.presentation.navigation

import android.util.Log
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.example.habitflow.R
import com.example.habitflow.presentation.navigation.Screen.Goals
import com.example.habitflow.presentation.navigation.Screen.Tasks
import com.example.habitflow.presentation.screens.goals.all.GoalsScreen
import com.example.habitflow.presentation.screens.goals.create.CreateGoalScreen
import com.example.habitflow.presentation.screens.goals.edit.EditGoalScreen
import com.example.habitflow.presentation.screens.tasks.all.TasksScreen
import com.example.habitflow.presentation.screens.tasks.creation.CreateTaskScreen
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavigationRoot() {
    val backStack = rememberNavBackStack(Tasks)
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val screens = listOf(
        ScreensForDrawer(Tasks, R.drawable.ic_task_app),
        ScreensForDrawer(Goals, R.drawable.ic_goal),
        ScreensForDrawer(Screen.Achievements, R.drawable.ic_trophy),
    )
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet() {
                NavigationDrawerItem(
                    modifier = Modifier.padding(
                        NavigationDrawerItemDefaults.ItemPadding
                    ),
                    icon = {
                        Icon(
                            modifier = Modifier
                                .clip(CircleShape)
                                .clickable {},
                            contentDescription = "go to settings",
                            painter = painterResource(R.drawable.ic_settings)
                        )
                    },
                    label = {},
                    onClick = {},
                    selected = false
                )
                screens.forEach { screenWithIcon ->
                    NavigationDrawerItem(
                        modifier = Modifier
                            .padding(NavigationDrawerItemDefaults.ItemPadding),
                        label = {
                            Text(
                                text = screenWithIcon.screen.label
                            )
                        },
                        selected = backStack.lastOrNull() == screenWithIcon.screen,
                        onClick = {
                            scope.launch {
                                drawerState.close()
                                backStack.add(screenWithIcon.screen)
                            }
                        },
                        icon = {
                            Icon(
                                painter = painterResource(screenWithIcon.iconId),
                                contentDescription = ""
                            )
                        }
                    )
                }
                HorizontalDivider(
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
                NavigationDrawerItem(
                    modifier = Modifier.padding(
                        NavigationDrawerItemDefaults.ItemPadding
                    ),
                    label = {
                        Text(
                            text = "Recently Completed"
                        )
                    },
                    onClick = {},
                    icon = {
                        Icon(
                            painter = painterResource(R.drawable.ic_delete),
                            contentDescription = "go to recently completed"
                        )
                    },
                    selected = false
                )
            }
        }
    ) {

        NavDisplay(
            backStack = backStack,
            transitionSpec = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(300)
                ) togetherWith slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(300)
                )
            },
            entryDecorators = listOf(
                rememberSaveableStateHolderNavEntryDecorator(),
                rememberViewModelStoreNavEntryDecorator()
            ),
            entryProvider = { key ->
                when (key) {
                    is Tasks -> {
                        NavEntry(
                            key = key,
                        ) {
                            TasksScreen {
                                scope.launch {
                                    drawerState.open()
                                }
                            }
                        }
                    }

                    is Screen.Achievements -> {
                        NavEntry(key = key) {
                            Text(
                                text = "here will be achievement screen"
                            )
                        }
                    }

                    is Screen.CreateTask -> {
                        NavEntry(
                            key = key
                        ) {
                            CreateTaskScreen {
                                backStack.removeLastOrNull()
                            }
                        }
                    }

                    is Goals -> {
                        NavEntry(
                            key = key
                        ) {
                            GoalsScreen(
                                onEditGoalClick = {
                                    backStack.add(Screen.EditGoal(it))
                                },
                                onBackClick = {
                                    if (backStack.size > 1) {
                                        backStack.removeLastOrNull()
                                    }
                                }
                            ) {
                                backStack.add(Screen.CreateGoal)
                            }
                        }
                    }

                    is Screen.CreateGoal -> {
                        NavEntry(
                            key = key
                        ) {
                            CreateGoalScreen {
                                backStack.removeLastOrNull()
                            }
                        }
                    }

                    is Screen.EditGoal -> {
                        NavEntry(
                            key = key
                        ) {
                            EditGoalScreen(key.id) {
                                backStack.removeLastOrNull()
                            }
                        }
                    }

                    else -> {
                        throw RuntimeException("Invalid key")
                    }
                }
            }
        )

    }
}