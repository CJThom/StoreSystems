package com.gpcasiapac.storesystems.feature.collect.presentation.components


import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BackHand
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.gpcasiapac.storesystems.foundation.component.DetailItemChip
import com.gpcasiapac.storesystems.foundation.design_system.Dimens
import com.gpcasiapac.storesystems.foundation.design_system.GPCTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * Delivery time chip component
 */
@Composable
fun DeliveryTimeChip(
    deliveryTime: String,
    modifier: Modifier = Modifier
) {
    DetailItemChip(modifier = modifier) {
        Icon(
            imageVector = Icons.Outlined.BackHand,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onTertiaryContainer,
            modifier = Modifier.size(Dimens.Size.iconSmall)
        )

        Text(
            text = deliveryTime,
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Medium
            ),
            color = MaterialTheme.colorScheme.onTertiaryContainer
        )
    }
}

@Preview
@Composable
fun DeliveryTimeChipPreview() {
    GPCTheme {
        DeliveryTimeChip(
            deliveryTime = "12:30 PM",
            modifier = Modifier.padding(Dimens.Space.medium)
        )
    }
}