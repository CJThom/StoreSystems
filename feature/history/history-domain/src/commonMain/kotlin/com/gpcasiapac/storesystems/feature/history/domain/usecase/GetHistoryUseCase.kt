package com.gpcasiapac.storesystems.feature.history.domain.usecase

import com.gpcasiapac.storesystems.feature.history.api.HistoryType
import com.gpcasiapac.storesystems.feature.history.domain.model.CollectHistoryItem
import com.gpcasiapac.storesystems.feature.history.domain.model.HistoryFilter
import com.gpcasiapac.storesystems.feature.history.domain.model.HistoryItem
import com.gpcasiapac.storesystems.feature.history.domain.model.HistoryStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Use case to observe history items (sealed types).
 * Supports filtering by status, type, and search query.
 */
class GetHistoryUseCase(
    private val observeUnifiedHistoryUseCase: ObserveUnifiedHistoryUseCase
) {
    /**
     * Observe all history items.
     *
     * @param statusFilter Filter by status (null = all statuses)
     * @param typeFilter Filter by type (null = all types)
     * @param searchQuery Search in invoice, order, customer numbers and names (null = no search)
     */
    operator fun invoke(
        statusFilter: HistoryStatus? = null,
        typeFilter: HistoryType? = null,
        searchQuery: String? = null
    ): Flow<List<HistoryItem>> {
        return observeUnifiedHistoryUseCase()
            .map { items ->
                items
                    .applyStatusFilter(statusFilter)
                    .applyTypeFilter(typeFilter)
                    .applySearchFilter(searchQuery)
            }
    }

    /**
     * Convenience method for HistoryFilter enum.
     */
    fun invoke(
        filter: HistoryFilter = HistoryFilter.ALL,
        typeFilter: HistoryType? = null,
        searchQuery: String? = null
    ): Flow<List<HistoryItem>> {
        val statusFilter = when (filter) {
            HistoryFilter.ALL -> null
            HistoryFilter.PENDING -> HistoryStatus.PENDING
            HistoryFilter.FAILED -> HistoryStatus.FAILED
            HistoryFilter.COMPLETED -> HistoryStatus.COMPLETED
        }

        return invoke(
            statusFilter = statusFilter,
            typeFilter = typeFilter,
            searchQuery = searchQuery
        )
    }

    private fun List<HistoryItem>.applyStatusFilter(
        statusFilter: HistoryStatus?
    ): List<HistoryItem> = if (statusFilter != null) filter { it.status == statusFilter } else this

    private fun List<HistoryItem>.applyTypeFilter(
        typeFilter: HistoryType?
    ): List<HistoryItem> = if (typeFilter != null) {
        when (typeFilter) {
            HistoryType.ORDER_SUBMISSION -> filterIsInstance<CollectHistoryItem>()
            else -> this
        }
    } else this

    private fun List<HistoryItem>.applySearchFilter(
        searchQuery: String?
    ): List<HistoryItem> {
        if (searchQuery.isNullOrBlank()) return this

        val query = searchQuery.trim().lowercase()

        return filter { item ->
            when (item) {
                is CollectHistoryItem ->
                    item.entityId.lowercase().contains(query) ||
                        item.metadata.any { md ->
                            md.invoiceNumber.lowercase().contains(query) ||
                                md.salesOrderNumber.lowercase().contains(query) ||
                                md.webOrderNumber?.lowercase()?.contains(query) == true ||
                                md.customerNumber.lowercase().contains(query) ||
                                md.accountName?.lowercase()?.contains(query) == true ||
                                md.firstName?.lowercase()?.contains(query) == true ||
                                md.lastName?.lowercase()?.contains(query) == true ||
                                md.getCustomerDisplayName().lowercase().contains(query)
                        }
            }
        }
    }
}
