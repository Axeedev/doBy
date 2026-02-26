package com.example.habitflow.data.remote.tokens

import android.content.Context
import android.util.Log
import com.example.habitflow.data.TokenProto
import com.example.habitflow.data.remote.voice.SaluteAuthService
import com.example.habitflow.data.tokenDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class TokenManager @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val authApi: SaluteAuthService
) {

    private val dataStore = context.tokenDataStore

    suspend fun getValidAccessToken(): String {
        val tokenData = dataStore.data.first()


        Log.d("getValidAccessToken", tokenData.accessToken)

        val now = System.currentTimeMillis()

        if (tokenData.accessToken.isNotEmpty() &&
            tokenData.expiresAt > now
        ) {
            return tokenData.accessToken
        }

        val response = authApi.getToken()

        if (!response.isSuccessful || response.body() == null) {
            throw Exception("Не удалось получить токен")
        }

        val newToken = response.body() ?: throw Exception("Не удалось получить токен")

        val expiresAt = newToken.expiresAt + now

        dataStore.updateData {
            it.toBuilder()
                .setAccessToken(newToken.accessToken)
                .setExpiresAt(expiresAt)
                .build()
        }

        return newToken.accessToken
    }
}