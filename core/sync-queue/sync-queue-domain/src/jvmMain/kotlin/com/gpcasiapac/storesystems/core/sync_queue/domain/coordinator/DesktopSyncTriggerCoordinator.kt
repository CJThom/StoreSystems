package com.gpcasiapac.storesystems.core.sync_queue.domain.coordinator

import com.gpcasiapac.storesystems.core.sync_queue.api.coordinator.SyncTriggerCoordinator

/**
 * Desktop (JVM) implementation of SyncTriggerCoordinator.
 * Currently uses println placeholders for demonstration.
 * Future implementation could use Quartz Scheduler or Kotlin coroutines.
 */
class DesktopSyncTriggerCoordinator : SyncTriggerCoordinator {

    override fun triggerSync() {
        println("Desktop: Sync triggered (no-op)")
    }

    override fun cancelSync() {
        println("Desktop: Sync cancelled (no-op)")
    }
}