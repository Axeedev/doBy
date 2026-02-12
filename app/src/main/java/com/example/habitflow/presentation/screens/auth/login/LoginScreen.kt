@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.habitflow.presentation.screens.auth.login

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.habitflow.R
import com.example.habitflow.presentation.screens.auth.AuthCommand
import com.example.habitflow.presentation.screens.auth.AuthViewModel
import com.example.habitflow.presentation.screens.auth.ErrorEvent
import com.example.habitflow.presentation.screens.goals.create.CreateOrEditButton

@Composable
fun LoginScreen(
    viewModel: AuthViewModel = hiltViewModel(),
    isLogin: Boolean = true,
    onSuccessAuth: () -> Unit,
    onSignupClick: () -> Unit,
    onBackClick: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    if (state.isAuthSuccess){
        LaunchedEffect(Unit) {
            onSuccessAuth()
        }
    }

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.errorEvents.collect {errorEvent ->
            when(errorEvent){
                is ErrorEvent.InvalidErrorOrPassword -> {
                    snackbarHostState.showSnackbar(message = errorEvent.msg)
                }
                is ErrorEvent.Other -> {
                    snackbarHostState.showSnackbar(message = errorEvent.msg)
                }
                is ErrorEvent.UserNotFound -> {
                    snackbarHostState.showSnackbar(message = errorEvent.msg)
                }

                is ErrorEvent.UserExists -> {
                    snackbarHostState.showSnackbar(message = errorEvent.msg)
                }
            }
        }
    }


    Scaffold(
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState
            ){snackbarData ->
                Snackbar(
                    snackbarData = snackbarData,
                    shape = RoundedCornerShape(12.dp),
                    containerColor = Color.White,
                    contentColor = Color.Black
                )
            }
        },
        containerColor = Color.White,
        contentColor = Color.Unspecified,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
                title = {

                },
                navigationIcon = {
                    if (!isLogin) {
                        Icon(
                            modifier = Modifier
                                .padding(start = 8.dp)
                                .clip(CircleShape)
                                .clickable {
                                    onBackClick()
                                },
                            painter = painterResource(R.drawable.ic_arrow_back),
                            contentDescription = "go back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
        ) {

            Spacer(Modifier.weight(1f))

            Column(
                modifier = Modifier
                    .weight(8f)
                    .fillMaxSize()
                    .padding(horizontal = 32.dp),
            ) {
                Text(
                    fontSize = 28.sp,
                    fontWeight = FontWeight.SemiBold,
                    text = if (isLogin) "Welcome back!" else "Get started now"
                )

                Spacer(Modifier.size(24.dp))

                Text(
                    text = "Email address",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 18.sp
                )

                Spacer(Modifier.size(16.dp))

                Column(modifier = Modifier.fillMaxWidth()) {
                    AuthTextField(
                        value = state.email,
                        placeholderText = "Enter your email",
                        isError = state.isEmailError,
                    ) {
                        viewModel.processCommand(AuthCommand.InputEmail(it))
                    }
                    if (state.isEmailError){
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = "Invalid email",
                            color = Color.Red
                        )
                    }
                }

                Spacer(Modifier.size(24.dp))

                Text(
                    text = "Password",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 18.sp
                )

                Spacer(Modifier.size(16.dp))

                Column(modifier = Modifier.fillMaxWidth()) {
                    AuthTextField(
                        value = state.password,
                        visualTransformation = PasswordVisualTransformation(),
                        placeholderText = "Enter your password",
                        isError = state.isPasswordError,
                    ) {
                        viewModel.processCommand(AuthCommand.InputPassword(it))
                    }
                    if (state.isPasswordError) {
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = "The password must be at least 8 characters long",
                            color = Color.Red
                        )
                    }
                }

                Spacer(Modifier.size(24.dp))

                CreateOrEditButton(
                    isSaveButtonEnabled = state.isAuthButtonEnabled,
                    text = if (isLogin) "Login" else "Sign up",
                    content = {
                        if (state.isLoading){
                            Spacer(Modifier.width(16.dp))
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = Color.White
                            )
                        }
                    }
                ) {
                    if (isLogin) viewModel.processCommand(
                        AuthCommand.ClickLoginButton
                    ) else viewModel.processCommand(AuthCommand.ClickSignupButton)
                }

                Spacer(Modifier.size(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    HorizontalDivider(Modifier.weight(1f))

                    Text(
                        modifier = Modifier
                            .padding(horizontal = 8.dp),
                        text = "or"
                    )

                    HorizontalDivider(Modifier.weight(1f))
                }

                Spacer(Modifier.size(16.dp))

                GoogleSignInButton(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    viewModel.processCommand(AuthCommand.ClickGoogleAuthButton)
                }
                if (isLogin){
                    val interactionSource = remember { MutableInteractionSource() }
                    Spacer(Modifier.size(16.dp))
                    Row(
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    ) {
                        Text(
                            text = "Don't have an account?",
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(
                            Modifier
                                .width(8.dp)
                        )
                        Text(
                            modifier = Modifier
                                .clickable(
                                    indication = null,
                                    interactionSource = interactionSource
                                ){
                                    onSignupClick()
                                },
                            text = "Sign up",
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF10B981)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun GoogleSignInButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Button(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, Color(0xFFD9D9D9)),
        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
        onClick = onClick
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_google),
            contentDescription = "sign in with Google",
            tint = Color.Unspecified
        )
        Spacer(Modifier.size(16.dp))
        Text(
            text = "Sign in with Google",
            color = Color.Black
        )
    }
}

@Composable
fun AuthTextField(
    modifier: Modifier = Modifier,
    value: String,
    isError: Boolean,
    placeholderText: String,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    onValueChange: (String) -> Unit
) {
    TextField(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, Color(0xFFD9D9D9), RoundedCornerShape(12.dp)),
        value = value,
        singleLine = true,
        onValueChange = onValueChange,
        placeholder = {
            Text(
                text = placeholderText
            )
        },
        isError = isError,
        textStyle = TextStyle(
            fontWeight = FontWeight.SemiBold,
            fontSize = 15.sp
        ),
        colors = TextFieldDefaults.colors(
            unfocusedContainerColor = Color.Transparent,
            focusedContainerColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            errorTextColor = Color.Red,
            errorIndicatorColor = Color.Transparent
        ),
        visualTransformation = visualTransformation
    )
}