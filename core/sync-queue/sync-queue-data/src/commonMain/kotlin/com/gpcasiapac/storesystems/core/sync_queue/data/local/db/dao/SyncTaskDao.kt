package com.gpcasiapac.storesystems.core.sync_queue.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.gpcasiapac.storesystems.core.sync_queue.data.local.db.entity.SyncTaskEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Instant
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
@Dao
interface SyncTaskDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: SyncTaskEntity)
    
    @Update
    suspend fun updateTask(task: SyncTaskEntity)
    
    @Query("SELECT * FROM sync_tasks WHERE id = :taskId")
    suspend fun getTaskById(taskId: String): SyncTaskEntity?
    
    @Query("SELECT * FROM sync_tasks WHERE task_type = :taskType AND status = :status ORDER BY priority DESC, added_time ASC LIMIT :limit")
    suspend fun getTasksByTypeAndStatus(taskType: String, status: String, limit: Int): List<SyncTaskEntity>
    
    @Query("SELECT * FROM sync_tasks WHERE status = 'PENDING' ORDER BY priority DESC, added_time ASC LIMIT 1")
    suspend fun getNextPendingTask(): SyncTaskEntity?
    
    @Query("UPDATE sync_tasks SET no_of_attempts = no_of_attempts + 1, last_attempt_time = :lastAttemptTime, updated_time = :updatedTime WHERE id = :taskId")
    suspend fun incrementAttempt(taskId: String, lastAttemptTime: Instant, updatedTime: Instant)
    
    @Query("DELETE FROM sync_tasks WHERE (status = 'COMPLETED' OR status = 'FAILED') AND updated_time < :cutoffTime")
    suspend fun deleteOldTasks(cutoffTime: Instant): Int
    
    @Query("SELECT COUNT(*) FROM sync_tasks WHERE task_type = :taskType AND status = 'PENDING'")
    fun observePendingTasksCount(taskType: String): Flow<Int>
    
    @Query("SELECT * FROM sync_tasks WHERE status = 'PENDING' ORDER BY priority DESC, added_time ASC")
    fun observePendingTasks(): Flow<List<SyncTaskEntity>>
    
    @Query("DELETE FROM sync_tasks WHERE id = :taskId")
    suspend fun deleteTask(taskId: String)
    
    @Query("UPDATE sync_tasks SET status = 'PENDING', updated_time = :updatedTime WHERE status = 'FAILED' AND (:taskType IS NULL OR task_type = :taskType)")
    suspend fun resetFailedTasks(taskType: String?, updatedTime: Instant): Int
    
    @Query("SELECT * FROM sync_tasks WHERE task_id = :entityId")
    suspend fun getTasksByEntityId(entityId: String): List<SyncTaskEntity>
}