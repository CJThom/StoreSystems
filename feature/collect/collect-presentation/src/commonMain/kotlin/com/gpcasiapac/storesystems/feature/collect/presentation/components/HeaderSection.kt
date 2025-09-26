package com.gpcasiapac.storesystems.feature.collect.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.gpcasiapac.storesystems.foundation.design_system.Dimens
import com.gpcasiapac.storesystems.foundation.design_system.GPCTheme
import org.jetbrains.compose.ui.tooling.preview.Preview


/**
 * Header section showing "Ready to collect" with orders count
 */
@Composable
fun HeaderSection(
    ordersCount: Int,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(Dimens.Space.extraSmall)
    ) {
        Text(
            text = "Ready to collect",
            style = typography.headlineLarge.copy(
                fontWeight = FontWeight.Medium
            ),
            color = colors.onBackground
        )

        Row(
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.spacedBy(Dimens.Space.extraSmall)
        ) {
            Text(
                text = ordersCount.toString(),
                style = typography.labelLarge.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = colors.onSurfaceVariant
            )
            Text(
                text = "Orders ready to be collected",
                style = typography.bodySmall,
                color = colors.onSurfaceVariant
            )
        }
    }
}

@Preview
@Composable
fun HeaderSectionPreview() {
    GPCTheme {
        Surface(modifier = Modifier.padding(Dimens.Space.medium)) {
            HeaderSection(ordersCount = 6)
        }
    }
}