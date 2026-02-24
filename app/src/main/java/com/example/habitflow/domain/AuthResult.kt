package com.example.habitflow.domain

sealed interface AuthResult {

    data object Success : AuthResult

    data class Failure(val exception: Throwable) : AuthResult

}