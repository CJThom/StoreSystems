package com.gpcasiapac.storesystems.common.presentation.theme

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape

/**
 * Generic, portable border modifiers that live in the common/presentation layer.
 * They use widths from MaterialTheme.componentBorders and resolve colors via
 * MaterialTheme.borderStroke(…) using the current role mapping (MaterialTheme.borderColors).
 * A default shape of MaterialTheme.shapes.small is applied when not specified.
 */

@Composable
fun Modifier.themedBorder(
    role: BorderRole = BorderRole.Variant,
    size: BorderSize = BorderSize.Default,
    shape: Shape? = null
): Modifier {
    val resolvedShape = shape ?: MaterialTheme.shapes.small
    val stroke = MaterialTheme.borderStroke(role = role, size = size)
    return this.border(border = stroke, shape = resolvedShape)
}

@Composable
fun Modifier.themedBorder(
    color: Color,
    size: BorderSize = BorderSize.Default,
    shape: Shape? = null
): Modifier {
    val resolvedShape = shape ?: MaterialTheme.shapes.small
    val stroke = MaterialTheme.borderStroke(color = color, size = size)
    return this.border(border = stroke, shape = resolvedShape)
}

@Composable
fun Modifier.themedBorder(
    brush: Brush,
    size: BorderSize = BorderSize.Default,
    shape: Shape? = null
): Modifier {
    val resolvedShape = shape ?: MaterialTheme.shapes.small
    val stroke = MaterialTheme.borderStroke(brush = brush, size = size)
    return this.border(border = stroke, shape = resolvedShape)
}