package com.gpcasiapac.storesystems.feature.collect.data.local.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.gpcasiapac.storesystems.core.identity.api.model.value.UserId
import com.gpcasiapac.storesystems.feature.collect.domain.model.SortOption
import com.gpcasiapac.storesystems.feature.collect.domain.model.value.WorkOrderId

@Entity(tableName = "collect_user_prefs")
data class CollectUserPrefsEntity(

    @PrimaryKey
    @ColumnInfo(name = "user_id")
    val userId: UserId,

    @ColumnInfo(name = "selected_work_order_id")
    val selectedWorkOrderId: WorkOrderId?,

    @ColumnInfo(name = "is_b2b_filter_selected")
    val isB2BFilterSelected: Boolean,

    @ColumnInfo(name = "is_b2c_filter_selected")
    val isB2CFilterSelected: Boolean,

    @ColumnInfo(name = "sort")
    val sort: SortOption

)