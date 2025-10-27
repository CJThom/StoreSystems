package com.gpcasiapac.storesystems.feature.collect.domain.repository

import com.gpcasiapac.storesystems.feature.collect.domain.model.CollectUserPrefs
import com.gpcasiapac.storesystems.feature.collect.domain.model.SortOption
import com.gpcasiapac.storesystems.feature.collect.domain.model.value.WorkOrderId
import kotlinx.coroutines.flow.Flow

/**
 * Repository for persisting Collect feature user preferences.
 * Backed by Room in commonMain; no network involved.
 *
 * All operations are scoped to a specific user via [userId] (1:1 with Identity user id).
 */
interface CollectUserPrefsRepository {

    /** Observe the preferences; emits a default value when not set yet. */
    fun observe(userId: String): Flow<CollectUserPrefs>

    /** Get the current preferences or default if none saved yet. */
    suspend fun get(userId: String): CollectUserPrefs

    /** Replace and persist the whole preferences object. */
    suspend fun save(userId: String, prefs: CollectUserPrefs)

    // Convenience field setters
    suspend fun setSelectedWorkOrderId(userId: String, workOrderId: WorkOrderId)
    suspend fun setB2BFilterSelected(userId: String, selected: Boolean)
    suspend fun setB2CFilterSelected(userId: String, selected: Boolean)
    suspend fun setSort(userId: String, sort: SortOption)
}
