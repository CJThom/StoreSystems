package com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderlist.model

import androidx.compose.runtime.Immutable
import com.gpcasiapac.storesystems.feature.collect.domain.model.OrderSearchSuggestionType

@Immutable
data class FilterChip(
    val label: String,
    val type: OrderSearchSuggestionType, // what the chip represents
    val value: String = label,
)