package com.gpcasiapac.storesystems.feature.collect.presentation.selection

import androidx.compose.runtime.Immutable

@Immutable
data class SelectionUiState<T>(
    val isEnabled: Boolean = false,
    val existing: Set<T> = emptySet(),
    val pendingAdd: Set<T> = emptySet(),
    val pendingRemove: Set<T> = emptySet(),
    val selected: Set<T> = emptySet(),
    val isAllSelected: Boolean = false,
)
