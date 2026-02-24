package com.example.habitflow.domain.repository

interface SyncRepository {

    suspend fun pushLocalChanges()

    suspend fun pullRemoteChanges()

}