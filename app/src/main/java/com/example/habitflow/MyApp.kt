package com.example.habitflow

import android.app.Application
import android.util.Log
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.example.habitflow.data.StreakManager
import com.example.habitflow.data.background.DataSyncScheduler
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class MyApp : Application(),  Configuration.Provider {


    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    @Inject
    lateinit var dataSyncScheduler: DataSyncScheduler

    @Inject
    lateinit var streakManager: StreakManager

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    private val scope = CoroutineScope(Dispatchers.Default  + SupervisorJob())

    override fun onCreate() {
        super.onCreate()
        scope.launch {
            dataSyncScheduler.scheduleDataPull()
            streakManager.checkStreakExpiration()
        }
    }
}