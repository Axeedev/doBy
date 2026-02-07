package com.example.habitflow.data.background

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.example.habitflow.data.local.NotificationsProvider
import com.example.habitflow.data.remote.ApiService
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

@HiltWorker
class RandomAdviceWorker @AssistedInject constructor(
    @Assisted applicationContext: Context,
    @Assisted workerParameters: WorkerParameters,
    private val apiService: ApiService,
    private val notificationsProvider: NotificationsProvider
) : CoroutineWorker(applicationContext, workerParameters) {
    override suspend fun doWork(): Result = withContext(Dispatchers.IO){
         try {

            val adviceDto = apiService.getRandomAdvice()
            notificationsProvider.showAdviceForTheDay(adviceDto.advice)
            Result.success()

        }catch (_: Exception){
            Result.failure()
        }
    }

    companion object{

        const val ADVICE_WORKER_NAME = "Get advice"

        fun enqueue(
            context: Context
        ){
            val constraints = Constraints
                .Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            val request = PeriodicWorkRequestBuilder<RandomAdviceWorker>(
                1, TimeUnit.DAYS
            ).setConstraints(constraints).build()

            WorkManager.getInstance(context)
                .enqueueUniquePeriodicWork(
                    uniqueWorkName = ADVICE_WORKER_NAME,
                    existingPeriodicWorkPolicy = ExistingPeriodicWorkPolicy.KEEP,
                    request = request
                )
        }

    }
}