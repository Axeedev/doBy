package com.example.habitflow.data.repository

import android.content.Context
import android.media.MediaRecorder
import android.util.Log
import com.example.habitflow.data.remote.Prompts
import com.example.habitflow.data.remote.summary.ChatApiService
import com.example.habitflow.data.remote.summary.dtos.ChatRequest
import com.example.habitflow.data.remote.summary.dtos.Message
import com.example.habitflow.data.remote.summary.dtos.SummaryResponse
import com.example.habitflow.data.remote.tokens.TokenManager
import com.example.habitflow.data.remote.voice.SaluteSpeechApi
import com.example.habitflow.data.utils.parseToMillis
import com.example.habitflow.domain.entities.goals.GoalCategory
import com.example.habitflow.domain.entities.tasks.Priority
import com.example.habitflow.domain.entities.tasks.Task
import com.example.habitflow.domain.repository.VoiceRepository
import com.example.habitflow.domain.usecases.tasks.AddTaskUseCase
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import javax.inject.Inject

class VoiceRepositoryImpl @Inject constructor(
    private val speechApi: SaluteSpeechApi,
    private val addTaskUseCase: AddTaskUseCase,
    private val tokenManager: TokenManager,
    private val chatApiService: ChatApiService,
    private val json: Json,
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

//                recorder?.stop()
//                recorder?.release()
//                recorder = null

                if (!audioFile.exists()) throw Exception("Файл не найден")

                val audioBytes = audioFile.readBytes()


                val audioBody = tempFile.asRequestBody(
                    "audio/mpeg".toMediaType(),
                )

                val speechToken = tokenManager.getValidSpeechAccessToken()

                val response = speechApi.recognizeSpeech(
                    authorization = "Bearer $speechToken",
                    audio = audioBody
                )

                if (!response.isSuccessful || response.body() == null) {
                    throw Exception("Ошибка распознавания: ${response.message()}")
                }

                val result = response.body()?.result ?: throw Exception("No body")

                val chatToken = tokenManager.getValidChatAccessToken()
                val summaryResponse = chatApiService.getSummary(
                    authorization = "Bearer $chatToken",
                    chatRequest = ChatRequest(
                        model = "GigaChat-2-Pro",
                        messages = listOf(
                            Message(
                                role = "user",
                                content = Prompts.CHAT_PROMPT + result
                            )
                        )
                    )
                )

                if (!summaryResponse.isSuccessful || summaryResponse.body() == null) {
                    throw Exception("Ошибка распознавания: ${summaryResponse.message()}")
                }
                val summaryResult = summaryResponse.body() ?: throw Exception("Ошибка распознавания: ${summaryResponse.message()}")



                summaryResult.choices.forEach { choice ->
                    val response = json.decodeFromString<SummaryResponse>(choice.message.content)
                    val tasks = response.tasks
                    Log.d("API RESPONSE:", tasks.size.toString())
                    tasks.forEach { taskDto ->
                        Log.d("TASK", taskDto.title)
                        val deadlineMillis = parseToMillis(taskDto.deadline)
                        addTaskUseCase(
                            task = Task(
                                id = 0,
                                title = taskDto.title,
                                deadlineMillis = deadlineMillis,
                                note = "",
                                category = GoalCategory(taskDto.category),
                                priority = Priority.LOW
                            )
                        )
                    }
                }


                listOf()

            } catch (e: Exception) {
                e.printStackTrace()
                listOf()
            } finally {
                audioFile.delete()
            }
        }
    }
}