package com.gpcasiapac.storesystems.feature.collect.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.gpcasiapac.storesystems.feature.collect.data.local.db.entity.SignatureEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SignatureDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: SignatureEntity)

    @Query("SELECT * FROM signatures WHERE work_order_id = :workOrderId LIMIT 1")
    suspend fun getByWorkOrderId(workOrderId: String): SignatureEntity?

    @Query("SELECT * FROM signatures WHERE work_order_id = :workOrderId LIMIT 1")
    fun observeByWorkOrderId(workOrderId: String): Flow<SignatureEntity?>

    @Query("DELETE FROM signatures WHERE work_order_id = :workOrderId")
    suspend fun deleteByWorkOrderId(workOrderId: String)

    @Transaction
    suspend fun replaceForWorkOrder(entity: SignatureEntity) {
        // Just upsert since PK work_order_id enforces 1:1
        upsert(entity)
    }
}