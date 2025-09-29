package com.gpcasiapac.storesystems.common.presentation.theme

import androidx.compose.foundation.BorderStroke
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Common (app-agnostic) border contracts and CompositionLocal carrier.
 * This module should not contain brand- or app-specific values.
 */
@Stable
data class BorderWidths(
    val small: Dp,
    val medium: Dp,
    val large: Dp,
)

@Stable
data class BorderStrokes(
    val widths: BorderWidths,
    val defaultColor: Color,
) {
    fun small(color: Color = defaultColor): BorderStroke = BorderStroke(widths.small, color)
    fun medium(color: Color = defaultColor): BorderStroke = BorderStroke(widths.medium, color)
    fun large(color: Color = defaultColor): BorderStroke = BorderStroke(widths.large, color)
}

@Stable
data class ComponentBorders(
    val card: BorderStrokes,
    val divider: BorderStrokes,
    // Add more components as needed
)

// Preview-safe, neutral defaults. These are NOT brand values.
val LocalComponentBorders = staticCompositionLocalOf {
    val widths = BorderWidths(
        small = 1.dp,
        medium = 2.dp,
        large = 3.dp,
    )
    ComponentBorders(
        card = BorderStrokes(widths, defaultColor = Color.Gray),
        divider = BorderStrokes(widths, defaultColor = Color.Gray),
    )
}

