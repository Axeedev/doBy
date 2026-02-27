package com.example.habitflow.domain.repository

import com.example.habitflow.domain.AuthResult

interface AuthRepository {

    suspend fun signInWithGoogle() : AuthResult

    suspend fun signInWithEmailAndPassword(
        email: String,
        password: String
    ) : AuthResult

    suspend fun signUpWithEmailAndPassword(
        email: String,
        password: String
    ) : AuthResult

}