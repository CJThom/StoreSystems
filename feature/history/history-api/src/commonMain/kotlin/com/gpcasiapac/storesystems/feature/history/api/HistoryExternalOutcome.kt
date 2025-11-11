package com.gpcasiapac.storesystems.feature.history.api

sealed interface HistoryExternalOutcome {
    // Emitted when the History host requests to be closed (e.g., back on root)
    data object Exit : HistoryExternalOutcome
}
