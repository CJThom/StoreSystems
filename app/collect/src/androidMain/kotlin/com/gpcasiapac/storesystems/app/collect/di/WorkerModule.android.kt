package com.gpcasiapac.storesystems.app.collect.di

import com.gpcasiapac.storesystems.app.collect.sync.AndroidSyncTriggerCoordinator
import com.gpcasiapac.storesystems.app.collect.sync.SyncWorker
import com.gpcasiapac.storesystems.core.sync_queue.domain.coordinator.SyncTriggerCoordinator
import org.koin.androidx.workmanager.dsl.workerOf
import org.koin.core.module.Module
import org.koin.dsl.module

actual fun workerModules(): List<Module> = listOf(
    module {
        workerOf(::SyncWorker)
        
        // Provide platform-specific implementation of SyncTriggerCoordinator
        single<SyncTriggerCoordinator> { 
            AndroidSyncTriggerCoordinator(get()) 
        }
    }
)
