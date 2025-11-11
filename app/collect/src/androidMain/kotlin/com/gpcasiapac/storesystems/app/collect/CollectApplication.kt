package com.gpcasiapac.storesystems.app.collect

import android.app.Application
import com.gpcasiapac.storesystems.app.collect.di.getAppModules
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.workmanager.koin.workManagerFactory
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class CollectApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        
        startKoin {
            androidLogger(Level.ERROR)
            androidContext(this@CollectApplication)
            workManagerFactory() // Enables Koin Worker factory for DI
            modules(getAppModules())
        }
        
        // WorkManager auto-initializes with default configuration
    }
}
