package com.gpcasiapac.storesystems.feature.history.presentation.composable

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.gpcasiapac.storesystems.feature.history.domain.model.HistoryMetadata
import com.gpcasiapac.storesystems.feature.history.presentation.mapper.toCustomerTypeParam
import com.gpcasiapac.storesystems.foundation.component.CollectOrderDetailsContent
import com.gpcasiapac.storesystems.foundation.component.CustomerTypeParam
import com.gpcasiapac.storesystems.foundation.component.ListItemScaffold
import com.gpcasiapac.storesystems.foundation.design_system.Dimens

@Composable
fun CollectMetadataRowCard(
    metadata: HistoryMetadata.CollectMetadata,
    modifier: Modifier = Modifier
) {
    ListItemScaffold(
        modifier = modifier,
        contentPadding = PaddingValues()
    ) {
        CollectOrderDetailsContent(
            customerName = metadata.getCustomerDisplayName(),
            customerType = metadata.customerType.toCustomerTypeParam(),
            invoiceNumber = metadata.invoiceNumber,
            webOrderNumber = metadata.webOrderNumber,
            isLoading = false,
            contentPadding = PaddingValues(bottom = Dimens.Space.small)
        )
    }
}

@Composable
fun CollectMetadataRowCardSkeleton(
    modifier: Modifier = Modifier
) {
    ListItemScaffold(
        modifier = modifier,
        contentPadding = PaddingValues()
    ) {
        CollectOrderDetailsContent(
            customerName = "",
            customerType = CustomerTypeParam.B2C,
            invoiceNumber = "",
            webOrderNumber = null,
            isLoading = true,
            contentPadding = PaddingValues(bottom = Dimens.Space.small)
        )
    }
}
