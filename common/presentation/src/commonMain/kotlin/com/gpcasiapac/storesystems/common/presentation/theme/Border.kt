package com.gpcasiapac.storesystems.common.presentation.theme

import androidx.compose.foundation.BorderStroke
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

// Width tokens (single source of truth for sizes)
@Stable
data class BorderWidths(
    val small: Dp,
    val medium: Dp,
    val large: Dp,
)

// Size selector
enum class BorderSize { Default, Small, Medium, Large }

// Semantic roles (color intent)
@Stable
sealed interface BorderRole {
    data object Outline : BorderRole
    data object Variant : BorderRole
    data object Error : BorderRole
    data object Selected : BorderRole
}

// Public carrier for component-level border tokens
@Stable
data class ComponentBorders(
    val widths: BorderWidths,
    val defaultSize: BorderSize = BorderSize.Default, // Optional ergonomic default
)

// Preview-safe defaults (not brand values)
val LocalComponentBorders = staticCompositionLocalOf {
    ComponentBorders(
        widths = BorderWidths(
            small = 1.dp,
            medium = 2.dp,
            large = 3.dp,
        )
    )
}

// Role → color mapping (theme provides real values)
@Stable
data class BorderColors(
    val outline: Color,
    val variant: Color,
    val error: Color,
    val selected: Color,
)

// Preview-safe color defaults (not brand values)
val LocalBorderColors = staticCompositionLocalOf {
    BorderColors(
        outline = Color(0xFF9E9E9E),
        variant = Color(0xFFBDBDBD),
        error = Color(0xFFB00020),
        selected = Color(0xFF2962FF),
    )
}

// ——— Helpers ———
fun BorderWidths.forSize(size: BorderSize): Dp = when (size) {
    BorderSize.Default, BorderSize.Small -> small
    BorderSize.Medium -> medium
    BorderSize.Large -> large
}

fun borderStrokeFrom(
    widths: BorderWidths,
    color: Color,
    size: BorderSize = BorderSize.Default,
): BorderStroke = BorderStroke(widths.forSize(size), color)

fun borderStrokeFromRole(
    colors: BorderColors,
    role: BorderRole,
    widths: BorderWidths,
    size: BorderSize = BorderSize.Default,
): BorderStroke = borderStrokeFrom(
    widths = widths,
    color = when (role) {
        BorderRole.Outline -> colors.outline
        BorderRole.Variant -> colors.variant
        BorderRole.Error -> colors.error
        BorderRole.Selected -> colors.selected
    },
    size = size
)

// ——— MaterialTheme accessors + providers ———
val MaterialTheme.componentBorders: ComponentBorders
    @Composable
    @ReadOnlyComposable
    get() = LocalComponentBorders.current

@Composable
fun ProvideComponentBorders(
    borders: ComponentBorders? = null,
    content: @Composable () -> Unit
) {
    val fallback = ComponentBorders(
        widths = BorderWidths(small = 1.dp, medium = 2.dp, large = 3.dp)
    )
    CompositionLocalProvider(LocalComponentBorders provides (borders ?: fallback), content = content)
}

val MaterialTheme.borderColors: BorderColors
    @Composable
    @ReadOnlyComposable
    get() = LocalBorderColors.current

@Composable
fun ProvideBorderColors(
    colors: BorderColors = BorderColors(
        outline = MaterialTheme.colorScheme.outline,
        variant = MaterialTheme.colorScheme.outlineVariant,
        error = MaterialTheme.colorScheme.error,
        selected = MaterialTheme.colorScheme.primary,
    ),
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(LocalBorderColors provides colors, content = content)
}

@Composable
fun MaterialTheme.borderStroke(
    role: BorderRole = BorderRole.Variant,
    size: BorderSize = BorderSize.Default
): BorderStroke = borderStrokeFromRole(
    colors = borderColors,
    role = role,
    widths = componentBorders.widths,
    size = size,
)

@Composable
fun MaterialTheme.borderStroke(
    color: Color,
    size: BorderSize = BorderSize.Default
): BorderStroke = borderStrokeFrom(
    widths = componentBorders.widths,
    color = color,
    size = size,
)

@Composable
fun MaterialTheme.borderStroke(
    brush: androidx.compose.ui.graphics.Brush,
    size: BorderSize = BorderSize.Default
): BorderStroke = BorderStroke(
    width = componentBorders.widths.forSize(size),
    brush = brush
)
