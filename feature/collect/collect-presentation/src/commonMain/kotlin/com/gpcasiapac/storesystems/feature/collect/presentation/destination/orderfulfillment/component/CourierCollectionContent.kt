package com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderfulfillment.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.gpcasiapac.storesystems.common.presentation.compose.placeholder.material3.placeholder
import com.gpcasiapac.storesystems.foundation.design_system.GPCTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
internal fun CourierCollectionContent(
    courierName: String,
    onCourierNameChange: (String) -> Unit,
    isLoading: Boolean = false
) {
    OutlinedTextField(
        value = courierName,
        onValueChange = onCourierNameChange,
        label = { Text("Courier name") },
        modifier = Modifier
            .fillMaxWidth()
            .placeholder(visible = isLoading),
        trailingIcon = {
            if (courierName.isNotEmpty()) {
                IconButton(onClick = { onCourierNameChange("") }) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Clear"
                    )
                }
            }
        }
    )
}

@Preview
@Composable
private fun CourierCollectionContentPreview() {
    var courierName by remember { mutableStateOf("DHL Express") }
    GPCTheme {
        Surface {
            CourierCollectionContent(
                courierName = courierName,
                onCourierNameChange = { courierName = it }
            )
        }
    }
}

@Preview
@Composable
private fun CourierCollectionContentLoadingPreview() {
    GPCTheme {
        Surface {
            CourierCollectionContent(
                courierName = "DHL Express",
                onCourierNameChange = { },
                isLoading = true
            )
        }
    }
}