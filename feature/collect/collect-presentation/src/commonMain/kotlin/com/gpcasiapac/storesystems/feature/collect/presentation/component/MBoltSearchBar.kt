package com.gpcasiapac.storesystems.feature.collect.presentation.component

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExpandedFullScreenSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.InputChip
import androidx.compose.material3.InputChipDefaults
import androidx.compose.material3.ListItem
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarColors
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.SearchBarState
import androidx.compose.material3.SearchBarValue
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.material3.rememberSearchBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.gpcasiapac.storesystems.feature.collect.domain.model.CustomerType
import com.gpcasiapac.storesystems.feature.collect.presentation.components.HeaderSmall
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderlist.component.CollectOrderItem
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderlist.component.OrderListToolbar
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderlist.model.CollectOrderListItemState
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.sampleCollectOrderListItemStateList
import com.gpcasiapac.storesystems.foundation.component.CheckboxCard
import com.gpcasiapac.storesystems.foundation.component.icon.B2BIcon
import com.gpcasiapac.storesystems.foundation.component.icon.B2CIcon
import com.gpcasiapac.storesystems.foundation.design_system.Dimens
import com.gpcasiapac.storesystems.foundation.design_system.GPCTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

// TODO: Move to common
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

    // Chips state and clear logic
    val inputChips = remember { mutableStateListOf<String>() }
    val chipsQueryBase = inputChips.joinToString(" ")
    val clearAll: () -> Unit = {
        inputChips.clear()
        onClearClick()
    }

    // Monitor expansion state changes and notify parent
    var previousSearchBarValue by remember { mutableStateOf(searchBarState.currentValue) }
    LaunchedEffect(searchBarState.currentValue) {
        val current = searchBarState.currentValue
        onExpandedChange(current == SearchBarValue.Expanded)
        // Clear both input chips and search text whenever the search bar collapses
        if (previousSearchBarValue == SearchBarValue.Expanded && current == SearchBarValue.Collapsed) {
            clearAll()
        }
        previousSearchBarValue = current
    }

    // Build a name -> CustomerType map to power icons for chips
    val customerTypeByName = remember(searchOrderItems) {
        val source =
            if (searchOrderItems.isNotEmpty()) searchOrderItems else sampleCollectOrderListItemStateList()
        source.associate { it.customerName to it.customerType }
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
                        hasChips = inputChips.isNotEmpty(),
                        queryBase = chipsQueryBase,
                        onClearAll = clearAll,
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
                        hasChips = inputChips.isNotEmpty(),
                        queryBase = chipsQueryBase,
                        onClearAll = clearAll,
                        modifier = Modifier,
                        prefix = {
                            Row(
                                modifier = Modifier.horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(Dimens.Space.small),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                CompositionLocalProvider(
                                    LocalMinimumInteractiveComponentSize provides Dp.Unspecified // Or set a specific smaller size
                                ) {
                                    inputChips.forEach { name ->
                                        InputChip(
                                            selected = false,
                                            leadingIcon = {
                                                when (customerTypeByName[name]) {
                                                    CustomerType.B2B -> B2BIcon(
                                                        modifier = Modifier.size(InputChipDefaults.IconSize)
                                                    )

                                                    CustomerType.B2C -> B2CIcon(
                                                        modifier = Modifier.size(InputChipDefaults.IconSize)
                                                    )

                                                    null -> {}
                                                }
                                            },
                                            // modifier = Modifier.
                                            onClick = { },
                                            label = {
                                                Text(
                                                    name,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis
                                                )
                                            },
                                            trailingIcon = {
                                                IconButton(
                                                    onClick = { inputChips.remove(name) },
                                                    modifier = Modifier.size(InputChipDefaults.IconSize)
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.Close,
                                                        contentDescription = "Remove"
                                                    )
                                                }

                                            }
                                        )
                                    }
                                }
                            }
                        }
                    )
                }
            ) {
                // Local controllers from dialog window scope for reliable IME control
                val overlayFocusManager = LocalFocusManager.current
                val overlayKeyboardController = LocalSoftwareKeyboardController.current

                val customerSuggestions = remember(customerTypeByName) {
                    customerTypeByName.keys.toList()
                }

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

                    // Customer suggestion chips from mock data (based on orders.json)
                    if (customerSuggestions.size > 1) {
                        // Header
                        item(span = { GridItemSpan(maxLineSpan) }) {
                            HeaderSmall(text = "Suggested")
                        }
                        // Chips row (wrapping) in a separate grid item to avoid any overlap with the header
                        item(span = { GridItemSpan(maxLineSpan) }) {
                            FlowRow(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = Dimens.Space.medium),
                                horizontalArrangement = Arrangement.spacedBy(Dimens.Space.small),
                            ) {
                                customerSuggestions.forEach { name ->
                                    SuggestionChip(
                                        modifier = Modifier.animateItem(),
                                        onClick = {
                                            if (!inputChips.contains(name)) {
                                                inputChips.add(name)
                                            }
                                            overlayKeyboardController?.hide()
                                            overlayFocusManager.clearFocus(force = true)
                                            onSearch(name)
                                        },
                                        icon = {
                                            when (customerTypeByName[name]) {
                                                CustomerType.B2B -> B2BIcon(
                                                    modifier = Modifier.size(InputChipDefaults.IconSize)
                                                )

                                                CustomerType.B2C -> B2CIcon(
                                                    modifier = Modifier.size(InputChipDefaults.IconSize)
                                                )

                                                null -> {}
                                            }
                                        },
                                        label = {
                                            Text(
                                                name,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }
                                    )
                                }
                            }
                        }
                    }

                    // Recent searches section (only when there are no order results)
                    if (searchOrderItems.isEmpty() && searchResults.isNotEmpty()) {
                        item(span = { GridItemSpan(maxLineSpan) }) {
                            HeaderSmall(text = "Recent searches")
                        }
                        items(
                            items = searchResults,
                            key = { it },
                            span = { GridItemSpan(maxLineSpan) }
                        ) { result ->
                            ListItem(
                                headlineContent = { Text(result) },
                                leadingContent = {
                                    Icon(
                                        imageVector = Icons.Default.Search,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(
                                        horizontal = Dimens.Space.medium,
                                        vertical = Dimens.Space.small
                                    )
                                    .clickable {
                                        overlayKeyboardController?.hide()
                                        overlayFocusManager.clearFocus(force = true)
                                        onResultClick(result)
                                    }
                            )
                        }
                    }

                    // Results header and order items
                    if (searchOrderItems.isNotEmpty()) {
                        item(span = { GridItemSpan(maxLineSpan) }) {
                            HeaderSmall(text = "Results")
                        }
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
                                CollectOrderItem(
                                    customerName = collectOrderState.customerName,
                                    customerType = collectOrderState.customerType,
                                    invoiceNumber = collectOrderState.invoiceNumber,
                                    webOrderNumber = collectOrderState.webOrderNumber,
                                    pickedAt = collectOrderState.pickedAt,
                                    isLoading = isRefreshing,
                                    contendPadding = PaddingValues(),
                                    modifier = Modifier.padding(
                                        start = Dimens.Space.medium,
                                        top = Dimens.Space.medium,
                                        bottom = Dimens.Space.small,
                                        end = if (isMultiSelectionEnabled) 0.dp else Dimens.Space.medium
                                    ),
                                )
                            }
                        }
                    } else if (searchResults.isEmpty() && query.isNotEmpty()) {
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
    hasChips: Boolean = false,
    queryBase: String = "",
    onClearAll: (() -> Unit)? = null,
    colors: TextFieldColors = SearchBarDefaults.inputFieldColors(
        focusedContainerColor = Color.Transparent,
        unfocusedContainerColor = Color.Transparent,
        disabledContainerColor = Color.Transparent,
    ),
    prefix: @Composable (() -> Unit)? = null,
) {
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    // Use TextFieldState-based InputField to enable prefix/suffix slots
    val initialFromQuery = remember(query, hasChips, queryBase) {
        if (queryBase.isNotEmpty() && query.startsWith(queryBase)) {
            query.removePrefix(queryBase).trimStart()
        } else {
            query
        }
    }
    val textState = remember { TextFieldState(initialFromQuery) }

    // Propagate user edits combined with chip-based queryBase
    LaunchedEffect(textState, queryBase) {
        snapshotFlow { textState.text.toString() }.collect { typed ->
            val combined = when {
                queryBase.isNotBlank() && typed.isNotBlank() -> "$queryBase $typed"
                queryBase.isNotBlank() -> queryBase
                else -> typed
            }
            if (combined != query) onQueryChange(combined)
        }
    }

    // Ensure external changes to queryBase update the composed query
    LaunchedEffect(queryBase) {
        val typed = textState.text.toString()
        val combined = when {
            queryBase.isNotBlank() && typed.isNotBlank() -> "$queryBase $typed"
            queryBase.isNotBlank() -> queryBase
            else -> typed
        }
        if (combined != query) onQueryChange(combined)
    }

    SearchBarDefaults.InputField(
        state = textState,
        onSearch = { _ ->
            keyboardController?.hide()
            focusManager.clearFocus(force = true)
            val typed = textState.text.toString()
            val effective = when {
                queryBase.isNotBlank() && typed.isNotBlank() -> "$queryBase $typed"
                queryBase.isNotBlank() -> queryBase
                else -> typed
            }
            onSearch(effective)
        },
        expanded = isExpanded,
        onExpandedChange = onExpandedChange,
        modifier = modifier,
        placeholder = {
            if (!hasChips) {
                Text(
                    text = placeholderText,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
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
        trailingIcon = if (!isExpanded || (query.isEmpty() && !hasChips)) null else {
            {
                IconButton(onClick = {
                    // Clear both chips (if handler provided) and any typed text
                    textState.edit { replace(0, length, "") }
                    onClearAll?.invoke() ?: onClearClick()
                }) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "Clear search",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        },
        prefix = prefix,
        suffix = null,
        colors = colors,
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
