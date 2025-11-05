package com.gpcasiapac.storesystems.feature.collect.domain.model

import com.gpcasiapac.storesystems.core.identity.api.model.value.UserId
import com.gpcasiapac.storesystems.feature.collect.domain.model.value.WorkOrderId

/**
 * User preferences related to the Collect feature.
 * Stored locally via Room in commonMain.
 */
data class CollectUserPrefs(
    val userId: UserId,
    val selectedWorkOrderId: WorkOrderId?,
    val isB2BFilterSelected: Boolean,
    val isB2CFilterSelected: Boolean,
    val sort: SortOption,
)
