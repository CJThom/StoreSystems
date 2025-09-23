package com.gpcasiapac.storesystems.feature.collect.presentation.details

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun OrderDetailsScreen(
    orderId: String,
    modifier: Modifier = Modifier,
    onBack: () -> Unit,
) {
    Column(modifier = modifier.fillMaxSize(), verticalArrangement = Arrangement.Center) {
        Text("Order details for $orderId (stub)")
        Button(onClick = onBack) { Text("Back") }
    }
}
