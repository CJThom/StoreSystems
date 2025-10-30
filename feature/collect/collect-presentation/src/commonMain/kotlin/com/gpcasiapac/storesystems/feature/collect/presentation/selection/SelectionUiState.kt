package com.gpcasiapac.storesystems.feature.collect.presentation.selection

import androidx.compose.runtime.Immutable

@Immutable
data class SelectionUiState(
    val isEnabled: Boolean = false,
    val existing: Set<String> = emptySet(),
    val pendingAdd: Set<String> = emptySet(),
    val pendingRemove: Set<String> = emptySet(),
    val selected: Set<String> = emptySet(),
    val isAllSelected: Boolean = false,
)
