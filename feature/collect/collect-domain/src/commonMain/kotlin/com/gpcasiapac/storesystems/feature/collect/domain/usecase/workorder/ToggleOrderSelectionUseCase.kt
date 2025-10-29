package com.gpcasiapac.storesystems.feature.collect.domain.usecase.workorder

/**
 * Pure, synchronous use case that computes the next selection state when a single item
 * is toggled in multi-select mode.
 *
 * This lives in domain to avoid duplicating selection math across multiple ViewModels.
 */
class ToggleOrderSelectionUseCase {

    data class Result(
        val pendingAdd: Set<String>,
        val pendingRemove: Set<String>,
        val selected: Set<String>,
        val isAllSelected: Boolean,
    )

    operator fun invoke(
        orderId: String,
        checked: Boolean,
        persisted: Set<String>,
        pendingAdd: Set<String>,
        pendingRemove: Set<String>,
        visibleIds: Set<String>,
    ): Result {
        val add = pendingAdd.toMutableSet()
        val remove = pendingRemove.toMutableSet()

        if (checked) {
            if (orderId in remove) {
                remove.remove(orderId)
            } else if (orderId !in persisted) {
                add.add(orderId)
            }
        } else {
            if (orderId in persisted) {
                remove.add(orderId)
            } else {
                add.remove(orderId)
            }
        }
        val selected = (persisted - remove) union add
        val allSelected = visibleIds.isNotEmpty() && visibleIds.all { it in selected }
        return Result(add, remove, selected, allSelected)
    }
}
