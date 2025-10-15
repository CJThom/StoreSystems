package com.gpcasiapac.storesystems.feature.collect.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.gpcasiapac.storesystems.feature.collect.data.local.db.entity.CollectOrderCustomerEntity
import com.gpcasiapac.storesystems.feature.collect.data.local.db.entity.CollectOrderEntity
import com.gpcasiapac.storesystems.feature.collect.data.local.db.entity.CollectOrderLineItemEntity
import com.gpcasiapac.storesystems.feature.collect.data.local.db.relation.CollectOrderWithCustomerRelation
import com.gpcasiapac.storesystems.feature.collect.data.local.db.relation.CollectOrderWithCustomerWithLineItemsRelation
import com.gpcasiapac.storesystems.feature.collect.domain.model.CustomerType
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

    @Transaction
    @Query("SELECT * FROM collect_orders ORDER BY picked_at DESC")
    fun getCollectOrderWithCustomerWithLineItemsRelationListFlow(): Flow<List<CollectOrderWithCustomerWithLineItemsRelation>>

    @Transaction
    @Query("SELECT * FROM collect_orders ORDER BY picked_at DESC")
    fun getCollectOrderWithCustomerRelationListFlow(): Flow<List<CollectOrderWithCustomerRelation>>

    @Transaction
    @Query("SELECT * FROM collect_orders WHERE invoice_number IN (:invoiceNumbers) ORDER BY picked_at DESC")
    fun getCollectOrderWithCustomerRelationListFlow(invoiceNumbers: Set<String>): Flow<List<CollectOrderWithCustomerRelation>>

    // Main list: filtered by customer types and sorted by option (placeholder for name sort)
    @Transaction
    @Query(
        """
        SELECT * FROM collect_orders
        WHERE invoice_number IN (
           SELECT invoice_number FROM collect_order_customers
           WHERE customer_type IN (:customerTypes)
        )
        ORDER BY
          CASE WHEN :sort = 'TIME_WAITING_ASC' THEN picked_at END ASC,
          CASE WHEN :sort = 'TIME_WAITING_DESC' THEN picked_at END DESC,
          CASE WHEN :sort = 'NAME_ASC' THEN invoice_number END ASC,
          CASE WHEN :sort = 'NAME_DESC' THEN invoice_number END DESC
        """
    )
    fun observeOrdersForMainList(
        customerTypes: Set<CustomerType>,
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
          (sales_order_number IS NOT NULL AND sales_order_number LIKE :q ESCAPE '!'
            COLLATE NOCASE) OR
          invoice_number IN (
             SELECT invoice_number FROM collect_order_customers WHERE (
               (account_name IS NOT NULL AND account_name LIKE :q ESCAPE '!' COLLATE NOCASE)
               OR ((first_name || ' ' || last_name) LIKE :q ESCAPE '!' COLLATE NOCASE)
               OR (phone IS NOT NULL AND phone LIKE :q ESCAPE '!' COLLATE NOCASE)
             )
          )
        )
        ORDER BY picked_at DESC
        """
    )
    fun observeOrdersForSearch(q: String): Flow<List<CollectOrderWithCustomerRelation>>


    @Transaction
    @Query("SELECT * FROM collect_orders where invoice_number = :invoiceNumber")
    fun getCollectOrderWithCustomerWithLineItemsRelationFlow(invoiceNumber:String): Flow<CollectOrderWithCustomerWithLineItemsRelation>

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
}
