package com.gpcasiapac.storesystems.common.presentation.theme

import androidx.compose.foundation.BorderStroke
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
    val defaultColor: Color = Color.Unspecified,
) {
    fun small(color: Color = defaultColor): BorderStroke = BorderStroke(widths.small, color)
    fun medium(color: Color = defaultColor): BorderStroke = BorderStroke(widths.medium, color)
    fun large(color: Color = defaultColor): BorderStroke = BorderStroke(widths.large, color)
    /** Default stroke for general usage; aliases to small width. */
    fun default(color: Color = defaultColor): BorderStroke = small(color)
}

@Stable
data class ComponentBorders(
    val outline: BorderStrokes,
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
        outline = BorderStrokes(widths, defaultColor = Color.Gray),
        divider = BorderStrokes(widths, defaultColor = Color.Gray),
    )
}

