package com.gpcasiapac.storesystems.feature.collect.presentation.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExpandedFullScreenSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.SearchBarState
import androidx.compose.material3.SearchBarValue
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.material3.rememberSearchBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.gpcasiapac.storesystems.feature.collect.presentation.component.CollectOrderDetails
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderlist.component.MultiSelectBottomBar
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderlist.model.CollectOrderListItemState
import com.gpcasiapac.storesystems.foundation.component.CheckboxCard
import com.gpcasiapac.storesystems.foundation.design_system.Dimens
import com.gpcasiapac.storesystems.foundation.design_system.GPCTheme
import org.jetbrains.compose.ui.tooling.preview.Preview


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MBoltSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    searchBarState: SearchBarState,
    onSearch: (String) -> Unit,
    onExpandedChange: (Boolean) -> Unit,
    onBackPressed: () -> Unit,
    onResultClick: (String) -> Unit,
    onClearClick: () -> Unit,
    searchResults: List<String>,
    // New: full search results as order items for the expanded grid
    searchOrderItems: List<CollectOrderListItemState>,
    isMultiSelectionEnabled: Boolean,
    selectedOrderIdList: Set<String>,
    isSelectAllChecked: Boolean,
    isRefreshing: Boolean,
    onOpenOrder: (String) -> Unit,
    onCheckedChange: (String, Boolean) -> Unit,
    onSelectAllToggle: (Boolean) -> Unit,
    onCancelSelection: () -> Unit,
    onSelectClick: () -> Unit,
    modifier: Modifier = Modifier,
    placeholderText: String = "Search..."
) {

    // TODO: Somehow sync this with the onExpandedChange of the SearchBarInputField
    // Monitor expansion state changes and notify parent
    LaunchedEffect(searchBarState.currentValue) {
        onExpandedChange(searchBarState.currentValue == SearchBarValue.Expanded)
    }

    Box(modifier = modifier) {
        // Collapsed search bar (lives in TopBar)
        SearchBar(
            modifier = Modifier.padding(Dimens.Space.medium),
            state = searchBarState,
            shape = MaterialTheme.shapes.small,
            inputField = {
                SearchBarInputField(
                    query = query,
                    onQueryChange = onQueryChange,
                    isExpanded = searchBarState.currentValue == SearchBarValue.Expanded,
                    onExpandedChange = onExpandedChange,
                    placeholderText = placeholderText,
                    onSearch = onSearch,
                    onBackPressed = onBackPressed,
                    onClearClick = onClearClick
                )
            }
        )

        // Expanded full-screen search bar (opens in new window)
        ExpandedFullScreenSearchBar(
            modifier = Modifier,
            state = searchBarState,
            collapsedShape = MaterialTheme.shapes.small,
            colors = SearchBarDefaults.colors(
                containerColor = MaterialTheme.colorScheme.surface,
                inputFieldColors = SearchBarDefaults.inputFieldColors(
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    disabledContainerColor = MaterialTheme.colorScheme.surface
                )
            ),
            inputField = {
                SearchBarInputField(
                    query = query,
                    onQueryChange = onQueryChange,
                    isExpanded = searchBarState.currentValue == SearchBarValue.Expanded,
                    onExpandedChange = onExpandedChange,
                    placeholderText = placeholderText,
                    onSearch = onSearch,
                    onBackPressed = onBackPressed,
                    onClearClick = onClearClick,
                )
            }
        ) {
            Column(
                modifier = Modifier.fillMaxHeight(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Search results content (orders grid)
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(Dimens.Adaptive.gridItemWidth),
                    contentPadding = PaddingValues(vertical = Dimens.Space.small),
                ) {
                    if (searchOrderItems.isEmpty() && query.isNotEmpty()) {
                        // Show the empty state inside the grid, spanning full width
                        item(span = { GridItemSpan(maxLineSpan) }) {
                            ListItem(
                                headlineContent = {
                                    Text(
                                        "No results found",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(Dimens.Space.medium)
                            )
                        }
                    } else {
                        items(
                            items = searchOrderItems,
                            key = { it.invoiceNumber }
                        ) { collectOrderState ->
                            CheckboxCard(
                                modifier = Modifier.padding(
                                    horizontal = Dimens.Space.medium,
                                    vertical = Dimens.Space.small
                                ),
                                isCheckable = isMultiSelectionEnabled,
                                isChecked = selectedOrderIdList.contains(collectOrderState.invoiceNumber),
                                onClick = { onOpenOrder(collectOrderState.invoiceNumber) },
                                onCheckedChange = { isChecked ->
                                    onCheckedChange(collectOrderState.invoiceNumber, isChecked)
                                }
                            ) {
                                CollectOrderDetails(
                                    customerName = collectOrderState.customerName,
                                    customerType = collectOrderState.customerType,
                                    invoiceNumber = collectOrderState.invoiceNumber,
                                    webOrderNumber = collectOrderState.webOrderNumber,
                                    pickedAt = collectOrderState.pickedAt,
                                    isLoading = isRefreshing,
                                    contendPadding = PaddingValues(
                                        start = Dimens.Space.medium,
                                        top = Dimens.Space.medium,
                                        bottom = Dimens.Space.medium,
                                        end = if (isMultiSelectionEnabled) 0.dp else Dimens.Space.medium
                                    ),
                                )
                            }
                        }
                    }
                }
                MultiSelectBottomBar(
                    selectedCount = selectedOrderIdList.size,
                    isSelectAllChecked = isSelectAllChecked,
                    onSelectAllToggle = onSelectAllToggle,
                    onCancelClick = onCancelSelection,
                    onSelectClick = onSelectClick
                )
            }

        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchBarInputField(
    query: String,
    onQueryChange: (String) -> Unit,
    isExpanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    placeholderText: String,
    onSearch: (String) -> Unit,
    onBackPressed: () -> Unit,
    onClearClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    SearchBarDefaults.InputField(
        query = query,
        onQueryChange = onQueryChange,
        onSearch = onSearch,
        expanded = isExpanded,
        onExpandedChange = onExpandedChange,
        modifier = modifier.background(MaterialTheme.colorScheme.surface),
        placeholder = {
            Text(
                text = placeholderText,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        leadingIcon = {
            AnimatedContent(
                targetState = isExpanded,
                label = "leading-icon",
            ) { value ->
                if (value) {
                    IconButton(onClick = onBackPressed) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.minimumInteractiveComponentSize()
                    )
                }
            }
        },
        trailingIcon = if (!isExpanded || query.isEmpty()) null else {
            {
                IconButton(onClick = onClearClick) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "Clear search",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        },
        colors = SearchBarDefaults.inputFieldColors(
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
            disabledContainerColor = MaterialTheme.colorScheme.surface
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(name = "Search Bar - Collapsed")
@Composable
fun MBoltSearchBarCollapsedPreview() {
    GPCTheme {
        MBoltSearchBar(
            query = "",
            onQueryChange = {},
            searchBarState = rememberSearchBarState(initialValue = SearchBarValue.Collapsed),
            onSearch = {},
            onExpandedChange = {},
            onBackPressed = {},
            onResultClick = {},
            onClearClick = {},
            searchResults = listOf(
                "Order #12345 - John Doe",
                "Order #12346 - Jane Smith",
                "Order #12347 - Bob Johnson"
            ),
            searchOrderItems = emptyList(),
            isMultiSelectionEnabled = false,
            selectedOrderIdList = emptySet(),
            isSelectAllChecked = false,
            isRefreshing = false,
            onOpenOrder = {},
            onCheckedChange = { _, _ -> },
            onSelectAllToggle = {},
            onCancelSelection = {},
            onSelectClick = {},
            placeholderText = "Search by Order #, Name, Phone"
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(name = "Search Bar - Expanded with Suggestions")
@Composable
fun MBoltSearchBarExpandedSuggestionsPreview() {
    GPCTheme {
        MBoltSearchBar(
            query = "John",
            onQueryChange = {},
            searchBarState = rememberSearchBarState(initialValue = SearchBarValue.Expanded),
            onSearch = {},
            onExpandedChange = {},
            onBackPressed = {},
            onResultClick = {},
            onClearClick = {},
            searchResults = listOf(
                "Order #12345 - John Doe",
                "Order #12346 - Jane Smith",
                "Order #12347 - Bob Johnson",
                "Order #12348 - John Williams"
            ),
            searchOrderItems = emptyList(),
            isMultiSelectionEnabled = false,
            selectedOrderIdList = emptySet(),
            isSelectAllChecked = false,
            isRefreshing = false,
            onOpenOrder = {},
            onCheckedChange = { _, _ -> },
            onSelectAllToggle = {},
            onCancelSelection = {},
            onSelectClick = {},
            placeholderText = "Search by Order #, Name, Phone"
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(name = "Search Bar - Expanded with Orders Grid")
@Composable
fun MBoltSearchBarExpandedOrdersPreview() {
    GPCTheme {
        val items =
            com.gpcasiapac.storesystems.feature.collect.presentation.destination.sampleCollectOrderListItemStateList()
        MBoltSearchBar(
            query = "Jo",
            onQueryChange = {},
            searchBarState = rememberSearchBarState(initialValue = SearchBarValue.Expanded),
            onSearch = {},
            onExpandedChange = {},
            onBackPressed = {},
            onResultClick = {},
            onClearClick = {},
            searchResults = emptyList(),
            searchOrderItems = items.take(6),
            isMultiSelectionEnabled = false,
            selectedOrderIdList = emptySet(),
            isSelectAllChecked = false,
            isRefreshing = false,
            onOpenOrder = {},
            onCheckedChange = { _, _ -> },
            onSelectAllToggle = {},
            onCancelSelection = {},
            onSelectClick = {},
            placeholderText = "Search by Order #, Name, Phone"
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(name = "Search Bar - Expanded Multi-Select")
@Composable
fun MBoltSearchBarExpandedMultiSelectPreview() {
    GPCTheme {
        val items =
            com.gpcasiapac.storesystems.feature.collect.presentation.destination.sampleCollectOrderListItemStateList()
        val selected = setOf(items[0].invoiceNumber, items[2].invoiceNumber)
        MBoltSearchBar(
            query = "Order",
            onQueryChange = {},
            searchBarState = rememberSearchBarState(initialValue = SearchBarValue.Expanded),
            onSearch = {},
            onExpandedChange = {},
            onBackPressed = {},
            onResultClick = {},
            onClearClick = {},
            searchResults = emptyList(),
            searchOrderItems = items.take(8),
            isMultiSelectionEnabled = true,
            selectedOrderIdList = selected,
            isSelectAllChecked = false,
            isRefreshing = false,
            onOpenOrder = {},
            onCheckedChange = { _, _ -> },
            onSelectAllToggle = {},
            onCancelSelection = {},
            onSelectClick = {},
            placeholderText = "Search by Order #, Name, Phone"
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(name = "Search Bar - Expanded Select All")
@Composable
fun MBoltSearchBarExpandedSelectAllPreview() {
    GPCTheme {
        val items =
            com.gpcasiapac.storesystems.feature.collect.presentation.destination.sampleCollectOrderListItemStateList()
                .take(5)
        val allIds = items.map { it.invoiceNumber }.toSet()
        MBoltSearchBar(
            query = "",
            onQueryChange = {},
            searchBarState = rememberSearchBarState(initialValue = SearchBarValue.Expanded),
            onSearch = {},
            onExpandedChange = {},
            onBackPressed = {},
            onResultClick = {},
            onClearClick = {},
            searchResults = emptyList(),
            searchOrderItems = items,
            isMultiSelectionEnabled = true,
            selectedOrderIdList = allIds,
            isSelectAllChecked = true,
            isRefreshing = false,
            onOpenOrder = {},
            onCheckedChange = { _, _ -> },
            onSelectAllToggle = {},
            onCancelSelection = {},
            onSelectClick = {},
            placeholderText = "Search by Order #, Name, Phone"
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(name = "Search Bar - Expanded No Results")
@Composable
fun MBoltSearchBarExpandedNoResultsPreview() {
    GPCTheme {
        MBoltSearchBar(
            query = "xyz",
            onQueryChange = {},
            searchBarState = rememberSearchBarState(initialValue = SearchBarValue.Expanded),
            onSearch = {},
            onExpandedChange = {},
            onBackPressed = {},
            onResultClick = {},
            onClearClick = {},
            searchResults = emptyList(),
            searchOrderItems = emptyList(),
            isMultiSelectionEnabled = false,
            selectedOrderIdList = emptySet(),
            isSelectAllChecked = false,
            isRefreshing = false,
            onOpenOrder = {},
            onCheckedChange = { _, _ -> },
            onSelectAllToggle = {},
            onCancelSelection = {},
            onSelectClick = {},
            placeholderText = "Search by Order #, Name, Phone"
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(name = "Search Bar - Expanded Refreshing")
@Composable
fun MBoltSearchBarExpandedRefreshingPreview() {
    GPCTheme {
        val items =
            com.gpcasiapac.storesystems.feature.collect.presentation.destination.sampleCollectOrderListItemStateList()
                .take(4)
        MBoltSearchBar(
            query = "Smith",
            onQueryChange = {},
            searchBarState = rememberSearchBarState(initialValue = SearchBarValue.Expanded),
            onSearch = {},
            onExpandedChange = {},
            onBackPressed = {},
            onResultClick = {},
            onClearClick = {},
            searchResults = emptyList(),
            searchOrderItems = items,
            isMultiSelectionEnabled = false,
            selectedOrderIdList = emptySet(),
            isSelectAllChecked = false,
            isRefreshing = true,
            onOpenOrder = {},
            onCheckedChange = { _, _ -> },
            onSelectAllToggle = {},
            onCancelSelection = {},
            onSelectClick = {},
            placeholderText = "Search by Order #, Name, Phone"
        )
    }
}
