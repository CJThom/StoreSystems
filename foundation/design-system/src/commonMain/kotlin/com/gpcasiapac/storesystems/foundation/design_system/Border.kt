package com.gpcasiapac.storesystems.foundation.design_system

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.unit.dp
import com.gpcasiapac.storesystems.common.presentation.theme.BorderStrokes
import com.gpcasiapac.storesystems.common.presentation.theme.BorderWidths
import com.gpcasiapac.storesystems.common.presentation.theme.ComponentBorderStrokes
import com.gpcasiapac.storesystems.common.presentation.theme.LocalComponentBorderStrokes

val MaterialTheme.borderStrokes: ComponentBorderStrokes
    @Composable
    @ReadOnlyComposable
    get() = LocalComponentBorderStrokes.current

@Composable
internal fun ProvideComponentBorderStrokes(
    borders: ComponentBorderStrokes? = null,
    content: @Composable () -> Unit
) {

    val widths = BorderWidths(
        small = 1.dp,
        medium = 2.dp,
        large = 3.dp,
    )

    val provided: ComponentBorderStrokes = borders ?: ComponentBorderStrokes(
        outline = BorderStrokes(
            widths = widths,
            defaultColor = MaterialTheme.colorScheme.outlineVariant
        ),
        divider = BorderStrokes(
            widths = widths,
            defaultColor = MaterialTheme.colorScheme.outline
        )
    )

    CompositionLocalProvider(
        value = LocalComponentBorderStrokes provides provided,
        content = content
    )
}
