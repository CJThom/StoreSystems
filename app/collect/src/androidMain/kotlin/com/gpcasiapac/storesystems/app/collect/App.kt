package com.gpcasiapac.storesystems.app.collect

import android.app.Application
import androidx.work.Configuration
import com.gpcasiapac.storesystems.app.collect.sync.SyncScheduler
import com.gpcasiapac.storesystems.app.collect.work.AppWorkerFactory

class App : Application(), Configuration.Provider {
    override val workManagerConfiguration: Configuration =
        Configuration.Builder()
            .setWorkerFactory(AppWorkerFactory())
            .build()

    override fun onCreate() {
        super.onCreate()
        // Schedule periodic sync at app startup
        SyncScheduler.schedulePeriodic(this)
    }
}
