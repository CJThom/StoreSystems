package com.gpcasiapac.storesystems.feature.collect.domain.model

import java.util.EnumSet

/**
 * Query configuration for fetching search suggestions.
 */
data class SuggestionQuery(
    val text: String,
    val includeKinds: Set<SuggestionKind> = EnumSet.allOf(SuggestionKind::class.java),
    val perKindLimit: Int = 8,
    val maxTotal: Int = 24,
    // New: currently selected chips to constrain suggestions
    val selected: List<SearchSuggestion> = emptyList(),
)