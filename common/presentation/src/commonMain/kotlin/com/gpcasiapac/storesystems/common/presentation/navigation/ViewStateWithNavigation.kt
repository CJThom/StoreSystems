package com.gpcasiapac.storesystems.common.presentation.navigation

import androidx.navigation3.runtime.NavKey
import com.gpcasiapac.storesystems.common.presentation.mvi.ViewState

interface ViewStateWithNavigation : ViewState {
    val stack: List<NavKey>
}
