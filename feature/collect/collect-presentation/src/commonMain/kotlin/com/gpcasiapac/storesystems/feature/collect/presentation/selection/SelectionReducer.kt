package com.gpcasiapac.storesystems.feature.collect.presentation.selection

internal object SelectionReducer {
    fun <T> reduce(
        state: SelectionUiState<T>,
        intent: SelectionIntent<T>,
        visibleIds: Set<T>
    ): SelectionUiState<T> {
        return when (intent) {
            is SelectionIntent.ToggleMode -> if (intent.enabled) {
                state.copy(
                    isEnabled = true,
                    pendingAdd = emptySet(),
                    pendingRemove = emptySet(),
                    selected = state.existing,
                ).recomputeAllSelected(visibleIds)
            } else {
                SelectionUiState(existing = state.existing)
            }

            is SelectionIntent.ToggleOne -> {
                val add = state.pendingAdd.toMutableSet()
                val remove = state.pendingRemove.toMutableSet()
                val id = intent.id
                if (intent.checked) {
                    if (id in remove) {
                        remove.remove(id)
                    } else {
                        if (id !in state.existing) add.add(id)
                    }
                } else {
                    if (id in state.existing) {
                        remove.add(id)
                    } else {
                        add.remove(id)
                    }
                }
                val selected = (state.existing - remove) union add
                state.copy(
                    pendingAdd = add,
                    pendingRemove = remove,
                    selected = selected
                ).recomputeAllSelected(visibleIds)
            }

            is SelectionIntent.ToggleAll -> {
                val add = state.pendingAdd.toMutableSet()
                val remove = state.pendingRemove.toMutableSet()
                if (intent.checked) {
                    val currentlySelected = (state.existing - remove) union add
                    val toAdd = visibleIds - currentlySelected
                    add.addAll(toAdd.filterNot { it in state.existing })
                    remove.removeAll(visibleIds)
                } else {
                    val persistedVisible = visibleIds.intersect(state.existing)
                    remove.addAll(persistedVisible)
                    add.removeAll(visibleIds)
                }
                val selected = (state.existing - remove) union add
                state.copy(
                    pendingAdd = add,
                    pendingRemove = remove,
                    selected = selected,
                    isAllSelected = intent.checked && visibleIds.isNotEmpty(),
                )
            }

            is SelectionIntent.Cancel -> SelectionUiState(existing = state.existing)
        }
    }
}

private fun <T> SelectionUiState<T>.recomputeAllSelected(visible: Set<T>): SelectionUiState<T> {
    return copy(isAllSelected = visible.isNotEmpty() && visible.all { it in selected })
}

