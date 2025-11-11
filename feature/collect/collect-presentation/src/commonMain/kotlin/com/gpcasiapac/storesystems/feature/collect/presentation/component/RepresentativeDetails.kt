package com.gpcasiapac.storesystems.feature.collect.presentation.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.gpcasiapac.storesystems.common.presentation.compose.placeholder.material3.placeholder
import com.gpcasiapac.storesystems.foundation.design_system.Dimens
import com.gpcasiapac.storesystems.foundation.design_system.GPCTheme

@Composable
fun RepresentativeDetails(
    name: String,
    customerNumber: String,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false
) {
    Column(
        modifier = modifier.padding(Dimens.Space.medium),
        verticalArrangement = Arrangement.spacedBy(Dimens.Space.extraSmall)
    ) {
        // Representative name
        Text(
            text = name,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Medium
            ),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.placeholder(visible = isLoading)
        )
        // Customer number
        Text(
            text = customerNumber,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.inverseSurface,
            modifier = Modifier.placeholder(visible = isLoading)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun RepresentativeDetailsPreview() {
    GPCTheme {
        Surface {
            RepresentativeDetails(
                name = "John Doe",
                customerNumber = "#9288180049912"
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun RepresentativeDetailsLoadingPreview() {
    GPCTheme {
        Surface {
            RepresentativeDetails(
                name = "John Doe",
                customerNumber = "#9288180049912",
                isLoading = true
            )
        }
    }
}