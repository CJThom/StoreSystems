package com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderlist.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderlist.OrderListScreenContract
import com.gpcasiapac.storesystems.foundation.design_system.Dimens

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MultiSelectConfirmDialog(
    title: String,
    cancelLabel: String,
    selectOnlyLabel: String,
    proceedLabel: String,
    onProceed: () -> Unit,
    onSelect: () -> Unit,
    onCancel: () -> Unit,
    onDismissRequest: () -> Unit,
) {
    BasicAlertDialog(
        onDismissRequest = onDismissRequest
    ) {
        Surface(
            shape = MaterialTheme.shapes.extraLarge,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier.padding(Dimens.Space.large),
                verticalArrangement = Arrangement.spacedBy(Dimens.Space.medium)
            ) {
                // Title
                Text(text = title, style = MaterialTheme.typography.headlineSmall)

                // Actions
                HorizontalDivider()

                Column(
                    verticalArrangement = Arrangement.spacedBy(Dimens.Space.small),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TextButton(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = onProceed
                    ) { Text(proceedLabel) }

                    TextButton(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = onSelect
                    ) { Text(selectOnlyLabel) }

                    TextButton(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = onCancel
                    ) { Text(cancelLabel) }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
private fun MultiSelectConfirmDialogPreview() {
    MultiSelectConfirmDialog(
        title = "Confirm selection",
        cancelLabel = "Cancel",
        selectOnlyLabel = "Select only",
        proceedLabel = "Select and proceed",
        onProceed = {},
        onSelect = {},
        onCancel = {},
        onDismissRequest = {}
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
private fun MultiSelectConfirmDialogEmptySummaryPreview() {
    MultiSelectConfirmDialog(
        title = "Confirm selection",
        cancelLabel = "Cancel",
        selectOnlyLabel = "Select only",
        proceedLabel = "Select and proceed",
        onProceed = {},
        onSelect = {},
        onCancel = {},
        onDismissRequest = {}
    )
}
