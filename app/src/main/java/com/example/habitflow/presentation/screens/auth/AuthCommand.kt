package com.example.habitflow.presentation.screens.auth

sealed interface AuthCommand {

    data class InputEmail(val email: String) : AuthCommand

    data class InputPassword(val password: String) : AuthCommand

    data object ClickSignupButton : AuthCommand

    data object ClickLoginButton : AuthCommand

    data object ClickGoogleAuthButton : AuthCommand

}
