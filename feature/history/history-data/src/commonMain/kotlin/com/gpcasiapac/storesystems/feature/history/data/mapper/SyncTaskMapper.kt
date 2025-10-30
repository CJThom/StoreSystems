package com.gpcasiapac.storesystems.feature.history.data.mapper

// This mapper previously converted SyncTask -> HistoryItem (data class).
// After migrating HistoryItem to a sealed interface with typed subtypes, this
// file is intentionally left without conversions. If needed in the future,
// add explicit mappings to a suitable sealed subtype.
