package com.gpcasiapac.storesystems.feature.collect.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.gpcasiapac.storesystems.feature.collect.data.local.db.entity.CollectUserPrefsEntity
import com.gpcasiapac.storesystems.feature.collect.domain.model.value.WorkOrderId
import kotlinx.coroutines.flow.Flow

@Dao
interface CollectUserPrefsDao {

    @Query("SELECT * FROM collect_user_prefs WHERE userId = :userId")
    fun observe(userId: String): Flow<CollectUserPrefsEntity?>

    @Query("SELECT * FROM collect_user_prefs WHERE userId = :userId")
    suspend fun get(userId: String): CollectUserPrefsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: CollectUserPrefsEntity)

    @Query("UPDATE collect_user_prefs SET selectedWorkOrderId = :workOrderId WHERE userId = :userId")
    suspend fun setSelectedWorkOrderId(userId: String, workOrderId: WorkOrderId): Int

    @Query("UPDATE collect_user_prefs SET isB2BFilterSelected = :value WHERE userId = :userId")
    suspend fun setB2BFilterSelected(userId: String, value: Boolean): Int

    @Query("UPDATE collect_user_prefs SET isB2CFilterSelected = :value WHERE userId = :userId")
    suspend fun setB2CFilterSelected(userId: String, value: Boolean): Int

    @Query("UPDATE collect_user_prefs SET sort = :value WHERE userId = :userId")
    suspend fun setSort(userId: String, value: com.gpcasiapac.storesystems.feature.collect.domain.model.SortOption): Int
}
