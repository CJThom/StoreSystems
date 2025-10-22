package com.gpcasiapac.storesystems.app.collect.di

import com.gpcasiapac.storesystems.app.collect.sync.DesktopSyncTriggerCoordinator
import com.gpcasiapac.storesystems.core.sync_queue.api.coordinator.SyncTriggerCoordinator
import com.gpcasiapac.storesystems.core.sync_queue.data.syncQueueDataModule
import org.koin.core.module.Module
import org.koin.dsl.module

actual fun workerModules(): List<Module> = listOf(
    syncQueueDataModule,
    module {
        // Provide platform-specific implementation of SyncTriggerCoordinator for Desktop
        single<SyncTriggerCoordinator> { 
            DesktopSyncTriggerCoordinator() 
        }
    }
)
