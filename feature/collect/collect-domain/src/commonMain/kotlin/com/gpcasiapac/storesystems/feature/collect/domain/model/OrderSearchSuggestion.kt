package com.gpcasiapac.storesystems.feature.collect.domain.model


data class OrderSearchSuggestion(
    val text: String,
    val type: OrderSearchSuggestionType,
)