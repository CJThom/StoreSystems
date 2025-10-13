package com.gpcasiapac.storesystems.feature.collect.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.gpcasiapac.storesystems.feature.collect.data.local.db.entity.CollectOrderCustomerEntity
import com.gpcasiapac.storesystems.feature.collect.data.local.db.entity.CollectOrderEntity
import com.gpcasiapac.storesystems.feature.collect.data.local.db.entity.CollectOrderLineItemEntity
import com.gpcasiapac.storesystems.feature.collect.data.local.db.entity.CollectWorkOrderEntity
import com.gpcasiapac.storesystems.feature.collect.data.local.db.relation.CollectOrderWithCustomerRelation
import com.gpcasiapac.storesystems.feature.collect.data.local.db.relation.CollectOrderWithCustomerWithLineItemsRelation
import com.gpcasiapac.storesystems.feature.collect.domain.model.CollectOrderWithCustomer
import kotlinx.coroutines.flow.Flow

@Dao
interface CollectOrderDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrReplaceCollectOrderEntityList(collectOrderEntityList: List<CollectOrderEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrReplaceCollectOrderCustomerEntityList(collectOrderCustomerEntity: List<CollectOrderCustomerEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrReplaceCollectOrderLineItemEntityList(collectOrderLineItemEntityList: List<CollectOrderLineItemEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrReplaceOrderEntity(collectOrderEntity: CollectOrderEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrReplaceOrderEntity(collectOrderEntityList: List<CollectOrderEntity>)

    @Query("SELECT COUNT(*) FROM collect_orders")
    suspend fun getCount(): Int

    @Transaction
    @Query("SELECT * FROM collect_orders ORDER BY picked_at DESC")
    fun getCollectOrderWithCustomerWithLineItemsRelationListFlow(): Flow<List<CollectOrderWithCustomerWithLineItemsRelation>>

    @Transaction
    @Query("SELECT * FROM collect_orders ORDER BY picked_at DESC")
    fun getCollectOrderWithCustomerRelationListFlow(): Flow<List<CollectOrderWithCustomerRelation>>

    @Transaction
    @Query("SELECT * FROM collect_orders where invoice_number = :invoiceNumber")
    fun getCollectOrderWithCustomerWithLineItemsRelationFlow(invoiceNumber:String): Flow<CollectOrderWithCustomerWithLineItemsRelation>

//    // Lightweight suggestion queries (prefix match) returning only needed columns, limited per type
//    @Query(
//        """
//        SELECT COALESCE(account_name, TRIM((COALESCE(first_name, '') || ' ' || COALESCE(last_name, '')))) AS display_name
//        FROM collect_orders
//        WHERE COALESCE(account_name, TRIM((COALESCE(first_name, '') || ' ' || COALESCE(last_name, '')))) LIKE :prefix ESCAPE '!' COLLATE NOCASE
//        LIMIT :limit
//        """
//    )
//    suspend fun getNameSuggestionsPrefix(prefix: String, limit: Int): List<String>

    @Query(
        """
        SELECT invoice_number FROM collect_orders
        WHERE invoice_number LIKE :prefix ESCAPE '!' COLLATE NOCASE
        LIMIT :limit
        """
    )
    suspend fun getInvoiceSuggestionsPrefix(prefix: String, limit: Int): List<String>

    @Query(
        """
        SELECT web_order_number FROM collect_orders
        WHERE web_order_number IS NOT NULL
          AND web_order_number LIKE :prefix ESCAPE '!' COLLATE NOCASE
        LIMIT :limit
        """
    )
    suspend fun getWebOrderSuggestionsPrefix(prefix: String, limit: Int): List<String>

    @Query("UPDATE collect_orders SET signature = :signature WHERE invoice_number IN (:invoiceNumbers)")
    suspend fun updateSignature(signature: String, invoiceNumbers: List<String>)

    @Query("SELECT invoice_number FROM collect_work_orders WHERE user_id = :userId")
    fun getSelectedInvoiceNumbers(userId: String): Flow<List<String>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addSelectedInvoiceNumber(workOrder: CollectWorkOrderEntity)

    @Query("DELETE FROM collect_work_orders WHERE user_id = :userId AND invoice_number = :invoiceNumber")
    suspend fun removeSelectedInvoiceNumber(userId: String, invoiceNumber: String)

    @Query("DELETE FROM collect_work_orders WHERE user_id = :userId")
    suspend fun clearSelectedInvoiceNumbers(userId: String)

    @Transaction
    suspend fun setSelectedInvoiceNumbers(userId: String, invoiceNumbers: List<String>) {
        clearSelectedInvoiceNumbers(userId)
        val workOrders = invoiceNumbers.map { CollectWorkOrderEntity(userId, it) }
        insertWorkOrders(workOrders)
    }

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertWorkOrders(workOrders: List<CollectWorkOrderEntity>)
}
