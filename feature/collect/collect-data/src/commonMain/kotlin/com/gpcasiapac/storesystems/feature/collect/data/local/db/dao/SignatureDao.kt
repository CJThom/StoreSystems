package com.gpcasiapac.storesystems.feature.collect.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.gpcasiapac.storesystems.feature.collect.data.local.db.entity.SignatureEntity
import com.gpcasiapac.storesystems.feature.collect.domain.model.value.WorkOrderId
import kotlinx.coroutines.flow.Flow

@Dao
interface SignatureDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrReplaceSignatureEntity(signatureEntity: SignatureEntity)

    @Query("SELECT * FROM signatures WHERE work_order_id = :workOrderId LIMIT 1")
    suspend fun getByWorkOrderId(workOrderId: WorkOrderId): SignatureEntity?

    @Query("SELECT * FROM signatures WHERE work_order_id = :workOrderId LIMIT 1")
    fun getSignatureEntityFlow(workOrderId: WorkOrderId): Flow<SignatureEntity?>

    @Query("DELETE FROM signatures WHERE work_order_id = :workOrderId")
    suspend fun deleteByWorkOrderId(workOrderId: WorkOrderId)

}