package com.gpcasiapac.storesystems.feature.collect.domain.usecase.workorder

import com.gpcasiapac.storesystems.feature.collect.domain.model.CollectWorkOrderItem
import com.gpcasiapac.storesystems.feature.collect.domain.model.value.WorkOrderId
import com.gpcasiapac.storesystems.feature.collect.domain.repository.OrderLocalRepository

/**
 * Applies a delta of removals then additions to a Work Order atomically in a single DB transaction.
 * - Normalizes inputs (trim, drop blanks, case-insensitive de-duplication)
 * - Removes first to avoid transient uniqueness conflicts and to compute correct positions
 * - Appends new items after current MAX(position)
 * - Interprets Room IGNORE semantics: rowId == -1L indicates duplicate
 */
class ApplyOrderSelectionDeltaUseCase(
    private val repo: OrderLocalRepository,
) {
    sealed interface Result {
        data object Noop : Result
        data class Summary(
            val added: List<String>,
            val duplicates: List<String>,
            val removed: Int,
        ) : Result
    }

    suspend operator fun invoke(
        workOrderId: WorkOrderId,
        add: Collection<String>,
        remove: Collection<String>,
    ): Result {
        val addDistinct = add.map { it.trim() }
            .filter { it.isNotEmpty() }
            .distinctBy { it.lowercase() }
        val removeDistinct = remove.map { it.trim() }
            .filter { it.isNotEmpty() }
            .distinctBy { it.lowercase() }

        // If in both add and remove, removal wins (do not re-add)
        val addEffective = addDistinct.filterNot { it in removeDistinct }

        if (addEffective.isEmpty() && removeDistinct.isEmpty()) return Result.Noop

        return repo.write {
            // 1) Remove first
            var removedCount = 0
            if (removeDistinct.isNotEmpty()) {
                val before = repo.getWorkOrderItemCount(workOrderId)
                if (before > 0) {
                    repo.deleteWorkOrderItems(
                        workOrderId = workOrderId,
                        orderIds = removeDistinct
                    )
                    val after = repo.getWorkOrderItemCount(workOrderId)
                    removedCount = (before - after).coerceAtLeast(0)
                }
            }

            // 2) Add
            val added = mutableListOf<String>()
            val duplicates = mutableListOf<String>()
            if (addEffective.isNotEmpty()) {
                val start = repo.getMaxWorkOrderItemPosition(workOrderId) + 1
                val items = addEffective.mapIndexed { idx, inv ->
                    CollectWorkOrderItem(
                        workOrderId = workOrderId,
                        invoiceNumber = inv,
                        position = start + idx,
                    )
                }
                val rowIds = repo.insertWorkOrderItemList(items)
                rowIds.forEachIndexed { i, rowId ->
                    val inv = addEffective[i]
                    if (rowId != -1L) added += inv else duplicates += inv
                }
            } else {
                // If no adds and nothing remains, delete the WO
                val remaining = repo.getWorkOrderItemCount(workOrderId)
                if (remaining == 0) {
                    repo.deleteWorkOrder(workOrderId)
                }
            }

            Result.Summary(
                added = added,
                duplicates = duplicates,
                removed = removedCount,
            )
        }
    }
}