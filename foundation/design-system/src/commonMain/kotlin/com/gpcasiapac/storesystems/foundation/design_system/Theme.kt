package com.gpcasiapac.storesystems.foundation.design_system

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MotionScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.gpcasiapac.storesystems.common.presentation.compose.theme.BorderSize
import com.gpcasiapac.storesystems.common.presentation.compose.theme.BorderWidths
import com.gpcasiapac.storesystems.common.presentation.compose.theme.ComponentBorders
import com.gpcasiapac.storesystems.common.presentation.compose.theme.BorderColors
import com.gpcasiapac.storesystems.common.presentation.compose.theme.ProvideBorders

/**
 * GPC Material 3 Theme
 *
 * This is the main theme composable that should be used by all apps in the project.
 * It provides a consistent Material 3 design system with custom GPC branding.
 *
 * @param darkTheme Whether to use dark theme. Defaults to system setting.
 * @param content The content to be themed.
 */
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun GPCTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {

    val componentBorders = ComponentBorders(
        widths = BorderWidths(
            small = 1.dp,
            medium = 2.dp,
            large = 3.dp
        ),
        defaultSize = BorderSize.Small
    )

    MaterialTheme(
        colorScheme = GPCLightColorScheme,
        typography = GPCTypography,
        shapes = GPCShapes,
        motionScheme = MotionScheme.expressive()
    ) {

        val borderColors = BorderColors(
            outline = MaterialTheme.colorScheme.outline,
            variant = MaterialTheme.colorScheme.outlineVariant,
            error = MaterialTheme.colorScheme.error,
            selected = MaterialTheme.colorScheme.primary,
        )

        val extendedScheme = GpcExtendedColorScheme(
            success = MaterialTheme.colorScheme.tertiary,
            onSuccess = MaterialTheme.colorScheme.onTertiary,
            successContainer = MaterialTheme.colorScheme.tertiaryContainer,
            onSuccessContainer = MaterialTheme.colorScheme.onTertiaryContainer,
            info = MaterialTheme.colorScheme.secondary,
            onInfo = MaterialTheme.colorScheme.onSecondary,
            infoContainer = MaterialTheme.colorScheme.secondaryContainer,
            onInfoContainer = MaterialTheme.colorScheme.onSecondaryContainer,
        )

        ProvideBorders(
            borders = componentBorders,
            colors = borderColors
        ) {
            ProvideGpcExtendedColorScheme(scheme = extendedScheme) {
                content()
            }
        }

    }
}
