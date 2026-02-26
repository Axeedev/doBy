package com.example.habitflow.presentation.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.habitflow.domain.AuthResult
import com.example.habitflow.domain.usecases.auth.SignInWithEmailAndPasswordUseCase
import com.example.habitflow.domain.usecases.auth.SignInWithGoogleUseCase
import com.example.habitflow.domain.usecases.auth.SignUpWithEmailAndPasswordUseCase
import com.example.habitflow.presentation.utils.AuthValidator
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val signInWithEmailAndPasswordUseCase: SignInWithEmailAndPasswordUseCase,
    private val signUpWithEmailAndPasswordUseCase: SignUpWithEmailAndPasswordUseCase,
    private val signInWithGoogleUseCase: SignInWithGoogleUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(AuthScreenState())
    val state
        get() = _state.asStateFlow()

    private val _errorEvents = MutableSharedFlow<ErrorEvent>()
    val errorEvents
        get() = _errorEvents.asSharedFlow()


    private suspend fun signInWithGoogle() {
        val isSignedIn = signInWithGoogleUseCase()
        _state.update {
            it.copy(
                isAuthSuccess = isSignedIn
            )
        }
    }

    private fun clickLoginButton() {
        _state.update { it.copy(isLoading = true) }
        val currentState = _state.value
        viewModelScope.launch {
            login(currentState.email, currentState.password)
        }
    }

    private fun clickSignupButton() {
        _state.update { it.copy(isLoading = true) }
        val currentState = _state.value
        viewModelScope.launch {
            signup(currentState.email, currentState.password)
        }
    }

    fun processCommand(authCommand: AuthCommand) {
        when (authCommand) {
            AuthCommand.ClickGoogleAuthButton -> {
                viewModelScope.launch {
                    signInWithGoogle()
                }
            }

            is AuthCommand.InputEmail -> {
                _state.update {
                    it.copy(email = authCommand.email, isEmailError = false)
                }

            }

            is AuthCommand.InputPassword -> {
                _state.update { it.copy(password = authCommand.password, isPasswordError = false) }
            }

            AuthCommand.ClickLoginButton -> {
                clickLoginButton()
            }

            AuthCommand.ClickSignupButton -> {
                clickSignupButton()
            }
        }
    }

    private suspend fun auth(
        email: String,
        password: String,
        authType: AuthType
    ) {
        if (!AuthValidator.validateEmail(email)) {
            _state.update { it.copy(isEmailError = true, isLoading = false) }
            return
        }
        if (!AuthValidator.validatePassword(password)) {
            _state.update { it.copy(isPasswordError = true, isLoading = false) }
            return
        }
        val authResult = when (authType) {
            AuthType.LOGIN -> {
                signInWithEmailAndPasswordUseCase(email, password)
            }

            AuthType.SIGNUP -> {
                signUpWithEmailAndPasswordUseCase(email, password)
            }
        }
        when (authResult) {
            is AuthResult.Failure -> {
                _state.update { it.copy(isLoading = false) }
                when (authResult.exception) {
                    is FirebaseAuthInvalidCredentialsException -> {
                        _errorEvents.emit(ErrorEvent.InvalidErrorOrPassword("Invalid email or password."))
                    }

                    is FirebaseAuthInvalidUserException -> {
                        _errorEvents.emit(ErrorEvent.UserNotFound("User with given credentials not found"))
                    }

                    is FirebaseAuthUserCollisionException -> {
                        _errorEvents.emit(ErrorEvent.UserExists("User with given credentials already exists"))
                    }

                    else -> {
                        _errorEvents.emit(ErrorEvent.Other("Something went wrong. Check your Internet connection"))
                    }
                }

            }

            AuthResult.Success -> {
                _state.update { it.copy(isAuthSuccess = true) }
            }
        }

    }

    private suspend fun login(
        email: String, password: String
    ) {
        auth(email, password, AuthType.LOGIN)
    }

    private suspend fun signup(
        email: String, password: String
    ) {
        auth(email, password, AuthType.SIGNUP)
    }

    private enum class AuthType {
        LOGIN, SIGNUP
    }
}