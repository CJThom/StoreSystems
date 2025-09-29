package com.gpcasiapac.storesystems.feature.collect.domain.repository

import kotlinx.coroutines.flow.Flow

/**
 * Stores the user's currently selected order IDs (single or multi select).
 *
 * Minimal API by design:
 * - Scope by an optional [userRefId] so multiple ViewModels (and even apps) can share the same
 *   selection surface while still keeping selections isolated per user/session if desired.
 * - Backing storage can be in-memory for now; can be swapped to persisted prefs later without
 *   touching call sites.
 */
interface OrderSelectionRepository {

    /** Observe the set of selected order IDs for the given user scope. */
    fun getSelectedIdListFlow(userRefId: String? = null): Flow<Set<String>>

    /** Replace the entire set of selected IDs. */
    suspend fun setSelectedIdList(orderIdList: List<String>, userRefId: String? = null)

    /** Add a single order ID to the selection. */
    suspend fun addSelectedId(orderId: String, userRefId: String? = null)

    /** Remove a single order ID from the selection. */
    suspend fun removeSelectedId(orderId: String, userRefId: String? = null)

    /** Clear the selection. */
    suspend fun clear(userRefId: String? = null)
}
