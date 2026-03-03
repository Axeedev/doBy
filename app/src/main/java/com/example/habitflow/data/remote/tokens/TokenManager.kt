package com.example.habitflow.data.remote.tokens

import android.content.Context
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

    suspend fun getValidSpeechAccessToken(): String {

        val tokenData = dataStore.data.first()

        val now = System.currentTimeMillis()

        if (tokenData.speechAccessToken.isNotEmpty() &&
            tokenData.speechExpiresAt > now
        ) {
            return tokenData.speechAccessToken
        }
        val response = authApi.getToken(
            authorization = "Basic MDE5Yzk1YjEtNzFmMS03MTY1LTliYTMtZTc2MmVjZGY5OTA4Ojc1ZjMzYTgyLWM3NGUtNGYxZC05OGQzLTExNGE0ZTY3ZGIyYQ==",
            rqUid = "019c95b1-71f1-7165-9ba3-e762ecdf9908"
        )

        if (!response.isSuccessful || response.body() == null) {
            throw Exception("Не удалось получить speech токен")
        }

        val newToken = response.body() ?: throw Exception("Не удалось получить speech токен")

        val expiresAt = newToken.expiresAt

        dataStore.updateData {
            it.toBuilder()
                .setSpeechAccessToken(newToken.accessToken)
                .setSpeechExpiresAt(expiresAt)
                .build()
        }

        return newToken.accessToken
    }

    suspend fun getValidChatAccessToken() : String {
        val tokenData = dataStore.data.first()

        val now = System.currentTimeMillis()
        if (tokenData.chatAccessToken.isNotEmpty() && tokenData.chatExpiresAt > now){
            return tokenData.chatAccessToken
        }

        val response = authApi.getToken(
            authorization = "Basic ZjY5YmZhNzQtYjJjMy00YWMzLTgyMDQtODI4N2E1MjA4NjQ5OjAzZTg1OWQwLTNhZjAtNDI5YS1hMzI1LWM0MjM1MzdlNTM5OA==",
            rqUid = "f69bfa74-b2c3-4ac3-8204-8287a5208649",
            scope = "GIGACHAT_API_PERS"
        )

        if (!response.isSuccessful || response.body() == null) {
            throw Exception("Не удалось получить chat токен")
        }

        val newToken = response.body() ?: throw Exception("Не удалось получить chat токен")

        val expiresAt = newToken.expiresAt
        dataStore.updateData {
            it.toBuilder()
                .setChatAccessToken(newToken.accessToken)
                .setChatExpiresAt(expiresAt)
                .build()
        }
        return newToken.accessToken
    }
}