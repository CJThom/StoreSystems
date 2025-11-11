package com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderfulfillment.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.tooling.preview.Preview
import com.gpcasiapac.storesystems.common.presentation.compose.placeholder.material3.placeholder
import com.gpcasiapac.storesystems.foundation.component.HeaderSmall
import com.gpcasiapac.storesystems.foundation.design_system.Dimens
import com.gpcasiapac.storesystems.foundation.design_system.GPCTheme

@Composable
internal fun CourierCollectionContent(
    courierName: String,
    onCourierNameChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    contentPadding: PaddingValues = PaddingValues(Dimens.Space.medium)
) {
    Column(modifier = modifier) {
        HeaderSmall(
            text = "Courier details",
            contentPadding = PaddingValues(
                top = contentPadding.calculateTopPadding(),
                start = contentPadding.calculateStartPadding(LocalLayoutDirection.current),
                end = contentPadding.calculateEndPadding(LocalLayoutDirection.current),
                bottom = Dimens.Space.medium
            )
        )

        TextField(
            value = courierName,
            onValueChange = onCourierNameChange,
           //  label = { Text("Courier name") },
              placeholder = { Text("Enter courier name") },

            modifier = Modifier
                .padding(
                    start = contentPadding.calculateStartPadding(LocalLayoutDirection.current),
                    end = contentPadding.calculateEndPadding(LocalLayoutDirection.current),
                )
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
        
        Spacer(Modifier.size(contentPadding.calculateBottomPadding()))
    }
}

// Preview parameter model to consolidate cases
private data class CourierPreviewCase(
    val courierName: String,
    val isLoading: Boolean,
    val label: String
) {
    override fun toString(): String = label
}

// Provider enumerating all preview scenarios for maintainability
private class CourierPreviewProvider :
    androidx.compose.ui.tooling.preview.PreviewParameterProvider<CourierPreviewCase> {
    override val values: Sequence<CourierPreviewCase> = sequenceOf(
        CourierPreviewCase(courierName = "", isLoading = false, label = "Empty (placeholder)"),
        CourierPreviewCase(courierName = "Australia Post", isLoading = false, label = "Populated"),
        CourierPreviewCase(
            courierName = "DHL Express",
            isLoading = true,
            label = "Loading with text"
        ),
        CourierPreviewCase(courierName = "", isLoading = true, label = "Loading empty"),
        CourierPreviewCase(
            courierName = "DHL Express International Logistics and Parcel Delivery Services – Very Long Name To Test Overflow",
            isLoading = false,
            label = "Long text"
        )
    )
}

@Preview(showBackground = true)
@Composable
private fun CourierCollectionContentPreview(
    @androidx.compose.ui.tooling.preview.PreviewParameter(CourierPreviewProvider::class) case: CourierPreviewCase
) {
    GPCTheme {
        Surface {
            CourierCollectionContent(
                courierName = case.courierName,
                onCourierNameChange = { },
                isLoading = case.isLoading
            )
        }
    }
}
