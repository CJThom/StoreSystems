package com.gpcasiapac.storesystems.feature.collect.domain.model

// Search query for debounced search text plus selected chips for scoping
data class SearchQuery(
    val text: String,
    val selected: List<SearchSuggestion> = emptyList(),
)