package com.gpcasiapac.storesystems.feature.collect.domain.usecase.workorder

/**
 * Pure, synchronous use case that computes the next selection state when the user toggles
 * Select All for the currently visible ids in multi-select mode.
 */
@Deprecated("Use SelectionHandlerDelegate")
class ToggleAllOrderSelectionUseCase {

    data class Result(
        val pendingAdd: Set<String>,
        val pendingRemove: Set<String>,
        val selected: Set<String>,
        val isAllSelected: Boolean,
    )

    operator fun invoke(
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
