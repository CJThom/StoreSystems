package com.gpcasiapac.storesystems.core.sync_queue.domain

import com.gpcasiapac.storesystems.core.sync_queue.api.coordinator.SyncTriggerCoordinator
import com.gpcasiapac.storesystems.core.sync_queue.domain.coordinator.AndroidSyncTriggerCoordinator
import com.gpcasiapac.storesystems.core.sync_queue.domain.coordinator.SyncWorker
import org.koin.androidx.workmanager.dsl.workerOf
import org.koin.core.module.Module
import org.koin.dsl.module

actual fun workerModules(): List<Module> = listOf(
    module {
        workerOf(::SyncWorker)
        single<SyncTriggerCoordinator>{
            AndroidSyncTriggerCoordinator(get())
        }
    }
)