package com.gpcasiapac.storesystems.common.presentation.navigation

import androidx.navigation3.runtime.NavKey
import com.gpcasiapac.storesystems.common.presentation.mvi.ViewState

/**
 * Simple navigation state holding the stack of keys.
 */
data class NavState(val stack: List<NavKey>) : ViewState