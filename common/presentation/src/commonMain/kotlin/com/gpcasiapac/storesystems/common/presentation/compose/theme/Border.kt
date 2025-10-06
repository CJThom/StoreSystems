package com.gpcasiapac.storesystems.common.presentation.compose.theme

import androidx.compose.foundation.BorderStroke
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Width tokens for borders used by components.
 *
 * This is the single source of truth for border sizes (small/medium/large) in the common layer.
 * No colors or shapes live here; those are provided separately so apps can map roles → colors
 * and choose shapes via their theme.
 */
@Stable
data class BorderWidths(
    val small: Dp,
    val medium: Dp,
    val large: Dp,
)

/**
 * Size selector for borders.
 *
 * Note: We no longer expose a "Default" enum value. Instead, APIs that take a size accept
 * `size: BorderSize?` and, when `null`, resolve to `MaterialTheme.componentBorders.defaultSize`.
 * This keeps a single place to define the project-wide default without duplicating it at call sites.
 */
enum class BorderSize { Small, Medium, Large }

/**
 * Semantic roles for border color intent.
 *
 * These do not carry colors themselves. Colors are supplied by [BorderColors] via
 * [ProvideBorderColors], typically mapped from Material3's colorScheme in your theme.
 */
@Stable
sealed interface BorderRole {
    data object Outline : BorderRole
    data object Variant : BorderRole
    data object Error : BorderRole
    data object Selected : BorderRole
}

/**
 * Component-level border tokens.
 *
 * - [widths]: the size scale used across the app.
 * - [defaultSize]: the size to use when a caller passes `size = null`.
 *
 * Why a nullable size in APIs?
 * - We accept `size: BorderSize?` on builders so that the default can be defined here in one place
 *   (project/app foundation) and changed without touching all call sites.
 */
@Stable
data class ComponentBorders(
    val widths: BorderWidths,
    val defaultSize: BorderSize = BorderSize.Small, // ergonomic default
)

/**
 * CompositionLocal providing optional [ComponentBorders] overrides.
 *
 * If not provided, MaterialTheme.componentBorders will fall back to preview-safe defaults
 * (1/2/3 dp; default size Small), mirroring M3 behavior.
 */
val LocalComponentBorders = staticCompositionLocalOf<ComponentBorders?> { null }

/**
 * Role → color mapping for borders.
 *
 * Supplied by your theme via [ProvideBorderColors], typically mapped from Material3's colorScheme:
 * - outline → colorScheme.outline
 * - variant → colorScheme.outlineVariant
 * - error → colorScheme.error
 * - selected → colorScheme.primary
 */
@Stable
data class BorderColors(
    val outline: Color,
    val variant: Color,
    val error: Color,
    val selected: Color,
)

/**
 * CompositionLocal providing optional [BorderColors] overrides.
 *
 * If not provided, [MaterialTheme.borderColors] will fall back to mapping roles from
 * [MaterialTheme.colorScheme] (outline, outlineVariant, error, primary), mirroring how Material 3
 * components use safe defaults when no theme is explicitly provided.
 *
 * You can still call [ProvideBorderColors] to override roles globally or for a subtree.
 */
val LocalBorderColors = staticCompositionLocalOf<BorderColors?> {
    // Null indicates no explicit override; callers fall back to MaterialTheme.colorScheme
    null
}

// ——— Helpers ———
/** Returns the appropriate width for the given [size]. */
fun BorderWidths.forSize(size: BorderSize): Dp = when (size) {
    BorderSize.Small -> small
    BorderSize.Medium -> medium
    BorderSize.Large -> large
}

/** Builds a [BorderStroke] from tokenized [widths] and an explicit [color]. */
fun borderStrokeFrom(
    widths: BorderWidths,
    color: Color,
    size: BorderSize = BorderSize.Small,
): BorderStroke = BorderStroke(widths.forSize(size), color)

/**
 * Builds a [BorderStroke] from tokenized [widths] and a role-mapped color resolved from [colors].
 * Use this to keep call sites semantic: choose intent via [role], intensity via [size].
 */
fun borderStrokeFromRole(
    colors: BorderColors,
    role: BorderRole,
    widths: BorderWidths,
    size: BorderSize = BorderSize.Small,
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
/**
 * Accessor for the current [ComponentBorders].
 *
 * Returns the value supplied by [ProvideComponentBorders] or a preview-safe default if none was provided.
 */
val MaterialTheme.componentBorders: ComponentBorders
    @Composable
    @ReadOnlyComposable
    get() = LocalComponentBorders.current ?: ComponentBorders(
        widths = BorderWidths(
            small = 1.dp,
            medium = 2.dp,
            large = 3.dp,
        ),
        defaultSize = BorderSize.Small,
    )

/**
 * Provides [ComponentBorders] (width tokens) to the composition.
 *
 * Override-only: if you don't call this, MaterialTheme.componentBorders falls back
 * to preview-safe defaults (1/2/3 dp; default size Small), mirroring M3 behavior.
 */
@Composable
fun ProvideComponentBorders(
    borders: ComponentBorders,
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(LocalComponentBorders provides borders, content = content)
}

@Composable
fun ProvideComponentBorders(
    borders: ComponentBorders,
    colors: BorderColors,
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(LocalComponentBorders provides borders, LocalBorderColors provides colors, content = content)
}

/**
 * Accessor for the current [BorderColors] role mapping.
 *
 * If no explicit colors are provided via [ProvideBorderColors], falls back to Material3's
 * [MaterialTheme.colorScheme] (outline, outlineVariant, error, primary), mirroring M3 defaults.
 */
val MaterialTheme.borderColors: BorderColors
    @Composable
    @ReadOnlyComposable
    get() = LocalBorderColors.current ?: BorderColors(
        outline = MaterialTheme.colorScheme.outline,
        variant = MaterialTheme.colorScheme.outlineVariant,
        error = MaterialTheme.colorScheme.error,
        selected = MaterialTheme.colorScheme.primary,
    )

/**
 * Provides [BorderColors] (role -> color mapping) to the composition.
 *
 * Override-only: if not provided, MaterialTheme.borderColors falls back to Material3 colorScheme
 * (outline, outlineVariant, error, primary).
 */
@Composable
fun ProvideBorderColors(
    colors: BorderColors,
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(LocalBorderColors provides colors, content = content)
}

/**
 * Builds a [BorderStroke] using a role-mapped color and tokenized widths.
 *
 * Why `size` is nullable: if `null`, we resolve to [ComponentBorders.defaultSize] so there is a
 * single place to control the default across the app without changing every call site.
 */
@Composable
fun MaterialTheme.borderStroke(
    role: BorderRole = BorderRole.Variant,
    size: BorderSize? = null
): BorderStroke {
    val resolvedSize = size ?: componentBorders.defaultSize
    return borderStrokeFromRole(
        colors = borderColors,
        role = role,
        widths = componentBorders.widths,
        size = resolvedSize,
    )
}

/**
 * Builds a [BorderStroke] using an explicit [color] with tokenized widths.
 *
 * `size` follows the same nullable convention as the role-based overload.
 */
@Composable
fun MaterialTheme.borderStroke(
    color: Color,
    size: BorderSize? = null
): BorderStroke {
    val resolvedSize = size ?: componentBorders.defaultSize
    return borderStrokeFrom(
        widths = componentBorders.widths,
        color = color,
        size = resolvedSize,
    )
}

/**
 * Builds a [BorderStroke] using a [brush] (e.g., gradient) and tokenized widths.
 *
 * `size` follows the same nullable convention as the other overloads.
 */
@Composable
fun MaterialTheme.borderStroke(
    brush: Brush,
    size: BorderSize? = null
): BorderStroke {
    val resolvedSize = size ?: componentBorders.defaultSize
    return BorderStroke(
        width = componentBorders.widths.forSize(resolvedSize),
        brush = brush
    )
}


/**
 * Provides both border width tokens and role colors in a single call.
 *
 * This façade is functionally equivalent to calling:
 *
 *   ProvideComponentBorders(borders) { ProvideBorderColors(colors) { content() } }
 *
 * Use this when you want to set both in one place (e.g., in your app theme).
 * Individual providers remain available for targeted overrides.
 */
@Composable
fun ProvideBorders(
    borders: ComponentBorders,
    colors: BorderColors,
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(
        LocalComponentBorders provides borders,
        LocalBorderColors provides colors,
        content = content
    )
}
