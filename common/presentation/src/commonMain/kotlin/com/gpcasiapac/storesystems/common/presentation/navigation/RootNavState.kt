package com.gpcasiapac.storesystems.common.presentation.navigation

import androidx.navigation3.runtime.NavKey

/**
 * Basic root navigation state containing only the navigation stack.
 * Features can extend this or create their own state implementing ViewStateWithNavigation.
 */
data class RootNavState(override val stack: List<NavKey>) : ViewStateWithNavigation