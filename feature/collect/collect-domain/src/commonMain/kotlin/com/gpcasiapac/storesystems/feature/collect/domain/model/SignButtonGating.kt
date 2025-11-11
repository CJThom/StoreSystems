package com.gpcasiapac.storesystems.feature.collect.domain.model

/**
 * Represents the enable/disable state for the Sign action and optional reasons when disabled.
 * Reasons are represented as stable string keys for presentation-layer mapping/localization.
 */
 data class SignButtonGating(
     val isEnabled: Boolean,
     val reasons: List<Reason> = emptyList(),
 ) {
     @JvmInline
     value class Reason(val value: String)
 }
