package com.example.habitflow.data.repository

import android.content.Context
import android.media.MediaRecorder
import android.util.Log
import com.example.habitflow.data.local.tasks.TaskEntity
import com.example.habitflow.data.local.tasks.TasksDao
import com.example.habitflow.data.remote.tokens.TokenManager
import com.example.habitflow.data.remote.voice.SaluteSpeechApi
import com.example.habitflow.domain.entities.tasks.Priority
import com.example.habitflow.domain.entities.tasks.Task
import com.example.habitflow.domain.repository.VoiceRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import javax.inject.Inject

class VoiceRepositoryImpl @Inject constructor(
    private val speechApi: SaluteSpeechApi,
    private val tasksDao: TasksDao,
    private val tokenManager: TokenManager,
    @param:ApplicationContext private val context: Context
) : VoiceRepository {

    private val assetManager = context.assets
    private var audioFile =  File(context.cacheDir, "sample.mp3")
    private var recorder: MediaRecorder? = null


    override suspend fun startRecording() {
        withContext(Dispatchers.IO) {
            try {
//                audioFile = File(context.cacheDir, "voice_input.mp3")
//                if (audioFile.exists()) audioFile.delete()
//
//                recorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
//                    MediaRecorder(context).apply {
//                        setAudioSource(MediaRecorder.AudioSource.MIC)
//                        setOutputFormat(MediaRecorder.OutputFormat.DEFAULT)
//                        setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT)
//                        setOutputFile(audioFile.absolutePath)
//                        prepare()
//                    }
//                } else {
//                    MediaRecorder().apply {
//                        setAudioSource(MediaRecorder.AudioSource.MIC)
//                        setOutputFormat(MediaRecorder.OutputFormat.DEFAULT)
//                        setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT)
//                        setOutputFile(audioFile.absolutePath)
//                        prepare()
//                    }
//                }
//                recorder?.start()
                Log.d("VoiceRepositoryImpl", "Recording started")
            } catch (e: Exception) {
                throw Exception("Ошибка записи: ${e.message}")
            }
        }
    }

    override suspend fun stopRecordingAndRecognize(): List<Task> {
        return withContext(Dispatchers.IO) {
            try {
                val tempFile = File(context.cacheDir, "sample.mp3")
                assetManager.open("sample.mp3").use {inputStream ->
                    tempFile.outputStream().use { output ->
                        inputStream.copyTo(output)
                    }
                }

                recorder?.stop()
                recorder?.release()
                recorder = null

                if (!audioFile.exists()) throw Exception("Файл не найден")

                val audioBytes = audioFile.readBytes()

                Log.d("stopRecordingAndRecognize", audioBytes.size.toString())

                val audioBody = tempFile.asRequestBody(
                    "audio/mpeg".toMediaType(),
                )


                val token = tokenManager.getValidAccessToken()

                val response = speechApi.recognizeSpeech(
                    authorization = "Bearer $token",
                    audio = audioBody
                )

                if (!response.isSuccessful || response.body() == null) {
                    throw Exception("Ошибка распознавания: ${response.message()}")
                }

                val result = response.body()?.result ?: throw Exception("No body")

                result.forEach {
                    tasksDao.addTask(
                        TaskEntity(
                            id = 0,
                            title = it,
                            deadlineMillis = null,
                            note = "",
                            category = "test",
                            priority = "LOW",
                            isCompleted = false,
                            isReturned = false
                        )
                    )
                }

                listOf()

            } catch (e: Exception) {
                throw Exception("Ошибка обработки: ${e.message}")
            } finally {
                audioFile.delete()
            }
        }
    }

//    private fun parseToTasks(response: SaluteRecognitionResponse): List<Task> {
//        val text = response.result
//        val entities = response.entities.orEmpty()
//
//        val datetimeEntities =
//            entities.filter { it.type == "datetime" && it.value?.date_time != null }
//
//        return datetimeEntities.map { entity ->
//            val dt = entity.value!!.date_time!!
//            val deadlineMillis = convertDateTimeToMillis(dt)
//
//            val title = response.result
//                .replace(entity.value.original_text ?: "", "", ignoreCase = true)
//                .trim()
//                .let { it.ifEmpty { "Задача" } }
//
//            Task(
//                id = 0,
//                title = title,
//                deadlineMillis = deadlineMillis,
//                note = "",
//                category = GoalCategory("AI"),
//                priority = Priority.LOW,
//                isCompleted = false,
//                isReturned = false
//            )
//        }



}