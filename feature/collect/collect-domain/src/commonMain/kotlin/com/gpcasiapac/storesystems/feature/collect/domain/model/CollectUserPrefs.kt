package com.gpcasiapac.storesystems.feature.collect.domain.model

/**
 * User preferences related to the Collect feature.
 * Stored locally via Room in commonMain.
 */
data class CollectUserPrefs(
    val selectedWorkOrderId: String,
    val isB2BFilterSelected: Boolean,
    val isB2CFilterSelected: Boolean,
    val sort: SortOption,
) {
    companion object {
        val DEFAULT = CollectUserPrefs(
            selectedWorkOrderId = "",
            isB2BFilterSelected = true,
            isB2CFilterSelected = true,
            sort = SortOption.TIME_WAITING_DESC,
        )
    }
}
