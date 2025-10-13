package com.gpcasiapac.storesystems.feature.collect.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.gpcasiapac.storesystems.feature.collect.data.local.db.entity.CollectWorkOrderEntity
import com.gpcasiapac.storesystems.feature.collect.data.local.db.entity.CollectWorkOrderItemEntity
import com.gpcasiapac.storesystems.feature.collect.data.local.db.relation.WorkOrderWithOrders
import kotlinx.coroutines.flow.Flow
import kotlin.time.Instant

@Dao
interface WorkOrderDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertWorkOrder(entity: CollectWorkOrderEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertItems(items: List<CollectWorkOrderItemEntity>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertItem(item: CollectWorkOrderItemEntity)

    @Query(
        """
        UPDATE work_orders
        SET signature = :signature, signed_at = :signedAt, signed_by_name = :signedBy
        WHERE work_order_id = :id
        """
    )
    suspend fun attachSignature(id: String, signature: String, signedAt: Instant, signedBy: String?)

    @Query("UPDATE work_orders SET status = 'SUBMITTED', submitted_at = :now WHERE work_order_id = :id")
    suspend fun markSubmitted(id: String, now: Instant)

    @Transaction
    @Query("SELECT * FROM work_orders WHERE work_order_id = :id")
    suspend fun getWorkOrder(id: String): WorkOrderWithOrders?

    @Transaction
    @Query("SELECT * FROM work_orders WHERE user_id = :userId AND status = 'OPEN' ORDER BY created_at DESC")
    fun observeOpenWorkOrdersForUser(userId: String): Flow<List<WorkOrderWithOrders>>

    @Transaction
    @Query("SELECT * FROM work_orders WHERE user_id = :userId AND status = 'OPEN' ORDER BY created_at DESC LIMIT 1")
    fun observeLatestOpenWorkOrderForUser(userId: String): Flow<WorkOrderWithOrders?>

    @Query("SELECT * FROM work_orders WHERE user_id = :userId AND status = 'OPEN' ORDER BY created_at DESC LIMIT 1")
    suspend fun getOpenWorkOrderForUser(userId: String): CollectWorkOrderEntity?

    @Query("DELETE FROM work_orders WHERE work_order_id = :workOrderId")
    suspend fun deleteWorkOrder(workOrderId: String)

    @Query("DELETE FROM work_order_items WHERE work_order_id = :workOrderId AND invoice_number = :invoiceNumber")
    suspend fun deleteWorkOrderItem(workOrderId: String, invoiceNumber: String)

    @Query("SELECT COUNT(invoice_number) FROM work_order_items WHERE work_order_id = :workOrderId")
    suspend fun getWorkOrderItemCount(workOrderId: String): Int
}
