package com.example.habitflow.domain.usecases.voice

import com.example.habitflow.domain.repository.VoiceRepository
import javax.inject.Inject

class StartVoiceRecordingUseCase @Inject constructor(
    private val repository: VoiceRepository
){

    suspend operator fun invoke() = repository.startRecording()
}