package com.gpcasiapac.storesystems.feature.history.api

sealed interface HistoryOutcome {
    data object Back : HistoryOutcome
    data class OpenDetails(val title: String, val groupKey: String) : HistoryOutcome
}
