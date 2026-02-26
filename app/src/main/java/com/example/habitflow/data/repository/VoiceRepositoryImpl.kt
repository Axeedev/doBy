package com.example.habitflow.data.repository

import android.content.Context
import android.media.MediaRecorder
import android.util.Log
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkManager
import com.example.habitflow.data.background.VoiceToTaskWorker
import com.example.habitflow.domain.repository.VoiceRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

class VoiceRepositoryImpl @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val workManager: WorkManager,
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

    override suspend fun stopRecordingAndRecognize(){
        val workRequest = OneTimeWorkRequestBuilder<VoiceToTaskWorker>()
            .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
            .addTag(VoiceToTaskWorker.VOICE_RECOGNIZE_WORKER_TAG)
            .build()

        workManager.enqueueUniqueWork(
            uniqueWorkName = "voice to task work",
            existingWorkPolicy = ExistingWorkPolicy.APPEND_OR_REPLACE,
            request = workRequest
        )
    }
}