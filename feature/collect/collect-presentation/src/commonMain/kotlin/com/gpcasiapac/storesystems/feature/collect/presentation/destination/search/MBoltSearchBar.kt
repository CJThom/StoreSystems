package com.gpcasiapac.storesystems.feature.collect.presentation.destination.search

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.material.icons.automirrored.outlined.ReceiptLong
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material.icons.outlined.Receipt
import androidx.compose.material.icons.outlined.ReceiptLong
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.gpcasiapac.storesystems.feature.collect.domain.model.CustomerNameSuggestion
import com.gpcasiapac.storesystems.feature.collect.domain.model.CustomerType
import com.gpcasiapac.storesystems.feature.collect.domain.model.InvoiceNumberSuggestion
import com.gpcasiapac.storesystems.feature.collect.domain.model.PhoneSuggestion
import com.gpcasiapac.storesystems.feature.collect.domain.model.SalesOrderNumberSuggestion
import com.gpcasiapac.storesystems.feature.collect.domain.model.SearchSuggestion
import com.gpcasiapac.storesystems.feature.collect.domain.model.WebOrderNumberSuggestion
import com.gpcasiapac.storesystems.feature.collect.presentation.component.StickyBarDefaults
import com.gpcasiapac.storesystems.feature.collect.presentation.components.HeaderSmall
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderlist.component.CollectOrderItem
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderlist.component.OrderListToolbar
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderlist.model.CollectOrderListItemState
import com.gpcasiapac.storesystems.foundation.component.CheckboxCard
import com.gpcasiapac.storesystems.foundation.component.icon.B2BIcon
import com.gpcasiapac.storesystems.foundation.component.icon.B2CIcon
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
    onSuggestionClicked: (SearchSuggestion) -> Unit = {},
    onClearClick: () -> Unit,
    recentSearches: List<String>,
    // Suggestions to render as chips (full typed model)
    suggestions: List<SearchSuggestion>,
    // Hoisted search UI state
    selectedChips: List<SearchSuggestion> = emptyList(),
    typedSuffix: String = "",
    onTypedSuffixChange: (String) -> Unit = {},
    onRemoveChip: (SearchSuggestion) -> Unit = {},
    // New: full search results as order items for the expanded grid
    searchOrderItems: List<CollectOrderListItemState> = emptyList(),
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

    Box(modifier = Modifier) {
        CollapsedSearchBarSection(
            modifier = modifier,
            contentPadding = collapsedContentPadding,
            collapsedShape = collapsedShape,
            collapsedColors = collapsedColors,
            collapsedBorder = collapsedBorder,
            searchBarState = searchBarState,
            totalQuery = query,
            typedSuffix = typedSuffix,
            onTypedSuffixChange = onTypedSuffixChange,
            onSearch = onSearch,
            onExpandedChange = onExpandedChange,
            onBackPressed = onBackPressed,
            onClearClick = onClearClick,
            hasChips = selectedChips.isNotEmpty(),
            chipsPrefix = {
                ChipsPrefixRow(
                    chips = selectedChips,
                    onRemoveChip = onRemoveChip
                )
            },
            placeholderText = placeholderText,
        )

        ExpandedSearchSection(
            searchBarState = searchBarState,
            collapsedShape = collapsedShape,
            totalQuery = query,
            typedSuffix = typedSuffix,
            onTypedSuffixChange = onTypedSuffixChange,
            onSearch = onSearch,
            onExpandedChange = onExpandedChange,
            onBackPressed = onBackPressed,
            onClearClick = onClearClick,
            selectedChips = selectedChips,
            chipsPrefix = {
                ChipsPrefixRow(
                    chips = selectedChips,
                    onRemoveChip = onRemoveChip
                )
            },
            suggestions = suggestions,
            onSuggestionClicked = onSuggestionClicked,
            searchOrderItems = searchOrderItems,
            recentSearches = recentSearches,
            isMultiSelectionEnabled = isMultiSelectionEnabled,
            selectedOrderIdList = selectedOrderIdList,
            isSelectAllChecked = isSelectAllChecked,
            isRefreshing = isRefreshing,
            onOpenOrder = onOpenOrder,
            onCheckedChange = onCheckedChange,
            onSelectAllToggle = onSelectAllToggle,
            onCancelSelection = onCancelSelection,
            onEnterSelectionMode = onEnterSelectionMode,
            onSelectClick = onSelectClick,
            onResultClick = onResultClick,
            placeholderText = placeholderText,
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CollapsedSearchBarSection(
    modifier: Modifier,
    contentPadding: PaddingValues,
    collapsedShape: Shape,
    collapsedColors: SearchBarColors,
    collapsedBorder: BorderStroke?,
    searchBarState: SearchBarState,
    totalQuery: String,
    typedSuffix: String,
    onTypedSuffixChange: (String) -> Unit,
    onSearch: (String) -> Unit,
    onExpandedChange: (Boolean) -> Unit,
    onBackPressed: () -> Unit,
    onClearClick: () -> Unit,
    hasChips: Boolean,
    chipsPrefix: @Composable () -> Unit,
    placeholderText: String,
) {
    Surface(
        modifier = modifier.padding(contentPadding),
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
                    totalQuery = totalQuery,
                    typedSuffix = typedSuffix,
                    onTypedSuffixChange = onTypedSuffixChange,
                    isExpanded = searchBarState.currentValue == SearchBarValue.Expanded,
                    onExpandedChange = onExpandedChange,
                    placeholderText = placeholderText,
                    onSearch = onSearch,
                    onBackPressed = onBackPressed,
                    onClearClick = onClearClick,
                    hasChips = hasChips,
                    prefix = chipsPrefix,
                )
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ExpandedSearchSection(
    searchBarState: SearchBarState,
    collapsedShape: Shape,
    totalQuery: String,
    typedSuffix: String,
    onTypedSuffixChange: (String) -> Unit,
    onSearch: (String) -> Unit,
    onExpandedChange: (Boolean) -> Unit,
    onBackPressed: () -> Unit,
    onClearClick: () -> Unit,
    selectedChips: List<SearchSuggestion>,
    chipsPrefix: @Composable () -> Unit,
    suggestions: List<SearchSuggestion>,
    onSuggestionClicked: (SearchSuggestion) -> Unit,
    searchOrderItems: List<CollectOrderListItemState>,
    recentSearches: List<String>,
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
    onResultClick: (String) -> Unit,
    placeholderText: String,
) {
    Surface {
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
                    totalQuery = totalQuery,
                    typedSuffix = typedSuffix,
                    onTypedSuffixChange = onTypedSuffixChange,
                    isExpanded = searchBarState.currentValue == SearchBarValue.Expanded,
                    onExpandedChange = onExpandedChange,
                    placeholderText = placeholderText,
                    onSearch = onSearch,
                    onBackPressed = onBackPressed,
                    onClearClick = onClearClick,
                    hasChips = selectedChips.isNotEmpty(),
                    modifier = Modifier,
                    prefix = chipsPrefix,
                )
            }
        ) {
            val overlayFocusManager = LocalFocusManager.current
            val overlayKeyboardController = LocalSoftwareKeyboardController.current

            val lazyGridState = rememberLazyGridState()
            val stickyHeaderScrollBehavior = StickyBarDefaults.liftOnScrollBehavior(
                lazyGridState = lazyGridState,
                stickyHeaderIndex = 0
            )

            Column {
                AnimatedVisibility(
                    visible = searchOrderItems.isNotEmpty(),
                    enter = expandVertically(animationSpec = tween(250)) + fadeIn(),
                    exit = shrinkVertically(animationSpec = tween(200)) + fadeOut(),
                    label = "SearchToolbarVisibility"
                ) {
                    OrderListToolbar(
                        modifier = Modifier,
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

                LazyVerticalGrid(
                    state = lazyGridState,
                    columns = GridCells.Adaptive(Dimens.Adaptive.gridItemWidth),
                ) {

                    // Suggestions chips
                    if (suggestions.size > 1) {
                        // Header
                        item(span = { GridItemSpan(maxLineSpan) }) {
                            HeaderSmall(text = "Suggested")
                        }
                        // Chips row
                        item(span = { GridItemSpan(maxLineSpan) }) {
                            SuggestionChipsRow(
                                suggestions = suggestions,
                                onSuggestionClick = { s ->
                                    overlayKeyboardController?.hide()
                                    overlayFocusManager.clearFocus(force = true)
                                    onSuggestionClicked(s)
                                }
                            )
                        }
                    }

                    // Recent searches section (only when there are no order results)
                    if (searchOrderItems.isEmpty() && recentSearches.isNotEmpty()) {
                        item(span = { GridItemSpan(maxLineSpan) }) {
                            HeaderSmall(text = "Recent searches")
                        }
                        items(
                            items = recentSearches,
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
                                    .animateItem(),
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
                    } else if (recentSearches.isEmpty() && totalQuery.isNotEmpty()) {
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
    totalQuery: String,
    typedSuffix: String,
    onTypedSuffixChange: (String) -> Unit,
    isExpanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    placeholderText: String,
    onSearch: (String) -> Unit,
    onBackPressed: () -> Unit,
    onClearClick: () -> Unit,
    modifier: Modifier = Modifier,
    hasChips: Boolean = false,
    colors: TextFieldColors = SearchBarDefaults.inputFieldColors(
        focusedContainerColor = Color.Transparent,
        unfocusedContainerColor = Color.Transparent,
        disabledContainerColor = Color.Transparent,
    ),
    prefix: @Composable (() -> Unit)? = null,
) {
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    // Keep a local text state for typed suffix so we can use prefix slot
    val textState = remember { TextFieldState(typedSuffix) }

    // Sync local state when external typedSuffix changes
    LaunchedEffect(typedSuffix) {
        val current = textState.text.toString()
        if (current != typedSuffix) {
            textState.edit { replace(0, length, typedSuffix) }
        }
    }

    // Propagate user edits upstream
    LaunchedEffect(textState) {
        snapshotFlow { textState.text.toString() }
            .collect { newText: String ->
                if (newText != typedSuffix) onTypedSuffixChange(newText)
            }
    }

    SearchBarDefaults.InputField(
        state = textState,
        onSearch = { _ ->
            keyboardController?.hide()
            focusManager.clearFocus(force = true)
            onSearch(totalQuery)
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
        trailingIcon = if (!isExpanded || (totalQuery.isEmpty() && !hasChips)) null else {
            {
                IconButton(onClick = { onClearClick() }) {
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


/**
 * Prefix row of removable InputChip(s) with B2B/B2C icons.
 * Used inside SearchBarDefaults.InputField prefix slot in the expanded search dialog.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ChipsPrefixRow(
    chips: List<SearchSuggestion>,
    onRemoveChip: (SearchSuggestion) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(Dimens.Space.small),
        verticalAlignment = Alignment.CenterVertically
    ) {
        CompositionLocalProvider(
            LocalMinimumInteractiveComponentSize provides Dp.Unspecified
        ) {
            chips.forEach { chip ->
                InputChip(
                    selected = false,
                    modifier = Modifier.height(InputChipDefaults.Height - 5.dp),
                    leadingIcon = { ChipIcon(chip) },
                    onClick = { /* no-op for prefix chips */ },
                    label = {
                        Text(
                            text = chip.text,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    trailingIcon = {
                        IconButton(
                            onClick = { onRemoveChip(chip) },
                            modifier = Modifier.size(InputChipDefaults.IconSize)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Close,
                                contentDescription = "Remove"
                            )
                        }
                    }
                )
            }
        }
    }
}

/**
 * Wrapping row of suggestion chips with B2B/B2C icons.
 */
@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
internal fun SuggestionChipsRow(
    suggestions: List<SearchSuggestion>,
    onSuggestionClick: (SearchSuggestion) -> Unit,
    modifier: Modifier = Modifier
) {
    FlowRow(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = Dimens.Space.medium),
        horizontalArrangement = Arrangement.spacedBy(Dimens.Space.small)
    ) {
        suggestions.forEach { s ->
            SuggestionChip(
                onClick = { onSuggestionClick(s) },
                icon = { ChipIcon(s) },
                label = {
                    Text(
                        text = s.text,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ChipIcon(s: SearchSuggestion) {
    when (s) {
        is CustomerNameSuggestion -> {
            if (s.customerType == CustomerType.B2B) {
                B2BIcon(modifier = Modifier.size(InputChipDefaults.IconSize))
            } else {
                B2CIcon(modifier = Modifier.size(InputChipDefaults.IconSize))
            }
        }

        is InvoiceNumberSuggestion -> Icon(Icons.AutoMirrored.Outlined.ReceiptLong, contentDescription = null)
        is WebOrderNumberSuggestion -> Icon(Icons.Outlined.Language, contentDescription = null)
        is SalesOrderNumberSuggestion -> Icon(Icons.Outlined.Receipt, contentDescription = null)
        is PhoneSuggestion -> Icon(Icons.Outlined.Phone, contentDescription = null)
    }
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
            recentSearches = listOf(
                "Order #12345 - John Doe",
                "Order #12346 - Jane Smith",
                "Order #12347 - Bob Johnson"
            ),
            suggestions = emptyList(),
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
            recentSearches = listOf(
                "Order #12345 - John Doe",
                "Order #12346 - Jane Smith",
                "Order #12347 - Bob Johnson",
                "Order #12348 - John Williams"
            ),
            selectedChips = emptyList(),
            typedSuffix = "",
            onTypedSuffixChange = {},
            onRemoveChip = {},
            suggestions = listOf(
                CustomerNameSuggestion("John Doe", CustomerType.B2C),
                InvoiceNumberSuggestion("INV-10001"),
                WebOrderNumberSuggestion("WEB-50001")
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
        val items = CollectOrderListItemState.placeholderList(6)
        MBoltSearchBar(
            query = "Jo",
            onQueryChange = {},
            searchBarState = rememberSearchBarState(initialValue = SearchBarValue.Expanded),
            onSearch = {},
            onExpandedChange = {},
            onBackPressed = {},
            onResultClick = {},
            onClearClick = {},
            recentSearches = emptyList(),
            selectedChips = emptyList(),
            typedSuffix = "",
            onTypedSuffixChange = {},
            onRemoveChip = {},
            suggestions = emptyList(),
            searchOrderItems = items,
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
        val items = CollectOrderListItemState.placeholderList(8)
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
            recentSearches = emptyList(),
            selectedChips = emptyList(),
            typedSuffix = "",
            onTypedSuffixChange = {},
            onRemoveChip = {},
            suggestions = emptyList(),
            searchOrderItems = items,
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
        val items = CollectOrderListItemState.placeholderList(5)
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
            recentSearches = emptyList(),
            selectedChips = emptyList(),
            typedSuffix = "",
            onTypedSuffixChange = {},
            onRemoveChip = {},
            suggestions = emptyList(),
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
            recentSearches = emptyList(),
            selectedChips = emptyList(),
            typedSuffix = "",
            onTypedSuffixChange = {},
            onRemoveChip = {},
            suggestions = emptyList(),
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
        val items = CollectOrderListItemState.placeholderList(4)
        MBoltSearchBar(
            query = "Smith",
            onQueryChange = {},
            searchBarState = rememberSearchBarState(initialValue = SearchBarValue.Expanded),
            onSearch = {},
            onExpandedChange = {},
            onBackPressed = {},
            onResultClick = {},
            onClearClick = {},
            recentSearches = emptyList(),
            selectedChips = emptyList(),
            typedSuffix = "",
            onTypedSuffixChange = {},
            onRemoveChip = {},
            suggestions = emptyList(),
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
