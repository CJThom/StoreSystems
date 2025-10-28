package com.gpcasiapac.storesystems.feature.collect.data.mapper

import com.gpcasiapac.storesystems.feature.collect.data.local.db.entity.CollectUserPrefsEntity
import com.gpcasiapac.storesystems.feature.collect.domain.model.CollectUserPrefs

fun CollectUserPrefsEntity.toDomain(): CollectUserPrefs {
    return CollectUserPrefs(
        userId = this.userId,
        selectedWorkOrderId = this.selectedWorkOrderId,
        isB2BFilterSelected = this.isB2BFilterSelected,
        isB2CFilterSelected = this.isB2CFilterSelected,
        sort = this.sort,
    )
}