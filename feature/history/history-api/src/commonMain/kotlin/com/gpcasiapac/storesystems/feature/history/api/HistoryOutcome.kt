package com.gpcasiapac.storesystems.feature.history.api

sealed interface HistoryOutcome {
    data object Back : HistoryOutcome
}
