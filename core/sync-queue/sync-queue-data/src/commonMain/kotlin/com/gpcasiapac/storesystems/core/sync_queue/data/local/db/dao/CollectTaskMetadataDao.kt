@file:OptIn(ExperimentalTime::class)

package com.gpcasiapac.storesystems.core.sync_queue.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.gpcasiapac.storesystems.core.sync_queue.data.local.db.entity.CollectTaskMetadataEntity
import kotlinx.coroutines.flow.Flow
import kotlin.time.ExperimentalTime

/**
 * DAO for CollectTaskMetadataEntity operations.
 */
@Dao
interface CollectTaskMetadataDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(metadata: CollectTaskMetadataEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(metadata: List<CollectTaskMetadataEntity>)
    
    @Update
    suspend fun update(metadata: CollectTaskMetadataEntity)
    
    @Query("SELECT * FROM collect_task_metadata WHERE id = :id")
    suspend fun getById(id: String): CollectTaskMetadataEntity?
    
    @Query("SELECT * FROM collect_task_metadata WHERE sync_task_id = :syncTaskId")
    suspend fun getBySyncTaskId(syncTaskId: String): CollectTaskMetadataEntity?
    
    @Query("SELECT * FROM collect_task_metadata WHERE invoice_number = :invoiceNumber")
    suspend fun getByInvoiceNumber(invoiceNumber: String): List<CollectTaskMetadataEntity>
    
    @Query("SELECT * FROM collect_task_metadata WHERE customer_number = :customerNumber")
    suspend fun getByCustomerNumber(customerNumber: String): List<CollectTaskMetadataEntity>
    
    @Query("DELETE FROM collect_task_metadata WHERE id = :id")
    suspend fun delete(id: String)
    
    @Query("DELETE FROM collect_task_metadata WHERE sync_task_id = :syncTaskId")
    suspend fun deleteBySyncTaskId(syncTaskId: String)
}
