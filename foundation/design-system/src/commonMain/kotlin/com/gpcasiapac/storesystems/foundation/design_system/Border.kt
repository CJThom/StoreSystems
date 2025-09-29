package com.gpcasiapac.storesystems.foundation.design_system

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.unit.dp
import com.gpcasiapac.storesystems.common.presentation.theme.BorderStrokes
import com.gpcasiapac.storesystems.common.presentation.theme.BorderWidths
import com.gpcasiapac.storesystems.common.presentation.theme.ComponentBorders
import com.gpcasiapac.storesystems.common.presentation.theme.LocalComponentBorders

val MaterialTheme.componentBorders: ComponentBorders
    @Composable
    @ReadOnlyComposable
    get() = LocalComponentBorders.current

@Composable
internal fun ProvideComponentBorders(
    borders: ComponentBorders? = null,
    content: @Composable () -> Unit
) {
    val widths = BorderWidths(
        small = 1.dp,
        medium = 2.dp,
        large = 3.dp,
    )
    val defaultColor = MaterialTheme.colorScheme.outlineVariant
    val common = BorderStrokes(widths, defaultColor)
    val provided = borders ?: ComponentBorders(
        card = common,
        divider = common,
    )

    CompositionLocalProvider(
        LocalComponentBorders provides provided,
        content = content
    )
}
