package com.example.habitflow.presentation.screens.goals.all

sealed interface GoalsCommand {

    data class DeleteCommand(val id: Int) : GoalsCommand

}