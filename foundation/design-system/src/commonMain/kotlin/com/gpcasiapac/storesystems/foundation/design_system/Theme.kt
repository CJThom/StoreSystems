package com.gpcasiapac.storesystems.foundation.design_system

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

/**
 * GPC Material 3 Theme
 *
 * This is the main theme composable that should be used by all apps in the project.
 * It provides a consistent Material 3 design system with custom GPC branding.
 *
 * @param darkTheme Whether to use dark theme. Defaults to system setting.
 * @param content The content to be themed.
 */
@Composable
fun GPCTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = GPCLightColorScheme,
        typography = GPCTypography,
        shapes = GPCShapes,
    ) {
        // Provide widths (tokens) and role colors; consumers can override colors per subtree if needed
        com.gpcasiapac.storesystems.common.presentation.theme.ProvideComponentBorders {
            com.gpcasiapac.storesystems.common.presentation.theme.ProvideBorderColors {
                content()
            }
        }
    }
}
