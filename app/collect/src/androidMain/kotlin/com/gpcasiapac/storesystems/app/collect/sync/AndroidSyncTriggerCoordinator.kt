package com.gpcasiapac.storesystems.app.collect.sync

import android.content.Context
import androidx.work.*
import com.gpcasiapac.storesystems.core.sync_queue.api.coordinator.SyncTriggerCoordinator
import java.util.concurrent.TimeUnit

/**
 * Android implementation of SyncTriggerCoordinator using WorkManager.
 */
class AndroidSyncTriggerCoordinator(
    private val context: Context
) : SyncTriggerCoordinator {
    
    companion object {
        private const val SYNC_WORK_NAME = "sync_pump"
    }
    
    override fun triggerSync() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val request = OneTimeWorkRequestBuilder<SyncWorker>()
            .setConstraints(constraints)
            .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 10, TimeUnit.SECONDS)
            .addTag("sync_pump")
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            SYNC_WORK_NAME,
            ExistingWorkPolicy.KEEP, // Don't start duplicate work
            request
        )
    }
    
    override fun cancelSync() {
        WorkManager.getInstance(context).cancelUniqueWork(SYNC_WORK_NAME)
    }
}
