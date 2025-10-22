package com.gpcasiapac.storesystems.app.collect

import android.app.Application
import androidx.work.Configuration
import androidx.work.WorkerFactory
import com.gpcasiapac.storesystems.app.collect.di.getAppModules
import org.koin.android.ext.android.get
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.workmanager.koin.workManagerFactory
import org.koin.core.context.startKoin

class App : Application(), Configuration.Provider {
    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(get<WorkerFactory>())
            .build()

    override fun onCreate() {
        super.onCreate()
        
        // Initialize Koin BEFORE WorkManager accesses configuration
        startKoin {
            androidContext(this@App)
            workManagerFactory()
            modules(getAppModules())
        }
    }
}
