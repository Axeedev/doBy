package com.example.habitflow.data.remote.tokens

import android.content.Context
import com.example.habitflow.data.TokenProto
import com.example.habitflow.data.tokenDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject

class TokenStorage @Inject constructor(
    @param:ApplicationContext private val context: Context
) {

    val tokenFlow: Flow<TokenProto> = context.tokenDataStore.data

    suspend fun saveToken(access: String) {
        context.tokenDataStore.updateData { current ->
            current.toBuilder()
                .setAccessToken(access)
                .build()
        }
    }

    suspend fun clearTokens() {
        context.tokenDataStore.updateData { TokenProto.getDefaultInstance() }
    }

    suspend fun getAccessToken(): String? =
        tokenFlow.firstOrNull()?.accessToken?.takeIf { it.isNotEmpty() }

}