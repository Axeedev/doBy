package com.example.habitflow.data.background

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.habitflow.data.remote.Models
import com.example.habitflow.data.remote.Prompts
import com.example.habitflow.data.remote.summary.ChatApiService
import com.example.habitflow.data.remote.summary.dtos.ChatRequest
import com.example.habitflow.data.remote.summary.dtos.Message
import com.example.habitflow.data.remote.summary.dtos.SummaryResponse
import com.example.habitflow.data.remote.tokens.TokenManager
import com.example.habitflow.data.remote.voice.SaluteSpeechApiService
import com.example.habitflow.data.utils.parseToMillis
import com.example.habitflow.domain.entities.goals.GoalCategory
import com.example.habitflow.domain.entities.tasks.Priority
import com.example.habitflow.domain.entities.tasks.Task
import com.example.habitflow.domain.usecases.tasks.AddTaskUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

@HiltWorker
class VoiceToTaskWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted workerParameters: WorkerParameters,
    private val speechApi: SaluteSpeechApiService,
    private val chatApiService: ChatApiService,
    private val addTaskUseCase: AddTaskUseCase,
    private val tokenManager: TokenManager,
    private val json: Json,
) : CoroutineWorker(
    context, workerParameters
) {
    private val assetManager = context.assets

    override suspend fun doWork(): Result {

        return try {
//            val tempFile = File(context.cacheDir, "sample.mp3")
//            assetManager.open("sample.mp3").use {inputStream ->
//                tempFile.outputStream().use { output ->
//                    inputStream.copyTo(output)
//                }
//            }
            val filePath = inputData.getString(FILE_PATH) ?: return Result.failure()
            val tempFile = File(filePath)


            val audioBody = tempFile.asRequestBody(
                contentType = "audio/x-pcm".toMediaType(),
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
                    model = Models.GIGACHAT_BASIC,
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
                tasks.forEach { taskDto ->
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

            Result.success()

        }catch (e: Exception){
            e.printStackTrace()
            if (runAttemptCount >= 3) Result.failure() else Result.retry()
        }
    }
    companion object{
        const val VOICE_RECOGNIZE_WORKER_TAG = "voice recognize"
        const val FILE_PATH = "file_path"
    }
}