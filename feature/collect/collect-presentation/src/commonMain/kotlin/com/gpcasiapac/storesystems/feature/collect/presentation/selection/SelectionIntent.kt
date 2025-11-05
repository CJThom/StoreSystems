package com.gpcasiapac.storesystems.feature.collect.presentation.selection

sealed interface SelectionIntent<out T> {
    data class ToggleMode(val enabled: Boolean) : SelectionIntent<Nothing>
    data class ToggleOne<T>(val id: T, val checked: Boolean) : SelectionIntent<T>
    data class ToggleAll(val checked: Boolean) : SelectionIntent<Nothing>
    data object Cancel : SelectionIntent<Nothing>
}