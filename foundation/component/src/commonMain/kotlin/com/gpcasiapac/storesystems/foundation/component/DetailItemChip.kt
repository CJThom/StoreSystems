package com.gpcasiapac.storesystems.foundation.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.gpcasiapac.storesystems.foundation.design_system.Dimens
import com.gpcasiapac.storesystems.foundation.design_system.GPCTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * Detail item chip component
 */
@Composable
fun DetailItemChip(
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit,
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer),
        shape = RoundedCornerShape(Dimens.Space.extraSmall),
    ) {
        Row(
            modifier = Modifier.padding(
                horizontal = Dimens.Space.small,
                vertical = Dimens.Space.extraSmall,
            ),
            horizontalArrangement = Arrangement.spacedBy(Dimens.Space.extraSmall),
            verticalAlignment = Alignment.CenterVertically
        ) {
            content()
        }
    }
}

@Preview
@Composable
fun DetailItemChipPreview() {
    GPCTheme {
        DetailItemChip(
            modifier = Modifier.padding(Dimens.Space.medium)
        ) {
            Text("Store Systems")
        }
    }
}