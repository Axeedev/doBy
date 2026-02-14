package com.example.habitflow.data.repository

import com.example.habitflow.data.auth.GoogleAuthClient
import com.example.habitflow.domain.repository.AuthRepository
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val googleAuthClient: GoogleAuthClient
) : AuthRepository {
    override suspend fun signInWithGoogle(): Boolean {
        return googleAuthClient.signIn()
    }
}