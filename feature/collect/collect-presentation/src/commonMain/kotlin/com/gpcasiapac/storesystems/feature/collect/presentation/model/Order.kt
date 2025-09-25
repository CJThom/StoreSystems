package com.gpcasiapac.storesystems.feature.collect.presentation.model

import androidx.compose.runtime.Immutable

@Immutable
data class Order(
    val id: String,
    val title: String,
)