package com.gpcasiapac.storesystems.feature.collect.domain.usecase.prefs

import com.gpcasiapac.storesystems.core.identity.api.model.value.UserId
import com.gpcasiapac.storesystems.feature.collect.domain.model.CollectUserPrefs
import com.gpcasiapac.storesystems.feature.collect.domain.model.SortOption
import com.gpcasiapac.storesystems.feature.collect.domain.model.value.WorkOrderId
import com.gpcasiapac.storesystems.feature.collect.domain.repository.CollectUserPrefsRepository

/**
 * Instantiates a [CollectUserPrefs] and persists it using the repository.
 */
class CreateCollectUserPrefsUseCase(
    private val collectUserPrefsRepository: CollectUserPrefsRepository,
) {

    suspend operator fun invoke(
        userId: UserId = UserId("demo"),
        selectedWorkOrderId: WorkOrderId? = null,
        isB2BFilterSelected: Boolean = true,
        isB2CFilterSelected: Boolean = true,
        sort: SortOption = SortOption.TIME_WAITING_DESC,
    ) {

        val collectUserPrefs = CollectUserPrefs(
            userId = userId,
            selectedWorkOrderId = selectedWorkOrderId,
            isB2BFilterSelected = isB2BFilterSelected,
            isB2CFilterSelected = isB2CFilterSelected,
            sort = sort,
        )

        collectUserPrefsRepository.save(collectUserPrefs = collectUserPrefs)

    }
}
