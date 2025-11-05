package com.gpcasiapac.storesystems.foundation.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.gpcasiapac.storesystems.common.presentation.compose.placeholder.material3.placeholder
import com.gpcasiapac.storesystems.foundation.component.icon.B2BIcon
import com.gpcasiapac.storesystems.foundation.component.icon.B2CIcon
import com.gpcasiapac.storesystems.foundation.design_system.Dimens
import com.gpcasiapac.storesystems.foundation.design_system.GPCTheme


enum class CustomerTypeParam {
    B2B, B2C
}

@Composable
fun CustomerName(
    customerName: String,
    customerType: CustomerTypeParam,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    contentPadding: PaddingValues = PaddingValues()
) {

    Row(
        modifier = modifier
            .padding(contentPadding)
            .width(IntrinsicSize.Max),
        horizontalArrangement = Arrangement.spacedBy(Dimens.Space.small),
        verticalAlignment = Alignment.CenterVertically
    ) {

        when (customerType) {
            CustomerTypeParam.B2B -> B2BIcon(isLoading = isLoading)
            CustomerTypeParam.B2C -> B2CIcon(isLoading = isLoading)
        }

        Text(
            text = customerName,
            style = MaterialTheme.typography.titleMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .fillMaxWidth()
                .placeholder(isLoading),// TODO: Match Figma / Fix theme Typography,
            autoSize = TextAutoSize.StepBased(maxFontSize = MaterialTheme.typography.titleMedium.fontSize)
        )

    }
}

private data class CustomerNamePreviewData(
    val name: String,
    val type: CustomerTypeParam
)

private class CustomerNamePreviewProvider : PreviewParameterProvider<CustomerNamePreviewData> {
    override val values = sequenceOf(
        CustomerNamePreviewData(
            name = "ABC Motorsports PTY Limited",
            type = CustomerTypeParam.B2B
        ),
        CustomerNamePreviewData(
            name = "Johnathan Josiah Citizenship Esq.",
            type = CustomerTypeParam.B2C
        ),
        CustomerNamePreviewData(
            name = "Short Co",
            type = CustomerTypeParam.B2B,
        )
    )

}

@Preview(showBackground = true)
@Composable
private fun CustomerNamePreview(
    @PreviewParameter(CustomerNamePreviewProvider::class) data: CustomerNamePreviewData
) {
    GPCTheme {
        Surface {
            CustomerName(
                customerName = data.name,
                customerType = data.type
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CustomerNameLoadingPreview(
    @PreviewParameter(CustomerNamePreviewProvider::class) data: CustomerNamePreviewData
) {
    GPCTheme {
        Surface(modifier = Modifier.padding(Dimens.Space.medium)) {
            CustomerName(
                customerName = data.name,
                customerType = data.type,
                isLoading = true
            )
        }
    }
}
