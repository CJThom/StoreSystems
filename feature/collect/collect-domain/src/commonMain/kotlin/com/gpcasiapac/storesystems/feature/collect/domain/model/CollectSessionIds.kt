package com.gpcasiapac.storesystems.feature.collect.domain.model

import com.gpcasiapac.storesystems.common.presentation.session.SessionIds
import com.gpcasiapac.storesystems.core.identity.api.model.User
import com.gpcasiapac.storesystems.core.identity.api.model.value.UserId
import com.gpcasiapac.storesystems.feature.collect.domain.model.value.WorkOrderId

/**
 * Session ids relevant to the Collect feature.
 */
data class CollectSessionIds(
    val userId: UserId? = null,
    val workOrderId: WorkOrderId? = null,
) : SessionIds