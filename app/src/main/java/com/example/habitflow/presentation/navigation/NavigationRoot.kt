package com.example.habitflow.presentation.navigation

import android.util.Log
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.example.habitflow.R
import com.example.habitflow.presentation.navigation.Screen.Goals
import com.example.habitflow.presentation.navigation.Screen.Tasks
import com.example.habitflow.presentation.screens.auth.login.LoginScreen
import com.example.habitflow.presentation.screens.auth.signup.SignupScreen
import com.example.habitflow.presentation.screens.goals.all.GoalsScreen
import com.example.habitflow.presentation.screens.goals.create.CreateGoalScreen
import com.example.habitflow.presentation.screens.goals.edit.EditGoalScreen
import com.example.habitflow.presentation.screens.settings.SettingsScreen
import com.example.habitflow.presentation.screens.tasks.all.TasksScreen
import com.example.habitflow.presentation.screens.tasks.completed.RecentlyCompletedScreen
import com.example.habitflow.presentation.screens.tasks.creation.CreateTaskScreen
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavigationRoot() {

    val startDestination = if (FirebaseAuth.getInstance().currentUser == null) Screen.Login else Tasks
    val backStack = rememberNavBackStack(startDestination)
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
            ModalDrawerSheet {
                NavigationDrawerItem(
                    modifier = Modifier.padding(
                        NavigationDrawerItemDefaults.ItemPadding
                    ),
                    icon = {
                        Icon(
                            modifier = Modifier
                                .clip(CircleShape)
                                .clickable {
                                    scope.launch {
                                        drawerState.close()
                                        backStack.add(Screen.Settings)
                                    }
                                },
                            contentDescription = "go to settings",
                            painter = painterResource(R.drawable.ic_settings)
                        )
                    },
                    label = {},
                    onClick = {},
                    selected = backStack.lastOrNull() == Screen.Settings
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
                    modifier = Modifier
                        .padding(NavigationDrawerItemDefaults.ItemPadding)
                        .padding(vertical = 4.dp)
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
                    onClick = {
                        scope.launch {
                            drawerState.close()
                            backStack.add(Screen.RecentlyCompleted)
                        }
                    },
                    icon = {
                        Icon(
                            painter = painterResource(R.drawable.ic_done),
                            contentDescription = "go to recently completed"
                        )
                    },
                    selected = backStack.lastOrNull() == Screen.RecentlyCompleted
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
                rememberViewModelStoreNavEntryDecorator(
                    removeViewModelStoreOnPop = {
                        true
                    }
                )
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

                    is Screen.RecentlyCompleted ->{
                        NavEntry(key = key){
                            RecentlyCompletedScreen {
                                if (backStack.size > 1){
                                    backStack.removeLastOrNull()
                                }
                            }
                        }
                    }

                    is Screen.Settings ->{
                        NavEntry(key = key){
                            SettingsScreen(
                                onSingOut = {
                                    while (backStack.isNotEmpty()){
                                        backStack.removeLastOrNull()
                                    }
                                    Log.d("Settings", backStack.joinToString(", "))
                                    backStack.add(Screen.Login)
                                }
                            ) {
                                if (backStack.size > 1){
                                    backStack.removeLastOrNull()
                                }
                            }
                        }
                    }

                    is Screen.Login -> {
                        NavEntry(key = key){
                            LoginScreen(
                                onSignupClick ={
                                    backStack.add(Screen.Signup)
                                },
                                onSuccessAuth = {
                                    backStack.add(Tasks)
                                }
                            ) {}
                        }
                    }
                    is Screen.Signup -> {
                        NavEntry(key = key){
                            SignupScreen (
                                onBackClick = {
                                    if (backStack.size > 1) {
                                        backStack.removeLastOrNull()
                                    }
                                },
                                onSuccessAuth = {
                                    backStack.add(Tasks)
                                }
                            )
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