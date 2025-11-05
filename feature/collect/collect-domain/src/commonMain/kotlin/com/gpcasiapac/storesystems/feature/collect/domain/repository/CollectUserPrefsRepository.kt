package com.gpcasiapac.storesystems.feature.collect.domain.repository

import com.gpcasiapac.storesystems.core.identity.api.model.value.UserId
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
    fun observe(userId: UserId): Flow<CollectUserPrefs?>

    /** Get the current preferences or default if none saved yet. */
    suspend fun get(userId: UserId): CollectUserPrefs?

    /** Replace and persist the whole preferences object. */
    suspend fun save(collectUserPrefs: CollectUserPrefs)

    /**
     * Update only the selected work order id for the given user.
     * @return number of rows updated (0 means the row does not exist)
     */
    suspend fun setSelectedWorkOrderId(userId: UserId, selectedWorkOrderId: WorkOrderId?): Int

}
