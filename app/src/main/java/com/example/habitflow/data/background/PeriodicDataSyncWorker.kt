package com.example.habitflow.data.background

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.habitflow.domain.repository.SyncRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class PeriodicDataSyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParameters: WorkerParameters,
    private val syncRepository: SyncRepository
) : CoroutineWorker(context, workerParameters) {

    override suspend fun doWork(): Result {
        return try {

            syncRepository.pushLocalChanges()
            syncRepository.pullRemoteChanges()

            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure()
        }
    }

    companion object{
        const val PERIODIC_DATA_SYNC_WORKER_TAG = "periodic data sync"
    }
}