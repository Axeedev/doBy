package com.example.habitflow.presentation.screens.auth

sealed interface ErrorEvent {

    data class InvalidErrorOrPassword(val msg: String) : ErrorEvent

    data class UserNotFound(val msg: String) : ErrorEvent

    data class UserExists(val msg: String) : ErrorEvent

    data class Other(val msg: String) : ErrorEvent

}