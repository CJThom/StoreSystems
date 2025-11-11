package com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderlist.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gpcasiapac.storesystems.common.presentation.compose.placeholder.material3.placeholder
import com.gpcasiapac.storesystems.common.presentation.compose.theme.borderStroke
import com.gpcasiapac.storesystems.feature.collect.presentation.component.StickyHeaderScrollBehavior
import com.gpcasiapac.storesystems.foundation.design_system.Dimens
import com.gpcasiapac.storesystems.foundation.design_system.GPCTheme

/**
 * Multi-select bar designed to replace FilterBar within the sticky header.
 * Matches FilterBar sizing, padding and elevated styling so swapping looks seamless.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MultiSelectTopBar(
    selectedCount: Int,
    isSelectAllChecked: Boolean,
    onSelectAllToggle: (Boolean) -> Unit,
    onCancelClick: () -> Unit,
    onSelectClick: () -> Unit,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    contentPadding: PaddingValues = PaddingValues(horizontal = Dimens.Space.medium, vertical = Dimens.Space.small),
    scrollBehavior: StickyHeaderScrollBehavior? = null,
    forceLifted: Boolean = false,
) {
    val isLifted = forceLifted || (scrollBehavior?.isLifted ?: false)

    Surface(
        modifier = modifier.fillMaxWidth(),
        border = if (isLifted) MaterialTheme.borderStroke() else null,
        color = if (isLifted) MaterialTheme.colorScheme.surfaceContainer else Color.Transparent,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(contentPadding),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Left: Select All toggle
            TextButton(
                onClick = { onSelectAllToggle(!isSelectAllChecked) },
                modifier = Modifier
                    .placeholder(isLoading)
                    .height(Dimens.Size.buttonSizeSmall),
                contentPadding = PaddingValues(
                    horizontal = Dimens.Space.semiMedium,
                    vertical = 0.dp
                )
            ) {
                Checkbox(
                    checked = isSelectAllChecked,
                    onCheckedChange = null
                )
                Spacer(Modifier.size(Dimens.Space.medium))
                Text(
                    text = "SELECT ALL",
                    style = MaterialTheme.typography.bodySmall
                )
            }

            // Right: Actions
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Dimens.Space.extraSmall)
            ) {
                TextButton(
                    onClick = onCancelClick,
                    modifier = Modifier
                        .placeholder(isLoading)
                        .height(Dimens.Size.buttonSizeSmall),
                    contentPadding = PaddingValues(
                        horizontal = Dimens.Space.semiMedium,
                        vertical = 0.dp
                    )
                ) {
                    Text(text = "CANCEL")
                }

                Button(
                    onClick = onSelectClick,
                    modifier = Modifier
                        .placeholder(isLoading)
                        .height(Dimens.Size.buttonSizeSmall),
                    contentPadding = PaddingValues(
                        horizontal = Dimens.Space.semiMedium,
                        vertical = 0.dp
                    )
                ) {
                    Text(
                        text = buildString {
                            append("SELECT")
                            if (selectedCount > 0) {
                                append(" ")
                                append(selectedCount)
                            }
                        }
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun MultiSelectTopBarPreview() {
    GPCTheme {
        MultiSelectTopBar(
            selectedCount = 3,
            isSelectAllChecked = true,
            onSelectAllToggle = {},
            onCancelClick = {},
            onSelectClick = {},
            scrollBehavior = null
        )
    }
}
