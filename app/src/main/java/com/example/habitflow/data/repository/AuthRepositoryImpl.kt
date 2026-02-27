package com.example.habitflow.data.repository

import com.example.habitflow.data.auth.GoogleAuthClient
import com.example.habitflow.domain.repository.AuthRepository
import com.example.habitflow.domain.AuthResult
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val googleAuthClient: GoogleAuthClient,
    private val firebaseAuth: FirebaseAuth,
) : AuthRepository {

    override suspend fun signInWithGoogle(): AuthResult {
        return googleAuthClient.signIn()
    }

    override suspend fun signInWithEmailAndPassword(email: String, password: String): AuthResult {

        return try {
            firebaseAuth.signInWithEmailAndPassword(email, password).await()
            AuthResult.Success
        }
        catch (e: Throwable){
            AuthResult.Failure(e)
        }

    }

    override suspend fun signUpWithEmailAndPassword(email: String, password: String): AuthResult {

        return try {
            firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            return AuthResult.Success
        }
        catch (e: Throwable){
            AuthResult.Failure(e)
        }
    }
}