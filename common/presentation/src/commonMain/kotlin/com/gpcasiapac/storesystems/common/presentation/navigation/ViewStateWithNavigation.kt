package com.gpcasiapac.storesystems.common.presentation.navigation

import androidx.navigation3.runtime.NavKey
import com.gpcasiapac.storesystems.common.presentation.mvi.ViewState

interface ViewStateWithNavigation<State : ViewStateWithNavigation<State>> : ViewState {
    val stack: List<NavKey>
    fun copyWithStack(stack: List<NavKey>): State
}
