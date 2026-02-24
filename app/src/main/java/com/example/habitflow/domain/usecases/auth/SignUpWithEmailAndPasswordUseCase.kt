package com.example.habitflow.domain.usecases.auth

import com.example.habitflow.domain.repository.AuthRepository
import javax.inject.Inject

class SignUpWithEmailAndPasswordUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {

    suspend operator fun invoke(email: String, password: String) = authRepository.signUpWithEmailAndPassword(email, password)
}