package com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderlist.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import com.gpcasiapac.storesystems.common.presentation.theme.themedBorder
import com.gpcasiapac.storesystems.foundation.design_system.Dimens

@Composable
fun MultiSelectBottomBar(
    selectedCount: Int,
    isSelectAllChecked: Boolean,
    onSelectAllToggle: (Boolean) -> Unit,
    onCancelClick: () -> Unit,
    onSelectClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    BottomAppBar(
        modifier = modifier.themedBorder(shape = RectangleShape),
        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
        contentPadding = PaddingValues(horizontal = Dimens.Space.medium, vertical = 0.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Dimens.Space.semiMedium),
                modifier = Modifier
                    .padding(Dimens.Space.semiMedium)
                    .clickable() {
                        onSelectAllToggle(!isSelectAllChecked)
                    }
            ) {
                Checkbox(
                    checked = isSelectAllChecked,
                    onCheckedChange = null
                )

                Text(
                    text = "SELECT ALL",
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(Dimens.Space.extraSmall),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    onClick = onCancelClick,
                    shape = MaterialTheme.shapes.small,
                ) {
                    Text(
                        text = "CANCEL"
                    )
                }
                Button(
                    onClick = onSelectClick,
                    modifier = Modifier.height(Dimens.Size.buttonSizeSmall),
                    contentPadding = PaddingValues(
                        horizontal = Dimens.Space.medium,
                        vertical = Dimens.Space.extraSmall
                    )
                ) {
                    Text(
                        text = buildString {
                            append("SELECT ")
                            if (selectedCount > 0) {
                                append(selectedCount.toString())
                            }
                        }
                    )
                }
            }
        }
    }
}