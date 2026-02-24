package com.example.habitflow

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class MyApp : Application(),  Configuration.Provider {


    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    @Inject
    lateinit var appInitializers: AppInitializers

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()
        appInitializers.init(this)

    }
}