package com.gpcasiapac.storesystems.feature.collect.data.local.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.gpcasiapac.storesystems.feature.collect.domain.model.SortOption
import com.gpcasiapac.storesystems.feature.collect.domain.model.value.WorkOrderId

@Entity(tableName = "collect_user_prefs")
data class CollectUserPrefsEntity(
    @PrimaryKey val userId: String,
    val selectedWorkOrderId: WorkOrderId?,
    val isB2BFilterSelected: Boolean,
    val isB2CFilterSelected: Boolean,
    val sort: SortOption,
)
