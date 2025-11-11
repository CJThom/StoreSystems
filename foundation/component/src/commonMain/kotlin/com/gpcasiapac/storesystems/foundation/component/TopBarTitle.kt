package com.gpcasiapac.storesystems.foundation.component

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun TopBarTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Medium),
        color = MaterialTheme.colorScheme.onPrimary
    )
}

@Preview
@Composable
fun TopBarTitlePreview() {
    TopBarTitle(
        title = "Store Systems",
    )
}