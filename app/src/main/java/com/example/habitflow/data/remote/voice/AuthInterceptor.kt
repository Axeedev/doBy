package com.example.habitflow.data.remote.voice

import com.example.habitflow.data.remote.tokens.TokenManager
import com.example.habitflow.data.remote.tokens.TokenStorage
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(
    private val tokenManager: TokenManager
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val token = runBlocking {
            tokenManager.getValidAccessToken()
        }

        val request = chain.request().newBuilder().apply {
            token.let {
                addHeader("Authorization", "Bearer $it.")
            }
        }.build()

        return chain.proceed(request)
    }
}