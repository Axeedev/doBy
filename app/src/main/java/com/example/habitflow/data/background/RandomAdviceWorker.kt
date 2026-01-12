package com.example.habitflow.data.background

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters


class RandomAdviceWorker(
    applicationContext: Context,
    workerParameters: WorkerParameters
) : CoroutineWorker(applicationContext, workerParameters) {
    override suspend fun doWork(): Result {
        TODO("Not yet implemented")
    }

}