package com.gpcasiapac.storesystems.core.sync_queue.api.coordinator

/**
 * Platform-agnostic interface for triggering sync operations.
 * Each platform implements this using their respective background job system:
 * - Android: WorkManager
 * - iOS: BGTaskScheduler (future)
 * - JVM: Coroutines (future)
 */
interface SyncTriggerCoordinator {
    /**
     * Trigger immediate sync (subject to platform constraints like network).
     */
    fun triggerSync()
    
    /**
     * Cancel all pending sync work.
     */
    fun cancelSync()
}
