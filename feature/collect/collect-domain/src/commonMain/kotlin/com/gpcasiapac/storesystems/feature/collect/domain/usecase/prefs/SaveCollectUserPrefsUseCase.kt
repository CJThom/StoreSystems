package com.gpcasiapac.storesystems.feature.collect.domain.usecase.prefs

import com.gpcasiapac.storesystems.core.identity.api.model.value.UserId
import com.gpcasiapac.storesystems.feature.collect.domain.model.CollectUserPrefs
import com.gpcasiapac.storesystems.feature.collect.domain.model.SortOption
import com.gpcasiapac.storesystems.feature.collect.domain.model.value.WorkOrderId
import com.gpcasiapac.storesystems.feature.collect.domain.repository.CollectUserPrefsRepository

/**
 * Instantiates a [CollectUserPrefs] and persists it using the repository.
 */
class SaveCollectUserPrefsUseCase(
    private val repository: CollectUserPrefsRepository,
) {

    suspend operator fun invoke(
        userId: UserId = UserId("demo"),
        selectedWorkOrderId: WorkOrderId?,
        isB2BFilterSelected: Boolean,
        isB2CFilterSelected: Boolean,
        sort: SortOption,
    ) {

        val collectUserPrefs = CollectUserPrefs(
            userId = userId,
            selectedWorkOrderId = selectedWorkOrderId,
            isB2BFilterSelected = isB2BFilterSelected,
            isB2CFilterSelected = isB2CFilterSelected,
            sort = sort,
        )

        repository.save(collectUserPrefs = collectUserPrefs)
    }
}
