package com.example.habitflow.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.Serializer
import androidx.datastore.dataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.example.habitflow.data.remote.tokens.CryptoManager
import java.io.InputStream
import java.io.OutputStream

val Context.dataStore: DataStore<Preferences> by preferencesDataStore("App settings")


val Context.tokenDataStore: DataStore<TokenProto> by dataStore(
    fileName = "token.pb",
    serializer = object : Serializer<TokenProto> {
        override val defaultValue: TokenProto = TokenProto.getDefaultInstance()

        override suspend fun readFrom(input: InputStream): TokenProto {
            return try {
                if (input.available() == 0) return defaultValue

                val decryptedInput = CryptoManager.decrypt(input)
                TokenProto.parseFrom(decryptedInput)
            } catch (e: Exception) {
                e.printStackTrace()
                defaultValue
            }
        }

        override suspend fun writeTo(t: TokenProto, output: OutputStream) {
            val encryptedOutput = CryptoManager.encrypt(output)
            t.writeTo(encryptedOutput)
            encryptedOutput.flush()
        }
    }
)