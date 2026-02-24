package com.example.habitflow.data.background

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.habitflow.domain.repository.SyncRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class DataSyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParameters: WorkerParameters,
    private val syncRepository: SyncRepository
) : CoroutineWorker(context, workerParameters) {

    override suspend fun doWork(): Result {

        val requestTypeAsString = inputData.getString(REQUEST_TYPE) ?: return Result.failure()
        val requestType = RequestType.valueOf(requestTypeAsString)

        return try {
            when(requestType){
                RequestType.PULL -> {
                    syncRepository.pullRemoteChanges()
                }
                RequestType.PUSH -> {
                    syncRepository.pushLocalChanges()
                }
            }

            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure()
        }
    }

    companion object {
        const val REQUEST_TYPE = "request_type"
        const val DATA_SYNC_TAG = "data_sync_worker"
    }
}