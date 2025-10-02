package com.gpcasiapac.storesystems.foundation.design_system

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color
import com.gpcasiapac.storesystems.common.presentation.compose.theme.ExtendedColorScheme as CommonExtendedColorScheme
import com.gpcasiapac.storesystems.common.presentation.compose.theme.LocalExtendedColorScheme
import com.gpcasiapac.storesystems.common.presentation.compose.theme.ProvideExtendedColorScheme

/**
 * Project/app concrete extended color scheme with explicit roles.
 *
 * Exposes role pairs similar to Material 3's ColorScheme: background and matching on* content colors.
 */
data class GpcExtendedColorScheme(
    val success: Color,
    val onSuccess: Color,
    val successContainer: Color,
    val onSuccessContainer: Color,
    val info: Color,
    val onInfo: Color,
    val infoContainer: Color,
    val onInfoContainer: Color,
) : CommonExtendedColorScheme

/**
 * Accessor that returns the concrete scheme type so call sites can write:
 * `MaterialTheme.extendedColorScheme.success`
 *
 * Fallback behavior: if not provided, map to Material 3 colorScheme so previews still render.
 */
val MaterialTheme.extendedColorScheme: GpcExtendedColorScheme
    @Composable
    @ReadOnlyComposable
    get() = when (val scheme = LocalExtendedColorScheme.current) {
        is GpcExtendedColorScheme -> scheme
        else -> error("ExtendedColorScheme not provided (or wrong type). Ensure GPCTheme calls ProvideGpcExtendedColorScheme().")
    }

/**
 * Mirrors Material 3's ColorScheme.contentColorFor behavior for extended roles.
 */
@Composable
fun GpcExtendedColorScheme.contentColorFor(background: Color): Color = when (background) {
    success -> onSuccess
    successContainer -> onSuccessContainer
    info -> onInfo
    infoContainer -> onInfoContainer
    else -> MaterialTheme.colorScheme.contentColorFor(background)
}

/**
 * Provider helper your theme can call to install the extended color scheme.
 * Strict: no defaults here; a scheme must be explicitly supplied by the theme.
 */
@Composable
fun ProvideGpcExtendedColorScheme(
    scheme: GpcExtendedColorScheme,
    content: @Composable () -> Unit
) {
    ProvideExtendedColorScheme(scheme, content)
}
