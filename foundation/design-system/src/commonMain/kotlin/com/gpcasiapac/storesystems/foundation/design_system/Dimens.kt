package com.gpcasiapac.storesystems.foundation.design_system

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Common dimensional values for Compose UI components.
 * Provides consistent spacing, sizes, and other dimensional values across the application.
 * Uses the 8dp grid system as the foundation for all spacing values.
 */
object Dimens {
    // Base spacing scale (8dp grid system) - single source of truth
    object Space {
        val extraSmall: Dp = 4.dp
        val small: Dp = 8.dp
        val medium: Dp = 16.dp
        val large: Dp = 24.dp
        val extraLarge: Dp = 32.dp
        val huge: Dp = 48.dp
    }

    // Semantic aliases for specific layout use cases
    object Layout {
        val screenPadding: Dp = Space.medium        // 16dp
        val sectionSpacing: Dp = Space.large        // 24dp
        val contentSpacing: Dp = Space.medium       // 16dp
    }

    // Component sizes
    object Size {
        val iconSmall: Dp = 16.dp
        val iconMedium: Dp = 24.dp
        val iconLarge: Dp = 32.dp
        val iconExtraLarge: Dp = 48.dp

        val buttonHeight: Dp = 48.dp
        val buttonMinWidth: Dp = 64.dp

        val cardElevation: Dp = 4.dp
        val cardCornerRadius: Dp = 8.dp

        val dividerThickness: Dp = 1.dp

        val progressIndicatorSize: Dp = 24.dp
        val progressIndicatorStroke: Dp = 3.dp
    }

    // Text field dimensions
    object TextField {
        val minHeight: Dp = 56.dp
        val cornerRadius: Dp = 4.dp
    }

    // Bottom sheet dimensions
    object BottomSheet {
        val peekHeight: Dp = 56.dp
        val cornerRadius: Dp = 16.dp
    }
}
