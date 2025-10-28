package com.gpcasiapac.storesystems.feature.collect.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.gpcasiapac.storesystems.core.identity.api.model.value.UserId
import com.gpcasiapac.storesystems.feature.collect.data.local.db.entity.CollectUserPrefsEntity
import com.gpcasiapac.storesystems.feature.collect.domain.model.SortOption
import com.gpcasiapac.storesystems.feature.collect.domain.model.value.WorkOrderId
import kotlinx.coroutines.flow.Flow

@Dao
interface CollectUserPrefsDao {

    @Query("SELECT * FROM collect_user_prefs WHERE user_id = :userId")
    fun observe(userId: UserId): Flow<CollectUserPrefsEntity?>

    @Query("SELECT * FROM collect_user_prefs WHERE user_id = :userId")
    suspend fun get(userId: UserId): CollectUserPrefsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: CollectUserPrefsEntity)

    @Query("UPDATE collect_user_prefs SET selected_work_order_id = :workOrderId WHERE user_id = :userId")
    suspend fun setSelectedWorkOrderId(userId: UserId, workOrderId: WorkOrderId): Int

    @Query("UPDATE collect_user_prefs SET is_b2b_filter_selected = :value WHERE user_id = :userId")
    suspend fun setB2BFilterSelected(userId: UserId, value: Boolean): Int

    @Query("UPDATE collect_user_prefs SET is_b2c_filter_selected = :value WHERE user_id = :userId")
    suspend fun setB2CFilterSelected(userId: UserId, value: Boolean): Int

    @Query("UPDATE collect_user_prefs SET sort = :value WHERE user_id = :userId")
    suspend fun setSort(userId: UserId, value: SortOption): Int

}
