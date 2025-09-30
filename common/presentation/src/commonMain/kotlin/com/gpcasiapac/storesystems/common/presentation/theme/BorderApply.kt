package com.gpcasiapac.storesystems.common.presentation.theme

import androidx.compose.foundation.border
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape

/**
 * Generic, portable border modifiers.
 *
 * What they do:
 * - Build a BorderStroke using tokenized widths from MaterialTheme.componentBorders
 * - Resolve color via role mapping from MaterialTheme.borderColors (or accept overrides)
 * - Apply a shape (defaults to MaterialTheme.shapes.small) to the border
 *
 * Why `size` is nullable:
 * - When size is null, the modifier resolves to MaterialTheme.componentBorders.defaultSize.
 *   This keeps a single place (the theme/provider) to control the app-wide default size.
 *
 * Why `shape` is nullable (resolved internally):
 * - We pass shape: Shape? = null and resolve inside to MaterialTheme.shapes.small. This avoids
 *   composition-timing pitfalls of reading theme values in default parameters, centralizes the
 *   default across all overloads, and makes it easy to change policy later (e.g., role-based shape)
 *   without changing the API.
 *
 * Override precedence:
 * - themedBorder(brush = …) > themedBorder(color = …) > themedBorder(role = …)
 *
 * Note: Providers are optional. If not provided, role colors fall back to MaterialTheme.colorScheme
 * defaults (Material 3 safe defaults), and widths come from LocalComponentBorders' preview-safe
 * defaults. Supplying providers in your app theme ensures your project-specific tokens are used.
 */

/**
 * Applies a role-based border using tokenized widths and a theme-provided color mapping.
 *
 * @param role Color intent for the border; resolved via MaterialTheme.borderColors.
 * @param size Optional size selector. If null, resolves to MaterialTheme.componentBorders.defaultSize.
 * @param shape Optional shape. If null, resolves inside to MaterialTheme.shapes.small.
 */
@Composable
fun Modifier.themedBorder(
    role: BorderRole = BorderRole.Variant,
    size: BorderSize? = null,
    shape: Shape? = null
): Modifier {
    val resolvedShape = shape ?: MaterialTheme.shapes.small
    val stroke = MaterialTheme.borderStroke(role = role, size = size)
    return this.border(border = stroke, shape = resolvedShape)
}

/**
 * Applies a color-based border using tokenized widths.
 *
 * @param color Explicit color for the border.
 * @param size Optional size selector. If null, resolves to MaterialTheme.componentBorders.defaultSize.
 * @param shape Optional shape. If null, resolves inside to MaterialTheme.shapes.small.
 */
@Composable
fun Modifier.themedBorder(
    color: Color,
    size: BorderSize? = null,
    shape: Shape? = null
): Modifier {
    val resolvedShape = shape ?: MaterialTheme.shapes.small
    val stroke = MaterialTheme.borderStroke(color = color, size = size)
    return this.border(border = stroke, shape = resolvedShape)
}

/**
 * Applies a brush-based border (e.g., gradient) using tokenized widths.
 *
 * @param brush Brush to paint the border.
 * @param size Optional size selector. If null, resolves to MaterialTheme.componentBorders.defaultSize.
 * @param shape Optional shape. If null, resolves inside to MaterialTheme.shapes.small.
 */
@Composable
fun Modifier.themedBorder(
    brush: Brush,
    size: BorderSize? = null,
    shape: Shape? = null
): Modifier {
    val resolvedShape = shape ?: MaterialTheme.shapes.small
    val stroke = MaterialTheme.borderStroke(brush = brush, size = size)
    return this.border(border = stroke, shape = resolvedShape)
}