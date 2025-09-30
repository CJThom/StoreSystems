package com.gpcasiapac.storesystems.foundation.design_system

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.gpcasiapac.storesystems.common.presentation.theme.BorderSize
import com.gpcasiapac.storesystems.common.presentation.theme.BorderWidths
import com.gpcasiapac.storesystems.common.presentation.theme.ComponentBorders
import com.gpcasiapac.storesystems.common.presentation.theme.ProvideComponentBorders

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
        // Provide width tokens; install extended colors scheme for project defaults.
        ProvideComponentBorders(
            borders = ComponentBorders(
                widths = BorderWidths(
                    small = 1.dp,
                    medium = 2.dp,
                    large = 3.dp
                ),
                defaultSize = BorderSize.Small
            )
        ) {
            ProvideGpcExtendedColorScheme(
                scheme = GpcExtendedColorScheme(
                    success = MaterialTheme.colorScheme.tertiary,
                    onSuccess = MaterialTheme.colorScheme.onTertiary,
                    successContainer = MaterialTheme.colorScheme.tertiaryContainer,
                    onSuccessContainer = MaterialTheme.colorScheme.onTertiaryContainer,
                    info = MaterialTheme.colorScheme.secondary,
                    onInfo = MaterialTheme.colorScheme.onSecondary,
                    infoContainer = MaterialTheme.colorScheme.secondaryContainer,
                    onInfoContainer = MaterialTheme.colorScheme.onSecondaryContainer,
                )
            ) {
                content()
            }
        }
    }

}
