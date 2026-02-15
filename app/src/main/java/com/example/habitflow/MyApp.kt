package com.example.habitflow

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.example.habitflow.data.StreakManager
import com.example.habitflow.data.background.RandomAdviceWorker
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class MyApp : Application(),  Configuration.Provider{


    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    @Inject
    lateinit var streakManager: StreakManager

    private val scope = CoroutineScope(Dispatchers.IO)

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()
        RandomAdviceWorker.enqueue(this)
        scope.launch {
            streakManager.checkStreakExpiration()
        }
    }


}