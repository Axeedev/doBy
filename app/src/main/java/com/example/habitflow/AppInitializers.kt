package com.example.habitflow

import android.app.Application
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.example.habitflow.data.StreakManager
import com.example.habitflow.data.background.DataSyncScheduler
import com.example.habitflow.data.background.RandomAdviceWorker
import kotlinx.coroutines.launch
import javax.inject.Inject

class AppInitializers @Inject constructor(
    private val streakManager: StreakManager,
    private val dataSyncScheduler: DataSyncScheduler
) {
    fun init(application: Application) {
        ProcessLifecycleOwner.get().lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onCreate(owner: LifecycleOwner) {
                owner.lifecycleScope.launch {
                    dataSyncScheduler.scheduleDataPull()
                    dataSyncScheduler.schedulePeriodSync()
                    streakManager.checkStreakExpiration()
                }
                RandomAdviceWorker.enqueue(application)
            }
        })
    }
}