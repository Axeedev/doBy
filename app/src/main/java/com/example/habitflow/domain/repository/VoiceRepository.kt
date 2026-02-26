package com.example.habitflow.domain.repository

import com.example.habitflow.domain.entities.tasks.Task

interface VoiceRepository {


    suspend fun startRecording()

    suspend fun stopRecordingAndRecognize()

}