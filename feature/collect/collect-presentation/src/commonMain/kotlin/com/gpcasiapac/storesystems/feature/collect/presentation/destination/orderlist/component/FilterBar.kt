package com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderlist.component

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Sort
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.outlined.FilterAlt
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.gpcasiapac.storesystems.common.presentation.compose.theme.borderStroke
import com.gpcasiapac.storesystems.feature.collect.domain.model.CustomerType
import com.gpcasiapac.storesystems.feature.collect.presentation.component.StickyHeaderScrollBehavior
import com.gpcasiapac.storesystems.foundation.design_system.Dimens
import com.gpcasiapac.storesystems.foundation.design_system.GPCTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * Filter bar component with filter chips and generic additional filter toggles
 * Follows Material Design 3 guidelines and theme-driven styling
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterBar(
    customerTypeFilterList: Set<CustomerType>,
    onToggleCustomerType: (type: CustomerType, checked: Boolean) -> Unit,
    onSelectAction: () -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(horizontal = Dimens.Space.medium),
    scrollBehavior: StickyHeaderScrollBehavior? = null
) {

    val isLifted = scrollBehavior?.isLifted ?: false

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
        ) {

            // Fixed expand filter icon on the far left
            OutlinedIconButton(
                onClick = {
                    // TODO: Handle filter action
                },
                modifier = Modifier.size(Dimens.Size.buttonSizeSmall),
                border = MaterialTheme.borderStroke()  // TODO: Remove overrides when M3 updates colors
            ) {
                Icon(
                    imageVector = Icons.Outlined.FilterAlt,
                    contentDescription = "Filter",
                    modifier = Modifier.size(Dimens.Size.iconSmall)
                )
            }

            Spacer(Modifier.width(Dimens.Space.small))

            // Horizontally scrolling row: filter chips + sort button
            Row(
                horizontalArrangement = Arrangement.spacedBy(Dimens.Space.small),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .weight(1f)
                    .horizontalScroll(rememberScrollState())
            ) {
                // Filter chips row
                Row(
                    horizontalArrangement = Arrangement.spacedBy(Dimens.Space.small),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    // TODO: Move this logic to ViewModel
                    val b2bSelected = CustomerType.B2B in customerTypeFilterList
                    val b2cSelected = CustomerType.B2C in customerTypeFilterList

                    FilterChip(
                        label = { Text("B2B") },
                        selected = b2bSelected,
                        onClick = { onToggleCustomerType(CustomerType.B2B, !b2bSelected) }
                    )

                    FilterChip(
                        label = { Text("B2C") },
                        selected = b2cSelected,
                        onClick = { onToggleCustomerType(CustomerType.B2C, !b2cSelected) }
                    )
                }

                OutlinedIconButton(
                    onClick = {
                        // TODO: Handle sort action
                    },
                    shape = MaterialTheme.shapes.small,
                    modifier = Modifier.size(Dimens.Size.buttonSizeSmall),
                    border = MaterialTheme.borderStroke()  // TODO: Remove overrides when M3 updates colors
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.Sort,
                        contentDescription = "Sort",
                        modifier = Modifier.size(Dimens.Size.iconSmall)
                    )
                }
            }

            VerticalDivider(Modifier.height(Dimens.Size.buttonSizeSmall))

            Spacer(Modifier.width(Dimens.Space.small))

            // Right side - SELECT button
            OutlinedButton(
                onClick = onSelectAction,
                modifier = Modifier.height(Dimens.Size.buttonSizeSmall),
                contentPadding = PaddingValues(
                    horizontal = Dimens.Space.semiMedium,
                    vertical = 0.dp
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Checklist,
                    contentDescription = null,
                    modifier = Modifier.size(ButtonDefaults.IconSize)
                )
                Spacer(Modifier.width(ButtonDefaults.IconSpacing))
                Text("SELECT")
            }
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
private fun FilterBarNoPhonePreview() {
    GPCTheme {
        Surface {
            var selectedFilters by remember { mutableStateOf(setOf(CustomerType.B2B)) }

            FilterBar(
                customerTypeFilterList = selectedFilters,
                onToggleCustomerType = { type, checked ->
                    selectedFilters = if (selectedFilters.contains(type)) {
                        selectedFilters - type
                    } else {
                        selectedFilters + type
                    }
                },
                onSelectAction = { /* Handle select */ }
            )
        }
    }
}
