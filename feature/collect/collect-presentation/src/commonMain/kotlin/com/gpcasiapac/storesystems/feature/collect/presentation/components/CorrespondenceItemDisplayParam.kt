package com.gpcasiapac.storesystems.feature.collect.presentation.components

import androidx.compose.runtime.Immutable

@Immutable
data class CorrespondenceItemDisplayParam(
    val id: String,
    val type: String,
    val detail: String,
    val isEnabled: Boolean
)
