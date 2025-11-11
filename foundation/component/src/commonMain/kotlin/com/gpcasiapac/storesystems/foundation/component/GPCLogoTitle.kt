package com.gpcasiapac.storesystems.foundation.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.gpcasiapac.storesystems.foundation.design_system.Dimens

@Composable
fun GPCLogoTitle(
    title: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Dimens.Space.small)
    ) {
        GPCLogoIcon()
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Medium
            )
        )
    }
}

@Preview
@Composable
private fun GPCLogoTitlePreview() {
    GPCLogoTitle("Store Systems")
}
