package com.example.habitflow.domain.usecases.voice

import com.example.habitflow.domain.repository.VoiceRepository
import javax.inject.Inject

class StopRecordingUseCase @Inject constructor(
    private val repository: VoiceRepository
){
    suspend operator fun invoke() = repository.stopRecordingAndRecognize()
}