package com.gpcasiapac.storesystems.feature.collect.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.OutlinedTextField
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import com.gpcasiapac.storesystems.common.presentation.compose.theme.borderStroke
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderfulfillment.OrderFulfilmentScreenContract
import com.gpcasiapac.storesystems.foundation.component.HeaderSmall
import com.gpcasiapac.storesystems.foundation.design_system.Dimens
import com.gpcasiapac.storesystems.foundation.design_system.GPCTheme

@Composable
fun IdVerificationSection(
    selected: OrderFulfilmentScreenContract.IdVerificationOption?,
    onSelected: (OrderFulfilmentScreenContract.IdVerificationOption) -> Unit,
    otherText: String,
    onOtherTextChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(Dimens.Space.medium),
) {
    Column(modifier = modifier) {
        HeaderSmall(
            text = "ID Verification",
            //contentPadding = contentPadding
        )

        Column(
            modifier = Modifier
                .selectableGroup()
                .padding(
                    start = contentPadding.calculateStartPadding(LocalLayoutDirection.current),
                    end = contentPadding.calculateEndPadding(LocalLayoutDirection.current),
                ),
            verticalArrangement = Arrangement.spacedBy(Dimens.Space.small)
        ) {
            IdOptionRow(
                text = "Driver's license",
                selected = selected == OrderFulfilmentScreenContract.IdVerificationOption.DRIVERS_LICENSE,
                onClick = { onSelected(OrderFulfilmentScreenContract.IdVerificationOption.DRIVERS_LICENSE) }
            )
            IdOptionRow(
                text = "Passport",
                selected = selected == OrderFulfilmentScreenContract.IdVerificationOption.PASSPORT,
                onClick = { onSelected(OrderFulfilmentScreenContract.IdVerificationOption.PASSPORT) }
            )
            IdOptionRow(
                text = "Other",
                selected = selected == OrderFulfilmentScreenContract.IdVerificationOption.OTHER,
                onClick = { onSelected(OrderFulfilmentScreenContract.IdVerificationOption.OTHER) }
            )

            AnimatedVisibility(
                visible = selected == OrderFulfilmentScreenContract.IdVerificationOption.OTHER,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                OutlinedTextField(
                    value = otherText,
                    onValueChange = onOtherTextChange,
                    singleLine = true,
                    label = { Text("Specify ID") },
                    modifier = Modifier                        .fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun IdOptionRow(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .selectable(
                selected = selected,
                onClick = onClick,
                role = Role.RadioButton
            ),
        shape = MaterialTheme.shapes.medium,
        border = MaterialTheme.borderStroke(),
        tonalElevation = 0.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Dimens.Space.medium),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = selected,
                onClick = null // null for accessibility; row handles clicks
            )
            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(start = Dimens.Space.medium)
            )
        }
    }
}

// ---- Previews ----
private data class IdVerificationPreviewData(
    val selected: OrderFulfilmentScreenContract.IdVerificationOption?,
    val otherText: String = ""
)

private class IdVerificationPreviewProvider : PreviewParameterProvider<IdVerificationPreviewData> {
    override val values: Sequence<IdVerificationPreviewData> = sequenceOf(
        IdVerificationPreviewData(null),
        IdVerificationPreviewData(OrderFulfilmentScreenContract.IdVerificationOption.DRIVERS_LICENSE),
        IdVerificationPreviewData(OrderFulfilmentScreenContract.IdVerificationOption.PASSPORT),
        IdVerificationPreviewData(OrderFulfilmentScreenContract.IdVerificationOption.OTHER),
    )
}

@Preview(showBackground = true)
@Composable
private fun IdVerificationSectionPreview(
    @PreviewParameter(IdVerificationPreviewProvider::class) data: IdVerificationPreviewData
) {
    GPCTheme {
        IdVerificationSection(
            selected = data.selected,
            onSelected = {},
            otherText = if (data.selected == OrderFulfilmentScreenContract.IdVerificationOption.OTHER) "Some ID" else "",
            onOtherTextChange = {}
        )
    }
}