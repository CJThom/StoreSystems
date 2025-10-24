package com.gpcasiapac.storesystems.core.sync_queue.domain

import com.gpcasiapac.storesystems.core.sync_queue.api.SyncHandler
import com.gpcasiapac.storesystems.core.sync_queue.api.coordinator.SyncTriggerCoordinator
import com.gpcasiapac.storesystems.core.sync_queue.domain.coordinator.AndroidSyncTriggerCoordinator
import com.gpcasiapac.storesystems.core.sync_queue.domain.coordinator.SyncWorker
import org.koin.androidx.workmanager.dsl.worker
import org.koin.core.module.Module
import org.koin.dsl.module

actual fun workerModules(): List<Module> = listOf(
    module {
        // Manually construct SyncWorker with all registered handlers
        worker {
            SyncWorker(
                appContext = get(),
                params = get(),
                handlers = getAll<SyncHandler>(),  // Collect all SyncHandler instances
                repo = get()
            )
        }
        
        single<SyncTriggerCoordinator>{
            AndroidSyncTriggerCoordinator(get())
        }
    }
)