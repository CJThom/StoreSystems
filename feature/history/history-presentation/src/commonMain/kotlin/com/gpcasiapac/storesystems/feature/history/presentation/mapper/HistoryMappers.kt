package com.gpcasiapac.storesystems.feature.history.presentation.mapper

import com.gpcasiapac.storesystems.feature.history.api.HistoryType
import com.gpcasiapac.storesystems.feature.history.domain.model.CollectHistoryItem
import com.gpcasiapac.storesystems.feature.history.domain.model.HistoryItem
import com.gpcasiapac.storesystems.feature.history.domain.model.HistoryStatus
import com.gpcasiapac.storesystems.feature.history.presentation.model.HistoryListItemState

/**
 * Map a CollectHistoryItem domain model to a presentation UI model
 * used by the history list cell.
 */
fun CollectHistoryItem.toHistoryListItemState(): HistoryListItemState {
    val first = metadata.firstOrNull()
    val invoiceNumbers = metadata.map { m -> m.webOrderNumber ?: m.invoiceNumber }
    val customerName = first?.getCustomerDisplayName().orEmpty()
    val submittedAt = first?.orderCreatedAt
    val status = this.status
    val canRetry = status == HistoryStatus.FAILED || status == HistoryStatus.REQUIRES_ACTION

    return HistoryListItemState(
        id = id,
        type = HistoryType.ORDER_SUBMISSION,
        customerName = customerName,
        invoiceNumbers = invoiceNumbers,
        status = status,
        submittedAt = submittedAt,
        canRetry = canRetry,
    )
}

/**
 * Generic mapper for the sealed HistoryItem root, returns null for unsupported subtypes.
 */
fun HistoryItem.toHistoryListItemStateOrNull(): HistoryListItemState? = when (this) {
    is CollectHistoryItem -> this.toHistoryListItemState()
    // Add other subtypes here when introduced
}

fun List<HistoryItem>.mapToUi(): List<HistoryListItemState> = this.mapNotNull { it.toHistoryListItemStateOrNull() }