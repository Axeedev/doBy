package com.example.habitflow.domain.repository

import com.example.habitflow.domain.VoiceRecordResult

interface VoiceRepository {


    suspend fun startRecording() : VoiceRecordResult

    suspend fun stopRecordingAndRecognize()

}