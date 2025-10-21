package com.gpcasiapac.storesystems.core.sync_queue.domain

import com.gpcasiapac.storesystems.core.sync_queue.domain.model.SyncTask
import com.gpcasiapac.storesystems.core.sync_queue.domain.model.TaskType

/**
 * Feature modules implement this to handle specific task types.
 * Keep contract minimal for v1 using Result<Unit>.
 */
interface SyncHandler {
    /** Task types this handler can process */
    val supportedTypes: Set<TaskType>

    /** Perform the task. Return Result.success(Unit) on success.
     * For failures, return Result.failure(Throwable). Use the provided exceptions to indicate intent. */
    suspend fun handle(task: SyncTask): Result<Unit>
}
