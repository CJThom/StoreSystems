package com.gpcasiapac.storesystems.feature.history.api

sealed interface HistoryOutcome {
    data object Back : HistoryOutcome
    data class OpenDetails(val type: HistoryType, val id: String) : HistoryOutcome
}
