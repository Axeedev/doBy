package com.example.habitflow.presentation.screens.tasks.all

sealed interface SnackbarEvent {

    data object SnackbarUnlocked : SnackbarEvent

    data object SpeechRecognizingError : SnackbarEvent

    data object VoiceRecordError : SnackbarEvent

}