package com.gpcasiapac.storesystems.feature.collect.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.gpcasiapac.storesystems.feature.collect.api.model.InvoiceNumber
import com.gpcasiapac.storesystems.feature.collect.data.local.db.entity.CollectWorkOrderEntity
import com.gpcasiapac.storesystems.feature.collect.data.local.db.entity.CollectWorkOrderItemEntity
import com.gpcasiapac.storesystems.feature.collect.data.local.db.relation.WorkOrderWithOrderWithCustomersRelation
import com.gpcasiapac.storesystems.feature.collect.data.local.db.relation.WorkOrderItemWithOrderWithCustomerRelation
import com.gpcasiapac.storesystems.feature.collect.data.local.db.relation.WorkOrderItemWithOrderWithCustomerWithLineItemsRelation
import com.gpcasiapac.storesystems.feature.collect.domain.model.CollectingType
import com.gpcasiapac.storesystems.feature.collect.domain.model.value.WorkOrderId
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkOrderDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertWorkOrder(entity: CollectWorkOrderEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrReplaceWorkOrderEntity(collectWorkOrderEntity: CollectWorkOrderEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertOrIgnoreWorkOrderItemList(workOrderItemList: List<CollectWorkOrderItemEntity>): List<Long>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertOrIgnoreWorkOrderItem(workOrderItem: CollectWorkOrderItemEntity): Long

    @Query("SELECT COALESCE(MAX(position), 0) FROM work_order_items WHERE work_order_id = :workOrderId")
    suspend fun getMaxPosition(workOrderId: WorkOrderId): Long

    @Query("UPDATE work_orders SET collecting_type = :type WHERE work_order_id = :workOrderId")
    suspend fun setCollectingType(workOrderId: WorkOrderId, type: CollectingType)

    @Query("UPDATE work_orders SET courier_name = :name WHERE work_order_id = :workOrderId")
    suspend fun setCourierName(workOrderId: WorkOrderId, name: String)

    @Transaction
    @Query("SELECT * FROM work_orders WHERE work_order_id = :workOrderId")
    suspend fun getWorkOrder(workOrderId: WorkOrderId): WorkOrderWithOrderWithCustomersRelation?

    @Transaction
    @Query("SELECT * FROM work_orders WHERE work_order_id = :workOrderId")
    fun observeWorkOrder(workOrderId: WorkOrderId): Flow<WorkOrderWithOrderWithCustomersRelation?>

    @Transaction
    @Query("SELECT * FROM work_orders WHERE work_order_id = :workOrderId ORDER BY created_at DESC LIMIT 1")
    fun getWorkOrderWithOrderWithCustomerRelationFlow(workOrderId: WorkOrderId): Flow<WorkOrderWithOrderWithCustomersRelation?>

    @Query("SELECT * FROM work_orders WHERE work_order_id = :workOrderId ORDER BY created_at DESC LIMIT 1")
    suspend fun getCollectWorkOrderEntity(workOrderId: WorkOrderId): CollectWorkOrderEntity?

    @Query("SELECT * FROM work_orders WHERE user_id = :userId ORDER BY created_at DESC LIMIT 1")
    suspend fun getCollectWorkOrderEntity(userId: String): CollectWorkOrderEntity?

    @Query("DELETE FROM work_orders WHERE work_order_id = :workOrderId")
    suspend fun deleteWorkOrder(workOrderId: WorkOrderId)

    @Query("DELETE FROM work_order_items WHERE work_order_id = :workOrderId AND invoice_number = :invoiceNumber")
    suspend fun deleteWorkOrderItem(workOrderId: WorkOrderId, invoiceNumber: InvoiceNumber)

    @Query("DELETE FROM work_order_items WHERE work_order_id = :workOrderId")
    suspend fun deleteAllItemsForWorkOrder(workOrderId: WorkOrderId)

    @Query("SELECT COUNT(invoice_number) FROM work_order_items WHERE work_order_id = :workOrderId")
    suspend fun getWorkOrderItemCount(workOrderId: WorkOrderId): Int

    @Query("DELETE FROM work_order_items WHERE work_order_id = :workOrderId AND invoice_number IN (:invoiceNumberList)")
    suspend fun deleteItemsForWorkOrder(
        workOrderId: WorkOrderId,
        invoiceNumberList: List<InvoiceNumber>
    )

    // New: ordered work order items with their nested order+customer
    @Transaction
    @Query("SELECT * FROM work_order_items WHERE work_order_id = :workOrderId ORDER BY position ASC")
    fun observeWorkOrderItemsWithOrders(workOrderId: WorkOrderId): Flow<List<WorkOrderItemWithOrderWithCustomerRelation>>

    @Transaction
    @Query("SELECT * FROM work_order_items WHERE work_order_id = :workOrderId ORDER BY position ASC")
    fun getWorkOrderItemWithOrderWithCustomerWithLineItemsRelationListFlow(workOrderId: WorkOrderId): Flow<List<WorkOrderItemWithOrderWithCustomerWithLineItemsRelation>>

}
