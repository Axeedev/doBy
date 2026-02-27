package com.example.habitflow.domain

sealed interface VoiceRecordResult {

    data object Success : VoiceRecordResult

    data object Error : VoiceRecordResult

}