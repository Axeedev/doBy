package com.example.habitflow.presentation.screens.goals.edit

import androidx.compose.runtime.Composable
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.habitflow.presentation.screens.goals.OpenReason
import com.example.habitflow.presentation.screens.goals.create.CreateGoalScreen
import com.example.habitflow.presentation.screens.goals.create.CreateGoalViewModel

@Composable
fun EditGoalScreen(
    goalId: Int,
    viewModel: CreateGoalViewModel = hiltViewModel(
        creationCallback = { factory: CreateGoalViewModel.ViewModelFactory ->
            factory.create(goalId)
        }
    ),
    onFinished: () -> Unit
){
    CreateGoalScreen(
        viewModel = viewModel,
        onFinished = onFinished,
        openReason = OpenReason.EDIT
    )
}