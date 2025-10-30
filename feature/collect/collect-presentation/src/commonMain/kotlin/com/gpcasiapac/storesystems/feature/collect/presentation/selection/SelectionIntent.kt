package com.gpcasiapac.storesystems.feature.collect.presentation.selection

sealed interface SelectionIntent {
    data class ToggleMode(val enabled: Boolean) : SelectionIntent
    data class ToggleOne(val id: String, val checked: Boolean) : SelectionIntent
    data class ToggleAll(val checked: Boolean) : SelectionIntent
    data object Cancel : SelectionIntent
}