package com.gpcasiapac.storesystems.foundation.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

object OutlineCardDefaults {
    val BorderWidth: Dp = 1.dp

    @Composable
    fun borderColor(): Color = MaterialTheme.colorScheme.outlineVariant

    @Composable
    fun containerColor(): Color = Color.Transparent

    @Composable
    fun contentPadding(): PaddingValues = PaddingValues(0.dp)
}

@Composable
fun OutlineCard(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onClick: (() -> Unit)? = null,
    contentPadding: PaddingValues = OutlineCardDefaults.contentPadding(),
    colors: androidx.compose.material3.CardColors = CardDefaults.cardColors(
        containerColor = OutlineCardDefaults.containerColor(),
        contentColor = MaterialTheme.colorScheme.onSurface,
        disabledContainerColor = OutlineCardDefaults.containerColor(),
        disabledContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
    ),
    borderColor: Color = OutlineCardDefaults.borderColor(),
    borderWidth: Dp = OutlineCardDefaults.BorderWidth,
    shape: Shape = CardDefaults.shape,
    elevation: androidx.compose.material3.CardElevation = CardDefaults.cardElevation(0.dp),
    content: @Composable () -> Unit,
) {
    val border = BorderStroke(borderWidth, borderColor)

    if (onClick != null) {
        Card(
            modifier = modifier,
            onClick = onClick,
            enabled = enabled,
            shape = shape,
            colors = colors,
            border = border,
            elevation = elevation,
        ) {
            Box(Modifier.padding(contentPadding)) {
                content()
            }
        }
    } else {
        Card(
            modifier = modifier,
            shape = shape,
            colors = colors,
            border = border,
            elevation = elevation,
        ) {
            Box(Modifier.padding(contentPadding)) {
                content()
            }
        }
    }
}
