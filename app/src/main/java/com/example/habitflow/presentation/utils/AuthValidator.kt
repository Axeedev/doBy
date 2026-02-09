package com.example.habitflow.presentation.utils

import android.util.Patterns

object AuthValidator {
    fun validateEmail(email: String) : Boolean{
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
    fun validatePassword(password: String) : Boolean{
        return password.length >= 8
    }
}