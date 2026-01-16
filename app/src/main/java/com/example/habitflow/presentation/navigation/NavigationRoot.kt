package com.example.habitflow.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.example.habitflow.presentation.screens.goals.create.CreateGoalScreen
import com.example.habitflow.presentation.screens.goals.all.GoalsScreen
import com.example.habitflow.presentation.screens.tasks.all.TasksScreen
import com.example.habitflow.presentation.screens.tasks.creation.CreateTaskScreen


@Composable
fun NavigationRoot() {
    val backStack = rememberNavBackStack(Screen.CreateGoal)
    NavDisplay(
        backStack = backStack,
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator()
        ),
        entryProvider = { key ->
            when (key) {
                is Screen.Tasks -> {
                    NavEntry(
                        key = key
                    ) {
                        TasksScreen(){
                            backStack.add(Screen.CreateTask)
                        }
                    }
                }

                is Screen.CreateTask -> {
                    NavEntry(
                        key = key
                    ) {
                        CreateTaskScreen(){
                            backStack.removeLastOrNull()
                        }

                    }
                }
                is Screen.Goals ->{
                    NavEntry(
                        key = key
                    ){
                        GoalsScreen(){
                            backStack.add(Screen.CreateGoal)
                        }
                    }
                }
                is Screen.CreateGoal->{
                    NavEntry(
                        key = key
                    ){
                        CreateGoalScreen(){
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