package com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderlist.component

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Sort
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.outlined.FilterAlt
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import com.gpcasiapac.storesystems.common.presentation.compose.placeholder.material3.placeholder
import com.gpcasiapac.storesystems.common.presentation.compose.theme.borderStroke
import com.gpcasiapac.storesystems.feature.collect.domain.model.CustomerType
import com.gpcasiapac.storesystems.feature.collect.presentation.component.StickyHeaderScrollBehavior
import com.gpcasiapac.storesystems.foundation.design_system.Dimens
import com.gpcasiapac.storesystems.foundation.design_system.GPCTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * Unified toolbar for OrderList that hosts both Filter content and Multi-select actions
 * inside a single Surface. The inner content swaps using AnimatedContent while the
 * container Surface handles the lifted look.
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun OrderListToolbar(
    isMultiSelectionEnabled: Boolean,
    // Filter content params
    customerTypeFilterList: Set<CustomerType>,
    onToggleCustomerType: (type: CustomerType, checked: Boolean) -> Unit,
    onSelectAction: () -> Unit,
    // Multi-select content params
    selectedCount: Int,
    isSelectAllChecked: Boolean,
    onSelectAllToggle: (Boolean) -> Unit,
    onCancelClick: () -> Unit,
    onSelectClick: () -> Unit,
    // Common params
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    contentPadding: PaddingValues = PaddingValues(horizontal = Dimens.Space.medium),
    scrollBehavior: StickyHeaderScrollBehavior? = null,
) {
    // Force-lift when multi-select is active; otherwise follow scroll behavior
    val isLifted = isMultiSelectionEnabled || (scrollBehavior?.isLifted ?: false)

    Surface(
        modifier = modifier.fillMaxWidth()
            .defaultMinSize(minHeight = FilterChipDefaults.Height + Dimens.Space.medium),
        border = if (isLifted) MaterialTheme.borderStroke() else null,
        color = if (isLifted) MaterialTheme.colorScheme.surfaceContainer else Color.Transparent,
    ) {
        AnimatedContent(
            targetState = isMultiSelectionEnabled,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            label = "OrderListToolbarContent"
        ) { isMulti ->
            if (!isMulti) {
                // FILTER CONTENT
                OrderListToolbarFilterContent(
                    customerTypeFilterList = customerTypeFilterList,
                    onToggleCustomerType = onToggleCustomerType,
                    onSelectAction = onSelectAction,
                    isLoading = isLoading,
                    contentPadding = contentPadding,
                )
            } else {
                // MULTI-SELECT CONTENT
                OrderListToolbarMultiSelectContent(
                    selectedCount = selectedCount,
                    isSelectAllChecked = isSelectAllChecked,
                    onSelectAllToggle = onSelectAllToggle,
                    onCancelClick = onCancelClick,
                    onSelectClick = onSelectClick,
                    isLoading = isLoading,
                    contentPadding = contentPadding,
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun OrderListToolbarFilterContent(
    customerTypeFilterList: Set<CustomerType>,
    onToggleCustomerType: (type: CustomerType, checked: Boolean) -> Unit,
    onSelectAction: () -> Unit,
    isLoading: Boolean = false,
    contentPadding: PaddingValues = PaddingValues(horizontal = Dimens.Space.medium),
) {
    val buttonHeight = ButtonDefaults.ExtraSmallContainerHeight
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(contentPadding),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Left: Filter button
        OutlinedIconButton(
            onClick = {
                // TODO: Handle filter action
            },
            modifier = Modifier
                .placeholder(isLoading)
                .size(buttonHeight),
            border = MaterialTheme.borderStroke()
        ) {
            Icon(
                imageVector = Icons.Outlined.FilterAlt,
                contentDescription = "Filter",
                modifier = Modifier.size(ButtonDefaults.iconSizeFor(buttonHeight))
            )
        }

        Spacer(Modifier.width(Dimens.Space.small))

        // Center: Chips + Sort
        Row(
            horizontalArrangement = Arrangement.spacedBy(Dimens.Space.small),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .weight(1f)
                .horizontalScroll(rememberScrollState())
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(Dimens.Space.small),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val b2bSelected = CustomerType.B2B in customerTypeFilterList
                val b2cSelected = CustomerType.B2C in customerTypeFilterList

                FilterChip(
                    label = {
                        Text(
                            text = "B2B",
                            modifier = Modifier.placeholder(isLoading)
                        )
                    },
                    selected = b2bSelected,
                    onClick = { onToggleCustomerType(CustomerType.B2B, !b2bSelected) },
                )

                FilterChip(
                    label = {
                        Text(
                            text = "B2C",
                            modifier = Modifier.placeholder(isLoading)
                        )
                    },
                    selected = b2cSelected,
                    onClick = { onToggleCustomerType(CustomerType.B2C, !b2cSelected) }
                )
            }

            OutlinedIconButton(
                onClick = {
                    // TODO: Handle sort action
                },
                shape = MaterialTheme.shapes.small,
                modifier = Modifier
                    .placeholder(isLoading)
                    .size(buttonHeight),
                border = MaterialTheme.borderStroke()
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.Sort,
                    contentDescription = "Sort",
                    modifier = Modifier.size(ButtonDefaults.iconSizeFor(buttonHeight))
                )
            }
        }

        VerticalDivider(Modifier.height(buttonHeight))

        Spacer(Modifier.width(Dimens.Space.small))

        // Right: Enter multi-select
        OutlinedButton(
            onClick = onSelectAction,
            modifier = Modifier
                .placeholder(isLoading)
                .height(buttonHeight),
            contentPadding = ButtonDefaults.contentPaddingFor(buttonHeight)
        ) {
            Icon(
                imageVector = Icons.Filled.Checklist,
                contentDescription = null,
                modifier = Modifier.size(ButtonDefaults.iconSizeFor(buttonHeight))
            )
            Spacer(Modifier.width(ButtonDefaults.iconSpacingFor(buttonHeight)))
            Text("SELECT")
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun OrderListToolbarMultiSelectContent(
    selectedCount: Int,
    isSelectAllChecked: Boolean,
    onSelectAllToggle: (Boolean) -> Unit,
    onCancelClick: () -> Unit,
    onSelectClick: () -> Unit,
    isLoading: Boolean = false,
    contentPadding: PaddingValues = PaddingValues(horizontal = Dimens.Space.medium),
) {
    val buttonHeight = ButtonDefaults.ExtraSmallContainerHeight
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                top = contentPadding.calculateTopPadding(),
                bottom = contentPadding.calculateBottomPadding(),
                end = contentPadding.calculateEndPadding(LocalLayoutDirection.current),
                start = contentPadding.calculateStartPadding(LocalLayoutDirection.current) - Dimens.Space.small
            ),
        // .padding(contentPadding),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {

        TextButton(
            onClick = { onSelectAllToggle(!isSelectAllChecked) },
            modifier = Modifier
                .placeholder(isLoading)
                .height(buttonHeight),
            contentPadding = ButtonDefaults.contentPaddingFor(buttonHeight)
        ) {
            Checkbox(
                checked = isSelectAllChecked,
                onCheckedChange = null
            )
            Spacer(Modifier.width(ButtonDefaults.iconSpacingFor(buttonHeight)))
            Text(
                text = "SELECT ALL",
                style = MaterialTheme.typography.bodySmall
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Dimens.Space.extraSmall)
        ) {
            TextButton(
                onClick = onCancelClick,
                modifier = Modifier
                    .placeholder(isLoading)
                    .height(buttonHeight),
                contentPadding = ButtonDefaults.contentPaddingFor(buttonHeight)
            ) {
                Text(text = "CANCEL")
            }

            Button(
                onClick = onSelectClick,
                modifier = Modifier
                    .placeholder(isLoading)
                    .height(buttonHeight),
                contentPadding = ButtonDefaults.contentPaddingFor(buttonHeight)
            ) {
                Icon(
                    imageVector = Icons.Filled.DoneAll,
                    contentDescription = null,
                    modifier = Modifier.size(ButtonDefaults.iconSizeFor(buttonHeight))
                )
                Spacer(Modifier.width(ButtonDefaults.iconSpacingFor(buttonHeight)))
                Text(
                    text = buildString {
                        append("ACCEPT")
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


@Preview(showBackground = true)
@Composable
private fun OrderListToolbarPreview_Normal() {
    GPCTheme {
        OrderListToolbar(
            isMultiSelectionEnabled = false,
            customerTypeFilterList = setOf(CustomerType.B2B),
            onToggleCustomerType = { _, _ -> },
            onSelectAction = {},
            selectedCount = 0,
            isSelectAllChecked = false,
            onSelectAllToggle = {},
            onCancelClick = {},
            onSelectClick = {},
            scrollBehavior = null
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun OrderListToolbarPreview_Multi() {
    GPCTheme {
        OrderListToolbar(
            isMultiSelectionEnabled = true,
            customerTypeFilterList = emptySet(),
            onToggleCustomerType = { _, _ -> },
            onSelectAction = {},
            selectedCount = 3,
            isSelectAllChecked = true,
            onSelectAllToggle = {},
            onCancelClick = {},
            onSelectClick = {},
            scrollBehavior = null
        )
    }
}
