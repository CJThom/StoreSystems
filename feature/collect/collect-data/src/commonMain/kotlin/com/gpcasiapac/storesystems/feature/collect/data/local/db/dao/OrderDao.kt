package com.gpcasiapac.storesystems.feature.collect.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.gpcasiapac.storesystems.feature.collect.data.local.db.entity.OrderEntity
import com.gpcasiapac.storesystems.feature.collect.data.local.db.entity.OrderWithCustomerWithLineItems
import kotlinx.coroutines.flow.Flow

@Dao
interface OrderDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrReplaceOrderEntity(orderEntity: OrderEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrReplaceOrderEntity(orderEntityList: List<OrderEntity>)

    @Query("SELECT COUNT(*) FROM orders")
    suspend fun getCount(): Int

    @Transaction
    @Query("SELECT * FROM orders ORDER BY picked_at DESC")
    fun getAllWithDetailsAsFlow(): Flow<List<OrderWithCustomerWithLineItems>>

    // Lightweight suggestion queries (prefix match) returning only needed columns, limited per type
    @Query(
        """
        SELECT COALESCE(account_name, TRIM((COALESCE(first_name, '') || ' ' || COALESCE(last_name, '')))) AS display_name
        FROM orders
        WHERE COALESCE(account_name, TRIM((COALESCE(first_name, '') || ' ' || COALESCE(last_name, '')))) LIKE :prefix ESCAPE '!' COLLATE NOCASE
        LIMIT :limit
        """
    )
    suspend fun getNameSuggestionsPrefix(prefix: String, limit: Int): List<String>

    @Query(
        """
        SELECT invoice_number FROM orders
        WHERE invoice_number LIKE :prefix ESCAPE '!' COLLATE NOCASE
        LIMIT :limit
        """
    )
    suspend fun getInvoiceSuggestionsPrefix(prefix: String, limit: Int): List<String>

    @Query(
        """
        SELECT web_order_number FROM orders
        WHERE web_order_number IS NOT NULL
          AND web_order_number LIKE :prefix ESCAPE '!' COLLATE NOCASE
        LIMIT :limit
        """
    )
    suspend fun getWebOrderSuggestionsPrefix(prefix: String, limit: Int): List<String>
}