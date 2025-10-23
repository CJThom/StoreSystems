package com.gpcasiapac.storesystems.core.sync_queue.domain

import com.gpcasiapac.storesystems.core.sync_queue.api.coordinator.SyncTriggerCoordinator
import com.gpcasiapac.storesystems.core.sync_queue.domain.coordinator.DesktopSyncTriggerCoordinator
import org.koin.core.module.Module
import org.koin.dsl.module

actual fun workerModules(): List<Module> = listOf(
    module {
        single<SyncTriggerCoordinator> {
            DesktopSyncTriggerCoordinator()
        }
    }
)