package com.example.habitflow.data.local

import android.content.Context
import androidx.core.net.toUri
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.util.UUID
import javax.inject.Inject

class InternalStorageManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val rootDirectory = context.filesDir

    suspend fun addToInternal(uri: String): String{
        val filePath = "IMG_${UUID.randomUUID()}"
        val file = File(rootDirectory, filePath)
        withContext(Dispatchers.IO){
            context.contentResolver.openInputStream(uri.toUri()).use {inputStream ->
                file.outputStream().use{outputStream ->
                    inputStream?.copyTo(outputStream)
                }
            }
        }
        return file.absolutePath
    }

}