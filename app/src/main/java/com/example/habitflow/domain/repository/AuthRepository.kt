package com.example.habitflow.domain.repository

interface AuthRepository {

    suspend fun signInWithGoogle() : Boolean
}