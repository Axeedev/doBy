package com.example.habitflow.presentation.screens.auth.signup

import androidx.compose.runtime.Composable
import com.example.habitflow.presentation.screens.auth.login.LoginScreen

@Composable
fun SignupScreen(
    onBackClick: () -> Unit,
    onSuccessAuth: () -> Unit,
){
    LoginScreen(
        isLogin = false,
        onSignupClick = {},
        onBackClick = onBackClick,
        onSuccessAuth = onSuccessAuth
    )
}