package com.gpcasiapac.storesystems.common.presentation.navigation

import com.gpcasiapac.storesystems.common.presentation.mvi.ViewState

/**
 * Simple navigation state holding the stack of keys.
 */
data class NavState<K : FeatureKey>(val stack: List<K>) : ViewState