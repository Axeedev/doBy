package com.example.habitflow.data.background

import android.util.Log
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.example.habitflow.domain.repository.SettingsRepository
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class DataSyncScheduler @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val workManager: WorkManager,
) {
    suspend fun schedulePeriodSync() {
        enqueuePeriodicRequest()
    }

    private suspend fun enqueuePeriodicRequest() {
        settingsRepository.getNetworkType()
            .collect { wifiOnly ->
                val constraints = Constraints.Builder()
                    .setRequiredNetworkType(
                        if (wifiOnly) NetworkType.UNMETERED else NetworkType.CONNECTED
                    )
                    .build()
                val request = PeriodicWorkRequestBuilder<PeriodicDataSyncWorker>(
                    30, TimeUnit.MINUTES
                ).setConstraints(constraints)
                    .addTag(PeriodicDataSyncWorker.PERIODIC_DATA_SYNC_WORKER_TAG)
                    .setBackoffCriteria(
                        BackoffPolicy.EXPONENTIAL,
                        30, TimeUnit.SECONDS
                    )
                    .setInitialDelay(10, TimeUnit.MINUTES)
                    .build()

                workManager.enqueueUniquePeriodicWork(
                    uniqueWorkName = "periodic refresh data",
                    existingPeriodicWorkPolicy = ExistingPeriodicWorkPolicy.KEEP,
                    request
                )
            }
    }

    suspend fun scheduleDataPush() {
        enqueueRequest(RequestType.PUSH)
    }

    suspend fun scheduleDataPull() {
        enqueueRequest(RequestType.PULL)
    }

    private suspend fun enqueueRequest(requestType: RequestType) {
        settingsRepository.getNetworkType()
            .collect { wifiOnly ->
                val constraints = Constraints.Builder()
                    .setRequiredNetworkType(
                        if (wifiOnly) NetworkType.UNMETERED else NetworkType.CONNECTED
                    )
                    .build()

                val data = workDataOf(
                    DataSyncWorker.REQUEST_TYPE to requestType.name
                )

                val request = OneTimeWorkRequestBuilder<DataSyncWorker>()
                    .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                    .setConstraints(constraints)
                    .setInputData(data)
                    .addTag(DataSyncWorker.DATA_SYNC_TAG)
                    .build()

                workManager.enqueueUniqueWork(
                    uniqueWorkName = "refresh data",
                    existingWorkPolicy = ExistingWorkPolicy.REPLACE,
                    request = request
                )
            }
    }
}