package com.example.habitflow.data.repository

import android.content.Context
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import androidx.annotation.RequiresPermission
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.example.habitflow.data.background.VoiceToTaskWorker
import com.example.habitflow.domain.repository.VoiceRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

class VoiceRepositoryImpl @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val workManager: WorkManager,
) : VoiceRepository {

    private val assetManager = context.assets
    private var audioFile =  File(context.cacheDir, "sample.pcm")
    private var audioRecord: AudioRecord? = null
    private var isRecording = false
    private var recordingJob: Job? = null


    @RequiresPermission(android.Manifest.permission.RECORD_AUDIO)
    override suspend fun startRecording() {
        withContext(Dispatchers.IO) {
            try {
                if (audioFile.exists()) audioFile.delete()

                val sampleRate = 16000
                val channelConfig = AudioFormat.CHANNEL_IN_MONO
                val audioFormat = AudioFormat.ENCODING_PCM_16BIT

                val bufferSize = AudioRecord.getMinBufferSize(
                    sampleRate,
                    channelConfig,
                    audioFormat
                )

                audioRecord = AudioRecord(
                    MediaRecorder.AudioSource.MIC,
                    sampleRate,
                    channelConfig,
                    audioFormat,
                    bufferSize
                )

                val buffer = ByteArray(bufferSize)
                val outputStream = FileOutputStream(audioFile)

                audioRecord?.startRecording()
                isRecording = true

                recordingJob = CoroutineScope(Dispatchers.IO).launch {
                    while (isRecording) {
                        val read = audioRecord?.read(buffer, 0, buffer.size) ?: 0
                        if (read > 0) {
                            outputStream.write(buffer, 0, read)
                        }
                    }
                    outputStream.close()
                }
            } catch (e: Exception) {
                throw Exception("Ошибка записи: ${e.message}")
            }
        }
    }

    override suspend fun stopRecordingAndRecognize(){
        isRecording = false

        recordingJob?.join()
        audioRecord?.apply {
            stop()
            release()
        }
        audioRecord = null
        val data = workDataOf(
            VoiceToTaskWorker.FILE_PATH to audioFile.absolutePath
        )

        val workRequest = OneTimeWorkRequestBuilder<VoiceToTaskWorker>()
            .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
            .setInputData(data)
            .addTag(VoiceToTaskWorker.VOICE_RECOGNIZE_WORKER_TAG)
            .build()

        workManager.enqueueUniqueWork(
            uniqueWorkName = "voice to task work",
            existingWorkPolicy = ExistingWorkPolicy.KEEP,
            request = workRequest
        )
    }
}