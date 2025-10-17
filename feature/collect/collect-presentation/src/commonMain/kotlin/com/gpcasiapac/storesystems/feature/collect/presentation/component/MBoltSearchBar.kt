package com.gpcasiapac.storesystems.feature.collect.presentation.component

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
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
import androidx.compose.material3.SearchBarColors
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.SearchBarState
import androidx.compose.material3.SearchBarValue
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.material3.rememberSearchBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderlist.component.OrderListToolbar
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderlist.model.CollectOrderListItemState
import com.gpcasiapac.storesystems.foundation.component.CheckboxCard
import com.gpcasiapac.storesystems.foundation.design_system.Dimens
import com.gpcasiapac.storesystems.foundation.design_system.GPCTheme
import org.jetbrains.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.ime
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.composed
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.platform.LocalDensity
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.sampleCollectOrderListItemStateList

@OptIn(ExperimentalLayoutApi::class)
@Stable
fun Modifier.clearFocusOnKeyboardDismiss(): Modifier = composed {
    var isFocused by remember { mutableStateOf(false) }
    var keyboardAppearedSinceLastFocused by remember { mutableStateOf(false) }

    if (isFocused) {
        val imeIsVisible = WindowInsets.ime.getBottom(LocalDensity.current) > 0
        val focusManager = LocalFocusManager.current

        LaunchedEffect(imeIsVisible) {
            if (imeIsVisible) {
                keyboardAppearedSinceLastFocused = true
            } else if (keyboardAppearedSinceLastFocused) {
                focusManager.clearFocus()
            }
        }
    }

    onFocusEvent {
        if (isFocused != it.isFocused) {
            isFocused = it.isFocused
            if (isFocused) keyboardAppearedSinceLastFocused = false
        }
    }
}


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
    onEnterSelectionMode: () -> Unit,
    onSelectClick: () -> Unit,
    modifier: Modifier = Modifier,
    placeholderText: String = "Search...",
    // Style overrides for collapsed bar (optional)
    collapsedContentPadding: PaddingValues = PaddingValues(Dimens.Space.medium),
    collapsedShape: Shape = MaterialTheme.shapes.small,
    collapsedColors: SearchBarColors = SearchBarDefaults.colors(),
    collapsedBorder: BorderStroke? = null,
) {

    // TODO: Somehow sync this with the onExpandedChange of the SearchBarInputField
    // Monitor expansion state changes and notify parent
    var previousSearchBarValue by remember { mutableStateOf(searchBarState.currentValue) }
    LaunchedEffect(searchBarState.currentValue) {
        val current = searchBarState.currentValue
        onExpandedChange(current == SearchBarValue.Expanded)
        // Clear the search text whenever the search bar collapses
        if (previousSearchBarValue == SearchBarValue.Expanded && current == SearchBarValue.Collapsed) {
            onClearClick()
        }
        previousSearchBarValue = current
    }

    // Search results content (orders grid)
    val lazyGridState = rememberLazyGridState()
    val stickyHeaderScrollBehavior = StickyBarDefaults.liftOnScrollBehavior(
        lazyGridState = lazyGridState,
        stickyHeaderIndex = 0
    )

    // Auto-scroll to prevent sticky header overlap when it appears
    LaunchedEffect(searchOrderItems.isNotEmpty()) {
        if (searchOrderItems.isNotEmpty()) {
            if (lazyGridState.firstVisibleItemIndex == 0 && lazyGridState.firstVisibleItemScrollOffset == 0) {
                // Scroll so that the sticky header sits at the top and doesn't cover content
                lazyGridState.animateScrollToItem(0)
            }
        }
    }

    Box(modifier = Modifier) {
        // Collapsed search bar (embedded)
        Surface(
            modifier = modifier.padding(collapsedContentPadding),
            shape = collapsedShape,
            color = Color.Transparent,
            border = collapsedBorder,
        ) {
            SearchBar(
                modifier = Modifier,
                state = searchBarState,
                shape = collapsedShape,
                colors = collapsedColors,
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
                      //  modifier = Modifier.clearFocusOnKeyboardDismiss()
                    )
                }
            )
        }

        Surface {
            // Expanded full-screen search bar (opens in new window)
            ExpandedFullScreenSearchBar(
                modifier = Modifier,
                state = searchBarState,
                collapsedShape = collapsedShape,
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
                        modifier = Modifier
                    )
                }
            ) {
                // Local controllers from dialog window scope for reliable IME control
                val overlayFocusManager = LocalFocusManager.current
                val overlayKeyboardController = LocalSoftwareKeyboardController.current

                LazyVerticalGrid(
                    state = lazyGridState,
                    columns = GridCells.Adaptive(Dimens.Adaptive.gridItemWidth),
                ) {
                    // Toolbar in sticky header with animated visibility
                    stickyHeader {
                        AnimatedVisibility(
                            visible = searchOrderItems.isNotEmpty(),
                            enter = expandVertically(animationSpec = tween(250)) + fadeIn(),
                            exit = shrinkVertically(animationSpec = tween(200)) + fadeOut(),
                            label = "SearchToolbarVisibility"
                        ) {
                            OrderListToolbar(
                                isMultiSelectionEnabled = isMultiSelectionEnabled,
                                customerTypeFilterList = emptySet(),
                                onToggleCustomerType = { _, _ -> },
                                onSelectAction = {
                                    overlayKeyboardController?.hide()
                                    overlayFocusManager.clearFocus(force = true)
                                    onEnterSelectionMode()
                                },
                                selectedCount = selectedOrderIdList.size,
                                isSelectAllChecked = isSelectAllChecked,
                                onSelectAllToggle = onSelectAllToggle,
                                onCancelClick = onCancelSelection,
                                onSelectClick = onSelectClick,
                                isLoading = isRefreshing,
                                scrollBehavior = stickyHeaderScrollBehavior,
                            )
                        }
                    }

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
                                modifier = Modifier
                                    .padding(
                                        horizontal = Dimens.Space.medium,
                                        vertical = Dimens.Space.small
                                    )
                                    .animateItem()
                                    .animateContentSize(),
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
    colors: TextFieldColors = SearchBarDefaults.inputFieldColors(
        focusedContainerColor = Color.Transparent,
        unfocusedContainerColor = Color.Transparent,
        disabledContainerColor = Color.Transparent,
    ),
) {
        val focusManager = LocalFocusManager.current
        val keyboardController = LocalSoftwareKeyboardController.current

        SearchBarDefaults.InputField(
            query = query,
            onQueryChange = onQueryChange,
            onSearch = { q ->
                keyboardController?.hide()
                focusManager.clearFocus(force = true)
                onSearch(q)
            },
            expanded = isExpanded,
            onExpandedChange = onExpandedChange,
            modifier = modifier,
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
            colors = colors
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
            onEnterSelectionMode = {},
            onSelectAllToggle = {},
            onCancelSelection = {},
            onSelectClick = {},
            placeholderText = "Search by Order #, Name, Phone",
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
            onEnterSelectionMode = {},
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
            sampleCollectOrderListItemStateList()
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
            onEnterSelectionMode = {},
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
            sampleCollectOrderListItemStateList()
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
            onEnterSelectionMode = {},
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
            sampleCollectOrderListItemStateList()
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
            onEnterSelectionMode = {},
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
            onEnterSelectionMode = {},
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
            sampleCollectOrderListItemStateList()
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
            onEnterSelectionMode = {},
            onSelectAllToggle = {},
            onCancelSelection = {},
            onSelectClick = {},
            placeholderText = "Search by Order #, Name, Phone"
        )
    }
}
