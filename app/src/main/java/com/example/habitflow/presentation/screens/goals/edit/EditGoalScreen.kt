package com.example.habitflow.presentation.screens.goals.edit

import androidx.compose.runtime.Composable
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.habitflow.presentation.screens.goals.OpenReason
import com.example.habitflow.presentation.screens.goals.create.CreateGoalScreen

@Composable
fun EditGoalScreen(
    goalId: Int,
    viewModel: EditGoalViewModel = hiltViewModel(
        creationCallback = { factory:EditGoalViewModel.EditGoalFactory ->
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