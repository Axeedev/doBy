package com.example.habitflow

import android.app.Application
import android.util.Log
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
        Log.d("AppInitializers", "onCreate Init")
        ProcessLifecycleOwner.get().lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onResume(owner: LifecycleOwner) {
                Log.d("AppInitializers", "onCreate")
                owner.lifecycleScope.launch {
                    Log.d("AppInitializers", "scheduled")
                    dataSyncScheduler.scheduleDataPull()
                    dataSyncScheduler.schedulePeriodSync()
                    streakManager.checkStreakExpiration()
                }
                RandomAdviceWorker.enqueue(application)
            }
        })
    }
}