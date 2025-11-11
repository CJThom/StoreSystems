package com.gpcasiapac.storesystems.feature.collect.presentation.selection

/**
 * Pure helper for computing multi-select state transitions.
 * Keeps logic identical across view models.
 */
@Deprecated("Use SelectionHandlerDelegate")
internal object SelectionCalculator {

    data class Result(
        val pendingAdd: Set<String>,
        val pendingRemove: Set<String>,
        val selected: Set<String>,
        val isAllSelected: Boolean,
    )

    /** Toggle a single id. */
    fun toggle(
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

    /** Toggle all visible ids at once. */
    fun toggleAll(
        checked: Boolean,
        persisted: Set<String>,
        pendingAdd: Set<String>,
        pendingRemove: Set<String>,
        visibleIds: Set<String>,
    ): Result {
        val add = pendingAdd.toMutableSet()
        val remove = pendingRemove.toMutableSet()

        if (checked) {
            // Add all visible that are not already selected
            val currentlySelected = (persisted - remove) union add
            val toAdd = visibleIds - currentlySelected
            add.addAll(toAdd.filterNot { it in persisted })
            // Undo any pending removals among the visible
            remove.removeAll(visibleIds)
        } else {
            // Deselect: mark persisted visible for removal, drop any pending adds among visibles
            val persistedVisible = visibleIds.intersect(persisted)
            remove.addAll(persistedVisible)
            add.removeAll(visibleIds)
        }
        val selected = (persisted - remove) union add
        return Result(add, remove, selected, checked)
    }
}
