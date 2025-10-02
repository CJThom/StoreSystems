package com.gpcasiapac.storesystems.common.presentation.compose.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.staticCompositionLocalOf

/**
 * Extended color system – common building blocks only.
 *
 * This module intentionally knows nothing about project/app roles like "success" or "info".
 * It only defines the contracts and plumbing for an extended color scheme so apps (in their
 * design-system module) can declare their own roles and provide a concrete scheme instance.
 */

/**
 * Marker interface for an extended color scheme. The concrete implementation lives in the
 * design-system module and typically exposes role properties (e.g., success, info, etc.).
 */
@Stable
interface ExtendedColorScheme

/**
 * CompositionLocal that holds a project/app-specific [ExtendedColorScheme] when provided by the
 * design-system theme. Null means no scheme has been installed.
 */
val LocalExtendedColorScheme = staticCompositionLocalOf<ExtendedColorScheme> {
    error("ExtendedColorScheme not provided. Wrap your UI in the design-system theme that calls ProvideExtendedColorScheme().")
}

/**
 * Provide a concrete extended color scheme into the composition. The [scheme] type is owned by the
 * design-system module and should implement [ExtendedColorScheme].
 */
@Composable
fun ProvideExtendedColorScheme(
    scheme: ExtendedColorScheme,
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(LocalExtendedColorScheme provides scheme, content = content)
}

/**
 * Accessor for the current extended color scheme. We intentionally do NOT supply a default here,
 * because the common module has no knowledge of project roles or colors. The design-system module
 * should expose a typed accessor (e.g., `val MaterialTheme.extendedColorScheme: GpcExtendedColorScheme`)
 * that returns either the provided scheme or a sensible fallback mapped from Material 3's
 * `MaterialTheme.colorScheme`.
 */
val MaterialTheme.extendedColorSchemeUnsafe: ExtendedColorScheme
    @Composable
    @ReadOnlyComposable
    get() = LocalExtendedColorScheme.current
