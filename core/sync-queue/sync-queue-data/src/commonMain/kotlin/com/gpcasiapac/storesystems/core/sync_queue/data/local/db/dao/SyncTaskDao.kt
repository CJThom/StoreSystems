@file:OptIn(ExperimentalTime::class)

package com.gpcasiapac.storesystems.core.sync_queue.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.gpcasiapac.storesystems.core.sync_queue.data.local.db.entity.SyncTaskEntity
import com.gpcasiapac.storesystems.core.sync_queue.data.local.db.entity.SyncTaskWithCollectMetadataEntity
import kotlinx.coroutines.flow.Flow
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@Dao
interface SyncTaskDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: SyncTaskEntity)

    @Update
    suspend fun updateTask(task: SyncTaskEntity)

    @Query("SELECT * FROM sync_tasks WHERE id = :taskId")
    suspend fun getTaskById(taskId: String): SyncTaskEntity?

    @Query("SELECT * FROM sync_tasks WHERE task_type = :taskType AND status = :status ORDER BY priority DESC, added_time ASC LIMIT :limit")
    suspend fun getTasksByTypeAndStatus(
        taskType: String,
        status: String,
        limit: Int
    ): List<SyncTaskEntity>

    @Query("SELECT * FROM sync_tasks WHERE status IN ('PENDING','FAILED') AND no_of_attempts < max_attempts ORDER BY priority DESC, added_time ASC LIMIT 1")
    suspend fun getNextPendingTask(): SyncTaskEntity?

    @Query("UPDATE sync_tasks SET no_of_attempts = no_of_attempts + 1, last_attempt_time = :lastAttemptTime, updated_time = :updatedTime WHERE id = :taskId")
    suspend fun incrementAttempt(taskId: String, lastAttemptTime: Instant, updatedTime: Instant)

    /**
     * Atomically set IN_PROGRESS and increment attempts if eligible.
     * Returns number of rows affected (1 if started, 0 otherwise).
     */
    @Query("UPDATE sync_tasks SET status = 'IN_PROGRESS', no_of_attempts = no_of_attempts + 1, last_attempt_time = :now, updated_time = :now WHERE id = :taskId AND status IN ('PENDING','FAILED') AND no_of_attempts < max_attempts")
    suspend fun startAttempt(taskId: String, now: Instant): Int

    @Query("DELETE FROM sync_tasks WHERE (status = 'COMPLETED' OR status = 'FAILED') AND updated_time < :cutoffTime")
    suspend fun deleteOldTasks(cutoffTime: Instant): Int

    @Query("SELECT COUNT(*) FROM sync_tasks WHERE task_type = :taskType AND status = 'PENDING'")
    fun observePendingTasksCount(taskType: String): Flow<Int>

    @Query("SELECT * FROM sync_tasks WHERE status = 'PENDING' ORDER BY priority DESC, added_time ASC")
    fun observePendingTasks(): Flow<List<SyncTaskEntity>>

    @Query("SELECT * FROM sync_tasks ORDER BY updated_time DESC")
    fun observeAllTasks(): Flow<List<SyncTaskEntity>>

    @Query("DELETE FROM sync_tasks WHERE id = :taskId")
    suspend fun deleteTask(taskId: String)

    @Query("UPDATE sync_tasks SET status = 'PENDING', updated_time = :updatedTime WHERE status = 'FAILED' AND (:taskType IS NULL OR task_type = :taskType)")
    suspend fun resetFailedTasks(taskType: String?, updatedTime: Instant): Int

    @Query("SELECT * FROM sync_tasks WHERE task_id = :entityId")
    suspend fun getTasksByEntityId(entityId: String): List<SyncTaskEntity>

    /**
     * Get task with collect metadata by task ID using JOIN.
     */
    @Transaction
    @Query("SELECT * FROM sync_tasks WHERE id = :taskId")
    suspend fun getTaskWithCollectMetadata(taskId: String): SyncTaskWithCollectMetadataEntity?

    /**
     * Observe all tasks with collect metadata.
     */
    @Transaction
    @Query("SELECT * FROM sync_tasks ORDER BY added_time DESC")
    fun observeAllTasksWithCollectMetadata(): Flow<List<SyncTaskWithCollectMetadataEntity>>

    /**
     * Observe pending tasks with collect metadata.
     */
    @Transaction
    @Query("SELECT * FROM sync_tasks WHERE status = 'PENDING' ORDER BY priority DESC, added_time ASC")
    fun observePendingTasksWithCollectMetadata(): Flow<List<SyncTaskWithCollectMetadataEntity>>

    /**
     * Get tasks by invoice number through metadata JOIN.
     */
    @Transaction
    @Query(
        """
        SELECT sync_tasks.* FROM sync_tasks
        INNER JOIN collect_task_metadata ON sync_tasks.id = collect_task_metadata.sync_task_id
        WHERE collect_task_metadata.invoice_number = :invoiceNumber
    """
    )
    suspend fun getTasksByInvoiceNumber(invoiceNumber: String): List<SyncTaskWithCollectMetadataEntity>

    /**
     * Get tasks by customer number through metadata JOIN.
     */
    @Transaction
    @Query(
        """
        SELECT sync_tasks.* FROM sync_tasks
        INNER JOIN collect_task_metadata ON sync_tasks.id = collect_task_metadata.sync_task_id
        WHERE collect_task_metadata.customer_number = :customerNumber
    """
    )
    suspend fun getTasksByCustomerNumber(customerNumber: String): List<SyncTaskWithCollectMetadataEntity>

    /**
     * Get tasks by entity ID with metadata.
     */
    @Transaction
    @Query("SELECT * FROM sync_tasks WHERE task_id = :entityId")
    suspend fun getTasksWithCollectMetadataByEntityId(entityId: String): List<SyncTaskWithCollectMetadataEntity>

    @Transaction
    @Query("SELECT * FROM sync_tasks WHERE id = :taskId")
    fun observeTasksWithCollectMetadataByTaskIdFlow(taskId: String): Flow<SyncTaskWithCollectMetadataEntity>
}