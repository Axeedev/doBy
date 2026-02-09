package com.example.habitflow.presentation.screens.auth

data class AuthScreenState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val isAuthSuccess: Boolean = false,
    val isEmailError: Boolean = false,
    val isPasswordError: Boolean = false,
    val errorEvent: ErrorEvent? = null
){
    val isAuthButtonEnabled
        get() = email.isNotEmpty() && password.isNotEmpty() && !isLoading
}
