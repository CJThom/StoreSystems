package com.gpcasiapac.storesystems.feature.history.presentation.composable

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gpcasiapac.storesystems.foundation.design_system.Dimens
import com.gpcasiapac.storesystems.foundation.design_system.GPCTheme

@Composable
fun InfoPanel(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(Dimens.Space.medium),
    showBorder: Boolean = false,
    content: @Composable () -> Unit,
) {
    val border = if (showBorder) {
        BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outlineVariant
        )
    } else null

    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        ),
        shape = MaterialTheme.shapes.medium,
        border = border
    ) {
        Column(Modifier.padding(contentPadding)) { content() }
    }
}

@Preview
@Composable
fun InfoPanelPreview() {
    GPCTheme {
        Surface {
            InfoPanel(
                modifier = Modifier.padding(10.dp),
                content = {
                    Column {
                        KeyValueRow(label = "Label", value = "Value")
                        KeyValueRow(label = "Label", value = "Value")
                        KeyValueRow(label = "Label", value = "Value")
                    }
                })
        }
    }
}