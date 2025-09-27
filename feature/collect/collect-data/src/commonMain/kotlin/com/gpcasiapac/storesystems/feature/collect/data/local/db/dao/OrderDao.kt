package com.gpcasiapac.storesystems.feature.collect.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.gpcasiapac.storesystems.feature.collect.data.local.db.entity.OrderEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface OrderDao {

    @Insert
    suspend fun insert(order: OrderEntity)

    @Insert
    suspend fun insertAll(orders: List<OrderEntity>)

    @Query("SELECT COUNT(*) FROM orders")
    suspend fun count(): Int

    @Query("SELECT * FROM orders ORDER BY picked_at DESC")
    fun getAllAsFlow(): Flow<List<OrderEntity>>
}