package com.gpcasiapac.storesystems.feature.collect.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.gpcasiapac.storesystems.feature.collect.api.model.InvoiceNumber
import com.gpcasiapac.storesystems.feature.collect.data.local.db.entity.CollectOrderCustomerEntity
import com.gpcasiapac.storesystems.feature.collect.data.local.db.entity.CollectOrderEntity
import com.gpcasiapac.storesystems.feature.collect.data.local.db.entity.CollectOrderLineItemEntity
import com.gpcasiapac.storesystems.feature.collect.data.local.db.relation.CollectOrderWithCustomerRelation
import com.gpcasiapac.storesystems.feature.collect.data.local.db.relation.CollectOrderWithCustomerWithLineItemsRelation
import com.gpcasiapac.storesystems.feature.collect.data.local.db.relation.WorkOrderWithOrderWithCustomersWithLineItemsRelation
import com.gpcasiapac.storesystems.feature.collect.domain.model.CustomerType
import com.gpcasiapac.storesystems.feature.collect.domain.model.value.WorkOrderId
import kotlinx.coroutines.flow.Flow

@Dao
interface CollectOrderDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrReplaceCollectOrderEntityList(collectOrderEntityList: List<CollectOrderEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrReplaceCollectOrderCustomerEntityList(collectOrderCustomerEntity: List<CollectOrderCustomerEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrReplaceCollectOrderLineItemEntityList(collectOrderLineItemEntityList: List<CollectOrderLineItemEntity>)

    @Query("SELECT COUNT(*) FROM collect_orders")
    suspend fun getCount(): Int

    @Query("SELECT COUNT(*) FROM collect_orders")
    fun observeCount(): Flow<Int>

    // Observe count of customer rows to detect suggestion-relevant changes
    @Query("SELECT COUNT(*) FROM collect_order_customers")
    fun observeCustomerCount(): Flow<Int>

    @Transaction
    @Query("SELECT * FROM collect_orders ORDER BY created_date_time DESC")
    fun getCollectOrderWithCustomerWithLineItemsRelationListFlow(): Flow<List<CollectOrderWithCustomerWithLineItemsRelation>>

    @Transaction
    @Query("SELECT * FROM collect_orders ORDER BY created_date_time DESC")
    fun getCollectOrderWithCustomerRelationListFlow(): Flow<List<CollectOrderWithCustomerRelation>>

    @Transaction
    @Query("SELECT * FROM collect_orders WHERE invoice_number IN (:invoiceNumbers) ORDER BY created_date_time DESC")
    fun getCollectOrderWithCustomerRelationListFlow(invoiceNumbers: Set<String>): Flow<List<CollectOrderWithCustomerRelation>>

    // Main list: filtered by customer types and sorted by option (placeholder for name sort)
    @Transaction
    @Query(
        """
        SELECT * FROM collect_orders
        WHERE invoice_number IN (
           SELECT invoice_number FROM collect_order_customers
        )
        ORDER BY
          CASE WHEN :sort = 'TIME_WAITING_ASC' THEN created_date_time END ASC,
          CASE WHEN :sort = 'TIME_WAITING_DESC' THEN created_date_time END DESC,
          CASE WHEN :sort = 'NAME_ASC' THEN invoice_number END ASC,
          CASE WHEN :sort = 'NAME_DESC' THEN invoice_number END DESC
        """
    )
    fun observeOrdersForMainList(
        sort: String,
    ): Flow<List<CollectOrderWithCustomerRelation>>

    // Search list: filtered only by search text across order and customer fields
    @Transaction
    @Query(
        """
        SELECT * FROM collect_orders
        WHERE (
          invoice_number LIKE :q ESCAPE '!' COLLATE NOCASE OR
          (web_order_number IS NOT NULL AND web_order_number LIKE :q ESCAPE '!'
            COLLATE NOCASE) OR
          (order_number IS NOT NULL AND order_number LIKE :q ESCAPE '!'
            COLLATE NOCASE) OR
          invoice_number IN (
             SELECT invoice_number FROM collect_order_customers WHERE (
               (name IS NOT NULL AND name LIKE :q ESCAPE '!' COLLATE NOCASE)
               OR (phone IS NOT NULL AND phone LIKE :q ESCAPE '!' COLLATE NOCASE)
             )
          )
        )
        ORDER BY created_date_time DESC
        """
    )
    fun observeOrdersForSearch(q: String): Flow<List<CollectOrderWithCustomerRelation>>

    // Search list with optional text and required invoice scope
    @Transaction
    @Query(
        """
        SELECT * FROM collect_orders
        WHERE (:q IS NULL OR (
          invoice_number LIKE :q ESCAPE '!' COLLATE NOCASE OR
          (web_order_number IS NOT NULL AND web_order_number LIKE :q ESCAPE '!'
            COLLATE NOCASE) OR
          (order_number IS NOT NULL AND order_number LIKE :q ESCAPE '!'
            COLLATE NOCASE) OR
          invoice_number IN (
             SELECT invoice_number FROM collect_order_customers WHERE (
               (name IS NOT NULL AND name LIKE :q ESCAPE '!' COLLATE NOCASE)
               OR (phone IS NOT NULL AND phone LIKE :q ESCAPE '!' COLLATE NOCASE)
             )
          )
        ))
        AND invoice_number IN (:invoiceScope)
        ORDER BY created_date_time DESC
        """
    )
    fun observeOrdersForSearchScoped(
        q: String?,
        invoiceScope: Set<String>,
    ): Flow<List<CollectOrderWithCustomerRelation>>


    @Transaction
    @Query("SELECT * FROM collect_orders where invoice_number = :invoiceNumber")
    fun getCollectOrderWithCustomerWithLineItemsRelationFlow(invoiceNumber: InvoiceNumber): Flow<CollectOrderWithCustomerWithLineItemsRelation>

    @Transaction
    @Query("SELECT * FROM work_orders where work_order_id = :workOrderId")
    fun getWorkOrderWithOrderWithCustomersWithLineItemsRelationFlow(workOrderId: WorkOrderId): Flow<WorkOrderWithOrderWithCustomersWithLineItemsRelation?>


    // ---- Suggestions (prefix) ----
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

    @Query(
        """
        SELECT order_number FROM collect_orders
        WHERE order_number IS NOT NULL
          AND order_number LIKE :prefix ESCAPE '!' COLLATE NOCASE
        LIMIT :limit
        """
    )
    suspend fun getSalesOrderSuggestionsPrefix(prefix: String, limit: Int): List<String>

    @Query(
        """
        SELECT DISTINCT name AS name
        FROM collect_order_customers
        WHERE name IS NOT NULL AND name LIKE :prefix ESCAPE '!' COLLATE NOCASE
        LIMIT :limit
        """
    )
    suspend fun getCustomerNameSuggestionsPrefix(prefix: String, limit: Int): List<CustomerNameRow>

    // New: fetch all distinct customer names (for initial expanded state with empty query)
    @Query(
        """
        SELECT DISTINCT name AS name
        FROM collect_order_customers
        WHERE name IS NOT NULL AND TRIM(name) <> ''
        ORDER BY name COLLATE NOCASE ASC
        LIMIT :limit
        """
    )
    suspend fun getAllCustomerNames(limit: Int): List<CustomerNameRow>

    data class CustomerNameRow(val name: String)

    @Query(
        """
        SELECT DISTINCT phone FROM collect_order_customers
        WHERE phone IS NOT NULL AND phone LIKE :prefix ESCAPE '!'
        LIMIT :limit
        """
    )
    suspend fun getPhoneSuggestionsPrefix(prefix: String, limit: Int): List<String>

    // Map suggestion -> invoice numbers (for chip scoping)
    @Query("SELECT invoice_number FROM collect_order_customers WHERE name = :name")
    suspend fun getInvoiceNumbersByCustomerName(name: String): List<String>

    @Query("SELECT invoice_number FROM collect_orders WHERE web_order_number = :web")
    suspend fun getInvoiceNumbersByWebOrder(web: String): List<String>

    @Query("SELECT invoice_number FROM collect_orders WHERE order_number = :so")
    suspend fun getInvoiceNumbersByOrderNumber(so: String): List<String>

    @Query("SELECT invoice_number FROM collect_order_customers WHERE phone = :phone")
    suspend fun getInvoiceNumbersByPhone(phone: String): List<String>

    // Lightweight existence check for invoice (case-insensitive)
    @Query("SELECT EXISTS(SELECT 1 FROM collect_orders WHERE invoice_number = :invoiceNumber COLLATE NOCASE)")
    suspend fun existsInvoice(invoiceNumber: String): Boolean
}
