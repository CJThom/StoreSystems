package com.gpcasiapac.storesystems.foundation.component

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.gpcasiapac.storesystems.foundation.design_system.Dimens
import com.gpcasiapac.storesystems.foundation.design_system.GPCTheme
import com.gpcasiapac.storesystems.foundation.design_system.MBoltIcons
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * Delivery time chip component
 */
@Composable
fun QuantityChip(
    quantity: Int,
    modifier: Modifier = Modifier
) {
    DetailItemChip(modifier = modifier) {
        Icon(
            //TODO Find an Icon for this, Drawable doesn't work in preview.
            //imageVector = MBoltIcons.DeployedCode,
            imageVector = Icons.Default.AddBox,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onTertiaryContainer,
            modifier = Modifier.size(Dimens.Size.iconMedium)
        )

        Text(
            text = quantity.toString(),
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Medium
            ),
            color = MaterialTheme.colorScheme.onTertiaryContainer
        )
    }
}

@Preview
@Composable
fun QuantityChipPreview() {
    GPCTheme {
        QuantityChip(
            quantity = 5,
            modifier = Modifier.padding(Dimens.Space.medium)
        )
    }
}