package com.gpcasiapac.storesystems.feature.collect.presentation.selection

internal object SelectionReducer {
    fun reduce(state: SelectionUiState, intent: SelectionIntent, visibleIds: Set<String>): SelectionUiState =
        when (intent) {
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
                val rem = state.pendingRemove.toMutableSet()
                val id = intent.id
                if (intent.checked) {
                    if (id in rem) rem.remove(id) else if (id !in state.existing) add.add(id)
                } else {
                    if (id in state.existing) rem.add(id) else add.remove(id)
                }
                val selected = (state.existing - rem) union add
                state.copy(pendingAdd = add, pendingRemove = rem, selected = selected)
                    .recomputeAllSelected(visibleIds)
            }

            is SelectionIntent.ToggleAll -> {
                val add = state.pendingAdd.toMutableSet()
                val rem = state.pendingRemove.toMutableSet()
                if (intent.checked) {
                    val currentlySelected = (state.existing - rem) union add
                    val toAdd = visibleIds - currentlySelected
                    add.addAll(toAdd.filterNot { it in state.existing })
                    rem.removeAll(visibleIds)
                } else {
                    val persistedVisible = visibleIds.intersect(state.existing)
                    rem.addAll(persistedVisible)
                    add.removeAll(visibleIds)
                }
                val selected = (state.existing - rem) union add
                state.copy(
                    pendingAdd = add,
                    pendingRemove = rem,
                    selected = selected,
                    isAllSelected = intent.checked && visibleIds.isNotEmpty(),
                )
            }

            SelectionIntent.Cancel -> SelectionUiState(existing = state.existing)
        }
}

private fun SelectionUiState.recomputeAllSelected(visible: Set<String>): SelectionUiState =
    copy(isAllSelected = visible.isNotEmpty() && visible.all { it in selected })
