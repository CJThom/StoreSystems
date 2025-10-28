package com.gpcasiapac.storesystems.feature.history.domain.usecase

import com.gpcasiapac.storesystems.feature.history.domain.model.HistoryFilter
import com.gpcasiapac.storesystems.feature.history.domain.model.HistoryItemWithMetadata
import com.gpcasiapac.storesystems.feature.history.domain.model.HistoryMetadata
import com.gpcasiapac.storesystems.feature.history.domain.model.HistoryStatus
import com.gpcasiapac.storesystems.feature.history.domain.model.HistoryType
import com.gpcasiapac.storesystems.feature.history.domain.repository.HistoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Use case to observe history items with metadata.
 * Supports filtering by status, type, and search query.
 */
class GetHistoryUseCase(
    private val historyRepository: HistoryRepository
) {
    /**
     * Observe all history items with metadata.
     * 
     * @param statusFilter Filter by status (null = all statuses)
     * @param typeFilter Filter by type (null = all types)
     * @param searchQuery Search in invoice, order, customer numbers and names (null = no search)
     */
    operator fun invoke(
        statusFilter: HistoryStatus? = null,
        typeFilter: HistoryType? = null,
        searchQuery: String? = null
    ): Flow<List<HistoryItemWithMetadata>> {
        return historyRepository.observeHistoryWithMetadata()
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
    ): Flow<List<HistoryItemWithMetadata>> {
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
    
    private fun List<HistoryItemWithMetadata>.applyStatusFilter(
        statusFilter: HistoryStatus?
    ): List<HistoryItemWithMetadata> {
        return if (statusFilter != null) {
            filter { it.status == statusFilter }
        } else {
            this
        }
    }
    
    private fun List<HistoryItemWithMetadata>.applyTypeFilter(
        typeFilter: HistoryType?
    ): List<HistoryItemWithMetadata> {
        return if (typeFilter != null) {
            filter { it.type == typeFilter }
        } else {
            this
        }
    }
    
    private fun List<HistoryItemWithMetadata>.applySearchFilter(
        searchQuery: String?
    ): List<HistoryItemWithMetadata> {
        if (searchQuery.isNullOrBlank()) return this
        
        val query = searchQuery.trim().lowercase()
        
        return filter { item ->
            // Search in entity ID
            item.entityId.lowercase().contains(query) ||
            // Search in metadata if available
            when (val metadata = item.metadata) {
                is HistoryMetadata.CollectMetadata -> {
                    metadata.invoiceNumber.lowercase().contains(query) ||
                    metadata.salesOrderNumber.lowercase().contains(query) ||
                    metadata.webOrderNumber?.lowercase()?.contains(query) == true ||
                    metadata.customerNumber.lowercase().contains(query) ||
                    metadata.accountName?.lowercase()?.contains(query) == true ||
                    metadata.firstName?.lowercase()?.contains(query) == true ||
                    metadata.lastName?.lowercase()?.contains(query) == true ||
                    metadata.getCustomerDisplayName().lowercase().contains(query)
                }
                is HistoryMetadata.NoMetadata -> false
            }
        }
    }
}
