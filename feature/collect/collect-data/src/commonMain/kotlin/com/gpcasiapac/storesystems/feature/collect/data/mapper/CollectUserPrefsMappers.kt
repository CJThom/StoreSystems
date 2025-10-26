package com.gpcasiapac.storesystems.feature.collect.data.mapper

import com.gpcasiapac.storesystems.feature.collect.data.local.db.entity.CollectUserPrefsEntity
import com.gpcasiapac.storesystems.feature.collect.domain.model.CollectUserPrefs

/**
 * Mappers for CollectUserPrefs between data and domain layers.
 */
object CollectUserPrefsMappers

fun CollectUserPrefsEntity.toDomain(): CollectUserPrefs =
    CollectUserPrefs(
        selectedWorkOrderId = selectedWorkOrderId,
        isB2BFilterSelected = isB2BFilterSelected,
        isB2CFilterSelected = isB2CFilterSelected,
        sort = sort,
    )

fun CollectUserPrefs.toEntity(userId: String): CollectUserPrefsEntity =
    CollectUserPrefsEntity(
        userId = userId,
        selectedWorkOrderId = selectedWorkOrderId,
        isB2BFilterSelected = isB2BFilterSelected,
        isB2CFilterSelected = isB2CFilterSelected,
        sort = sort,
    )
