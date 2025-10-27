package com.gpcasiapac.storesystems.feature.collect.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.gpcasiapac.storesystems.feature.collect.data.local.db.entity.CollectWorkOrderEntity
import com.gpcasiapac.storesystems.feature.collect.data.local.db.entity.CollectWorkOrderItemEntity
import com.gpcasiapac.storesystems.feature.collect.data.local.db.relation.WorkOrderWithOrderWithCustomersRelation
import com.gpcasiapac.storesystems.feature.collect.data.local.db.relation.CollectOrderWithCustomerRelation
import com.gpcasiapac.storesystems.feature.collect.data.local.db.relation.WorkOrderItemWithOrderWithCustomerRelation
import kotlinx.coroutines.flow.Flow
import kotlin.time.Instant

@Dao
interface WorkOrderDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertWorkOrder(entity: CollectWorkOrderEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertItems(items: List<CollectWorkOrderItemEntity>): List<Long>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertItem(item: CollectWorkOrderItemEntity): Long

    @Query("SELECT COALESCE(MAX(position), 0) FROM work_order_items WHERE work_order_id = :workOrderId")
    suspend fun getMaxPosition(workOrderId: String): Long

    @Query(
        """
        UPDATE work_orders
        SET collecting_type = :type
        WHERE work_order_id = :id
        """
    )
    suspend fun setCollectingType(id: String, type: com.gpcasiapac.storesystems.feature.collect.domain.model.CollectingType)

    @Query(
        """
        UPDATE work_orders
        SET courier_name = :name
        WHERE work_order_id = :id
        """
    )
    suspend fun setCourierName(id: String, name: String)


    @Transaction
    @Query("SELECT * FROM work_orders WHERE work_order_id = :id")
    suspend fun getWorkOrder(id: String): WorkOrderWithOrderWithCustomersRelation?

    @Transaction
    @Query("SELECT * FROM work_orders WHERE work_order_id = :id")
    fun observeWorkOrder(id: String): Flow<WorkOrderWithOrderWithCustomersRelation?>

    @Transaction
    @Query("SELECT * FROM work_orders WHERE user_id = :userId ORDER BY created_at DESC")
    fun observeOpenWorkOrdersForUser(userId: String): Flow<List<WorkOrderWithOrderWithCustomersRelation>>

    @Transaction
    @Query("SELECT * FROM work_orders WHERE user_id = :userId ORDER BY created_at DESC LIMIT 1")
    fun observeLatestOpenWorkOrderForUser(userId: String): Flow<WorkOrderWithOrderWithCustomersRelation?>

    @Query("SELECT * FROM work_orders WHERE user_id = :userId ORDER BY created_at DESC LIMIT 1")
    suspend fun getOpenWorkOrderForUser(userId: String): CollectWorkOrderEntity?

    // New: just the latest open work order id for user
    @Query(
        "SELECT work_order_id FROM work_orders WHERE user_id = :userId ORDER BY created_at DESC LIMIT 1"
    )
    fun observeLatestOpenWorkOrderId(userId: String): Flow<String?>

    @Query("DELETE FROM work_orders WHERE work_order_id = :workOrderId")
    suspend fun deleteWorkOrder(workOrderId: String)

    @Query("DELETE FROM work_order_items WHERE work_order_id = :workOrderId AND invoice_number = :invoiceNumber")
    suspend fun deleteWorkOrderItem(workOrderId: String, invoiceNumber: String)

    @Query("DELETE FROM work_order_items WHERE work_order_id = :workOrderId")
    suspend fun deleteAllItemsForWorkOrder(workOrderId: String)

    @Query("SELECT invoice_number FROM work_order_items WHERE work_order_id = :workOrderId")
    fun observeSelectedInvoiceNumbers(workOrderId: String): Flow<List<String>>

    @Query("SELECT COUNT(invoice_number) FROM work_order_items WHERE work_order_id = :workOrderId")
    suspend fun getWorkOrderItemCount(workOrderId: String): Int

    @Query("DELETE FROM work_order_items WHERE work_order_id = :workOrderId AND invoice_number IN (:invoiceNumbers)")
    suspend fun deleteItemsForWorkOrder(workOrderId: String, invoiceNumbers: List<String>)

    // Ordered list of invoice numbers for a Work Order (by position ASC)
    @Query(
        "SELECT invoice_number FROM work_order_items WHERE work_order_id = :workOrderId ORDER BY position ASC"
    )
    fun observeInvoiceNumbersInScanOrder(workOrderId: String): Flow<List<String>>

    // New: ordered work order items with their nested order+customer
    @Transaction
    @Query(
        "SELECT * FROM work_order_items WHERE work_order_id = :workOrderId ORDER BY position ASC"
    )
    fun observeWorkOrderItemsWithOrders(
        workOrderId: String
    ): Flow<List<WorkOrderItemWithOrderWithCustomerRelation>>
}
