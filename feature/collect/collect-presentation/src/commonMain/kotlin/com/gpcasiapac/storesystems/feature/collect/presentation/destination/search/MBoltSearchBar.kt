package com.gpcasiapac.storesystems.feature.collect.presentation.destination.search

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.ReceiptLong
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material.icons.outlined.Receipt
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.mutableStateOf
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
import co.touchlab.kermit.Logger
import com.gpcasiapac.storesystems.feature.collect.api.model.InvoiceNumber
import com.gpcasiapac.storesystems.feature.collect.domain.model.CustomerNameSuggestion
import com.gpcasiapac.storesystems.feature.collect.domain.model.CustomerType
import com.gpcasiapac.storesystems.feature.collect.domain.model.InvoiceNumberSuggestion
import com.gpcasiapac.storesystems.feature.collect.domain.model.PhoneSuggestion
import com.gpcasiapac.storesystems.feature.collect.domain.model.SalesOrderNumberSuggestion
import com.gpcasiapac.storesystems.feature.collect.domain.model.SearchSuggestion
import com.gpcasiapac.storesystems.feature.collect.domain.model.WebOrderNumberSuggestion
import com.gpcasiapac.storesystems.feature.collect.presentation.component.StickyBarDefaults
import com.gpcasiapac.storesystems.foundation.component.HeaderSmall
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderlist.component.CollectOrderItem
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderlist.component.OrderListToolbar
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderlist.model.CollectOrderListItemState
import com.gpcasiapac.storesystems.feature.collect.presentation.selection.SelectionContract
import com.gpcasiapac.storesystems.foundation.component.CheckboxCard
import com.gpcasiapac.storesystems.foundation.component.icon.B2BIcon
import com.gpcasiapac.storesystems.foundation.component.icon.B2CIcon
import com.gpcasiapac.storesystems.foundation.design_system.Dimens
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchDestination(
    searchViewModel: SearchViewModel = koinViewModel(),
    placeholderText: String = "Search by Order #, Name, Phone",
    collapsedContentPadding: PaddingValues = PaddingValues(Dimens.Space.medium),
    collapsedShape: Shape = MaterialTheme.shapes.small,
    collapsedColors: SearchBarColors = SearchBarDefaults.colors(
        containerColor = MaterialTheme.colorScheme.surface
    ),
    collapsedBorder: BorderStroke? = null,
) {

    val log = Logger.withTag("SearchDestination")

    SearchComponent(
        state = searchViewModel.viewState.collectAsState().value,
        onEventSent = { event -> searchViewModel.setEvent(event) },
        effectFlow = searchViewModel.effect,
        // onOutcome = onOutcome,
        placeholderText = placeholderText,
        collapsedContentPadding = collapsedContentPadding,
        collapsedShape = collapsedShape,
        collapsedColors = collapsedColors,
        collapsedBorder = collapsedBorder
    )

}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchComponent(
    state: SearchContract.State,
    onEventSent: (SearchContract.Event) -> Unit,
    effectFlow: Flow<SearchContract.Effect>?,
    // New styling/customization parameters
    modifier: Modifier = Modifier,
    placeholderText: String = "Search by Order #, Name, Phone",
    collapsedContentPadding: PaddingValues = PaddingValues(
        horizontal = Dimens.Space.medium,
        vertical = Dimens.Space.small
    ),
    collapsedShape: Shape = MaterialTheme.shapes.small,
    collapsedColors: SearchBarColors = SearchBarDefaults.colors(
        containerColor = MaterialTheme.colorScheme.surface
    ),
    collapsedBorder: BorderStroke? = null,
) {

    val log = Logger.withTag("SearchComponent")

    val coroutineScope = rememberCoroutineScope()
    val searchBarState = rememberSearchBarState(initialValue = SearchBarValue.Collapsed)
    val scrollState = rememberScrollState()

    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    val queryState = rememberTextFieldState(state.query)
    LaunchedEffect(queryState) {
        snapshotFlow { queryState.text.toString() }.collectLatest {
            onEventSent(SearchContract.Event.OnQueryChanged(it))
        }
    }

    LaunchedEffect(effectFlow) {
        effectFlow?.collectLatest { effect ->
            when (effect) {
                is SearchContract.Effect.ShowMultiSelectConfirmDialog -> {
                    //searchConfirmDialogSpec.value = effect
                }

                is SearchContract.Effect.ExpandSearchBar -> {
                    coroutineScope.launch {
                        searchBarState.animateToExpanded()
                    }
                }

                is SearchContract.Effect.CollapseSearchBar -> {
                    coroutineScope.launch {
                        searchBarState.animateToCollapsed()
                    }
                }

                is SearchContract.Effect.ClearQueryField -> {
                    queryState.clearText()
                }

                is SearchContract.Effect.SetQueryField -> {
                    queryState.setTextAndPlaceCursorAtEnd(effect.text)
                }

                is SearchContract.Effect.FocusQueryField -> {
                    keyboardController?.show()
                    // SearchBar manages focus internally; showing keyboard is often enough
                }

                is SearchContract.Effect.ScrollToEnd -> {
                    coroutineScope.launch {
                        log.d { "Scroll to end : ${scrollState.value} to ${scrollState.maxValue}" }
//                        if (scrollState.maxValue > 0) {
//                            scrollState.scrollTo(scrollState.maxValue)
//                        }
                        scrollState.animateScrollTo(scrollState.maxValue)
                    }
                }
            }
        }
    }

    // Keep search bar animation in sync with SearchViewModel
    LaunchedEffect(searchBarState.currentValue) {
        log.d { "searchBarState currentValue = ${searchBarState.currentValue}" }
        onEventSent(SearchContract.Event.OnExpandedChanged(searchBarState.currentValue == SearchBarValue.Expanded))
    }

    // Build the input field (prefix chips + text field)
    val inputField = @Composable {
        SearchBarInputField(
            textFieldState = queryState,
            searchBarState = searchBarState,
            scrollState = scrollState,
            isExpanded = searchBarState.currentValue == SearchBarValue.Expanded,
            placeholderText = placeholderText,
            onSearch = { query ->
                onEventSent(SearchContract.Event.OnSearchClicked)
            },
            onBackPressed = {
                onEventSent(SearchContract.Event.SearchBarBackPressed)
            },
            onClearClick = {
                onEventSent(SearchContract.Event.ClearSearch)
            },
            hasChips = state.selectedSuggestionList.isNotEmpty(),
            selectedChipList = state.selectedSuggestionList,
            onRemoveChip = { s -> onEventSent(SearchContract.Event.RemoveChip(s)) }
        )
    }

    Box(modifier = modifier) {
        CollapsedSearchBarSection(
            modifier = Modifier,
            contentPadding = collapsedContentPadding,
            collapsedShape = collapsedShape,
            collapsedColors = collapsedColors,
            collapsedBorder = collapsedBorder,
            searchBarState = searchBarState,
            inputField = inputField
        )
        if (searchBarState.currentValue == SearchBarValue.Expanded) {
            ExpandedSearchSection(
                searchBarState = searchBarState,
                collapsedShape = collapsedShape,
                suggestions = state.searchSuggestionList,
                onSuggestionClicked = { s ->
                    onEventSent(
                        SearchContract.Event.SearchSuggestionClicked(
                            s
                        )
                    )
                },
                searchOrderItems = state.searchOrderItemList,
                recentSearches = emptyList(),
                isMultiSelectionEnabled = state.selection.isEnabled,
                selectedOrderIdList = state.selection.selected,
                isSelectAllChecked = state.selection.isAllSelected,
                isRefreshing = false, // TODO
                onOpenOrder = { /* onEventSent(OrderFulfilmentScreenContract.Event.OrderClicked(it)) */ },
                onCheckedChange = { orderId, checked ->
                    onEventSent(
                        SearchContract.Event.Selection(
                            SelectionContract.Event.SetItemChecked(orderId, checked)
                        )
                    )
                },
                onSelectAllToggle = { checked ->
                    onEventSent(
                        SearchContract.Event.Selection(
                            SelectionContract.Event.SelectAll(checked)
                        )
                    )
                },
                onCancelSelection = {
                    onEventSent(
                        SearchContract.Event.Selection(SelectionContract.Event.Cancel)
                    )
                },
                onEnterSelectionMode = {
                    onEventSent(
                        SearchContract.Event.Selection(SelectionContract.Event.ToggleMode(true))
                    )
                },
                onSelectClick = {
                    // onEventSent(OrderFulfilmentScreenContract.Event.ConfirmSearchSelection)
                },
                inputField = inputField
            )
        }
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
    inputField: @Composable () -> Unit
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
            inputField = inputField
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ExpandedSearchSection(
    searchBarState: SearchBarState,
    collapsedShape: Shape,
    suggestions: List<SearchSuggestion>,
    onSuggestionClicked: (SearchSuggestion) -> Unit,
    searchOrderItems: List<CollectOrderListItemState>,
    recentSearches: List<String>,
    isMultiSelectionEnabled: Boolean,
    selectedOrderIdList: Set<InvoiceNumber>,
    isSelectAllChecked: Boolean,
    isRefreshing: Boolean,
    onOpenOrder: (InvoiceNumber) -> Unit,
    onCheckedChange: (InvoiceNumber, Boolean) -> Unit,
    onSelectAllToggle: (Boolean) -> Unit,
    onCancelSelection: () -> Unit,
    onEnterSelectionMode: () -> Unit,
    onSelectClick: () -> Unit,
    inputField: @Composable () -> Unit,
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
            inputField = inputField
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
                    modifier = Modifier.fillMaxSize()
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
                                        // onResultClick(result)
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
                            key = { it.invoiceNumber.value }
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
                    } else if (recentSearches.isEmpty()) {// TODO: Need to check query text?
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
    textFieldState: TextFieldState,
    searchBarState: SearchBarState,
    scrollState: ScrollState,
    isExpanded: Boolean,
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
    selectedChipList: List<SearchSuggestion>,
    onRemoveChip: (SearchSuggestion) -> Unit
) {

    SearchBarDefaults.InputField(
        textFieldState = textFieldState,
        searchBarState = searchBarState,
        onSearch = { query ->
            onSearch(query)
        },
        modifier = modifier.height(56.dp),
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
        trailingIcon = if (!isExpanded || (textFieldState.text.isEmpty() && !hasChips)) null else {
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
        prefix = {
            // Reserve some space at the end for typing/caret so chips don't consume the entire row
            BoxWithConstraints() {
                val reservedForTyping = 72.dp
                val maxPrefixWidth = (maxWidth - reservedForTyping).coerceAtLeast(0.dp)
                ChipsPrefixRow(
                    chips = selectedChipList,
                    onRemoveChip = { s -> onRemoveChip(s) },
                    scrollState = scrollState,
                    modifier = Modifier.widthIn(max = maxPrefixWidth)
                )
            }
        },
      //  scrollState = scrollState,
        colors = colors,
    )
}


/**
 * Prefix row of removable InputChip(s) with B2B/B2C icons.
 * Used inside SearchBarDefaults.InputField prefix slot in the expanded search dialog.
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
internal fun ChipsPrefixRow(
    chips: List<SearchSuggestion>,
    onRemoveChip: (SearchSuggestion) -> Unit,
    modifier: Modifier = Modifier,
    scrollState: ScrollState,
) {
    // Track previous size to detect additions only
    var previousSize = remember { mutableIntStateOf(chips.size) }
    val lastChipRequester = remember { BringIntoViewRequester() }

    // When a chip is added, bring the last chip into view
    LaunchedEffect(chips.size) {
        val newSize = chips.size
        if (newSize > previousSize.intValue && newSize > 0) {
            lastChipRequester.bringIntoView()
        }
        previousSize.intValue = newSize
    }

    Row(
        modifier = modifier.horizontalScroll(scrollState),
        horizontalArrangement = Arrangement.spacedBy(Dimens.Space.small),
        verticalAlignment = Alignment.CenterVertically
    ) {
        CompositionLocalProvider(
            LocalMinimumInteractiveComponentSize provides Dp.Unspecified
        ) {
            chips.forEachIndexed { index, chip ->
                val chipModifier = if (index == chips.lastIndex) {
                    Modifier.bringIntoViewRequester(lastChipRequester)
                } else Modifier

                InputChip(
                    selected = false,
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
                    },
                    modifier = chipModifier
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

        is InvoiceNumberSuggestion -> Icon(
            Icons.AutoMirrored.Outlined.ReceiptLong,
            contentDescription = null
        )

        is WebOrderNumberSuggestion -> Icon(Icons.Outlined.Language, contentDescription = null)
        is SalesOrderNumberSuggestion -> Icon(Icons.Outlined.Receipt, contentDescription = null)
        is PhoneSuggestion -> Icon(Icons.Outlined.Phone, contentDescription = null)
    }
}

//
//@Deprecated("MBoltSearchBar is deprecated. Use SearchComponent which directly composes SearchBarInputField, CollapsedSearchBarSection, and ExpandedSearchSection.")
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun MBoltSearchBar(
//    query: String,
//    onQueryChange: (String) -> Unit,
//    searchBarState: SearchBarState,
//    onSearch: (String) -> Unit,
//    onExpandedChange: (Boolean) -> Unit,
//    onBackPressed: () -> Unit,
//    onResultClick: (InvoiceNumber) -> Unit,
//    onSuggestionClicked: (SearchSuggestion) -> Unit = {},
//    onClearClick: () -> Unit,
//    recentSearches: List<String>,
//    // Suggestions to render as chips (full typed model)
//    suggestions: List<SearchSuggestion>,
//    // Hoisted search UI state
//    selectedChips: List<SearchSuggestion> = emptyList(),
//    typedSuffix: String = "",
//    onTypedSuffixChange: (String) -> Unit = {},
//    onRemoveChip: (SearchSuggestion) -> Unit = {},
//    // New: full search results as order items for the expanded grid
//    searchOrderItems: List<CollectOrderListItemState> = emptyList(),
//    isMultiSelectionEnabled: Boolean,
//    selectedOrderIdList: Set<InvoiceNumber>,
//    isSelectAllChecked: Boolean,
//    isRefreshing: Boolean,
//    onOpenInvoice: (InvoiceNumber) -> Unit,
//    onCheckedChange: (InvoiceNumber, Boolean) -> Unit,
//    onSelectAllToggle: (Boolean) -> Unit,
//    onCancelSelection: () -> Unit,
//    onEnterSelectionMode: () -> Unit,
//    onSelectClick: () -> Unit,
//    modifier: Modifier = Modifier,
//    placeholderText: String = "Search...",
//    // Style overrides for collapsed bar (optional)
//    collapsedContentPadding: PaddingValues = PaddingValues(Dimens.Space.medium),
//    collapsedShape: Shape = MaterialTheme.shapes.small,
//    collapsedColors: SearchBarColors = SearchBarDefaults.colors(),
//    collapsedBorder: BorderStroke? = null,
//) {
//
//    val inputField = @Composable {
//        SearchBarInputField(
//            //totalQuery = query,
//            typedSuffix = typedSuffix,
//            onTypedSuffixChange = onTypedSuffixChange,
//            isExpanded = searchBarState.currentValue == SearchBarValue.Expanded,
//            onExpandedChange = onExpandedChange,
//            placeholderText = placeholderText,
//            onSearch = onSearch,
//            onBackPressed = onBackPressed,
//            onClearClick = onClearClick,
//            hasChips = selectedChips.isNotEmpty(),
//            prefix = {
//                ChipsPrefixRow(
//                    chips = selectedChips,
//                    onRemoveChip = onRemoveChip
//                )
//            },
//        )
//    }
//
//    Box(modifier = Modifier) {
//        CollapsedSearchBarSection(
//            modifier = modifier,
//            contentPadding = collapsedContentPadding,
//            collapsedShape = collapsedShape,
//            collapsedColors = collapsedColors,
//            collapsedBorder = collapsedBorder,
//            searchBarState = searchBarState,
//            inputField = inputField
//        )
//
//        if (searchBarState.currentValue == SearchBarValue.Expanded) {
//            ExpandedSearchSection(
//                searchBarState = searchBarState,
//                collapsedShape = collapsedShape,
//                totalQuery = query,
//                suggestions = suggestions,
//                onSuggestionClicked = onSuggestionClicked,
//                searchOrderItems = searchOrderItems,
//                recentSearches = recentSearches,
//                isMultiSelectionEnabled = isMultiSelectionEnabled,
//                selectedOrderIdList = selectedOrderIdList,
//                isSelectAllChecked = isSelectAllChecked,
//                isRefreshing = isRefreshing,
//                onOpenOrder = onOpenInvoice,
//                onCheckedChange = onCheckedChange,
//                onSelectAllToggle = onSelectAllToggle,
//                onCancelSelection = onCancelSelection,
//                onEnterSelectionMode = onEnterSelectionMode,
//                onSelectClick = onSelectClick,
//                inputField = inputField
//            )
//        }
//    }
//}
//
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Preview(name = "Search Bar - Collapsed")
//@Composable
//fun MBoltSearchBarCollapsedPreview() {
//    GPCTheme {
//        MBoltSearchBar(
//            query = "",
//            onQueryChange = {},
//            searchBarState = rememberSearchBarState(initialValue = SearchBarValue.Collapsed),
//            onSearch = {},
//            onExpandedChange = {},
//            onBackPressed = {},
//            onResultClick = {},
//            onClearClick = {},
//            recentSearches = listOf(
//                "Order #12345 - John Doe",
//                "Order #12346 - Jane Smith",
//                "Order #12347 - Bob Johnson"
//            ),
//            suggestions = emptyList(),
//            searchOrderItems = emptyList(),
//            isMultiSelectionEnabled = false,
//            selectedOrderIdList = emptySet(),
//            isSelectAllChecked = false,
//            isRefreshing = false,
//            onOpenInvoice = {},
//            onCheckedChange = { _, _ -> },
//            onEnterSelectionMode = {},
//            onSelectAllToggle = {},
//            onCancelSelection = {},
//            onSelectClick = {},
//            placeholderText = "Search by Order #, Name, Phone",
//        )
//    }
//}
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Preview(name = "Search Bar - Expanded with Suggestions")
//@Composable
//fun MBoltSearchBarExpandedSuggestionsPreview() {
//    GPCTheme {
//        MBoltSearchBar(
//            query = "John",
//            onQueryChange = {},
//            searchBarState = rememberSearchBarState(initialValue = SearchBarValue.Expanded),
//            onSearch = {},
//            onExpandedChange = {},
//            onBackPressed = {},
//            onResultClick = {},
//            onClearClick = {},
//            recentSearches = listOf(
//                "Order #12345 - John Doe",
//                "Order #12346 - Jane Smith",
//                "Order #12347 - Bob Johnson",
//                "Order #12348 - John Williams"
//            ),
//            selectedChips = emptyList(),
//            typedSuffix = "",
//            onTypedSuffixChange = {},
//            onRemoveChip = {},
//            suggestions = listOf(
//                CustomerNameSuggestion("John Doe", CustomerType.B2C),
//                InvoiceNumberSuggestion("INV-10001"),
//                WebOrderNumberSuggestion("WEB-50001")
//            ),
//            searchOrderItems = emptyList(),
//            isMultiSelectionEnabled = false,
//            selectedOrderIdList = emptySet(),
//            isSelectAllChecked = false,
//            isRefreshing = false,
//            onOpenInvoice = {},
//            onCheckedChange = { _, _ -> },
//            onEnterSelectionMode = {},
//            onSelectAllToggle = {},
//            onCancelSelection = {},
//            onSelectClick = {},
//            placeholderText = "Search by Order #, Name, Phone"
//        )
//    }
//}
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Preview(name = "Search Bar - Expanded with Orders Grid")
//@Composable
//fun MBoltSearchBarExpandedOrdersPreview() {
//    GPCTheme {
//        val items = CollectOrderListItemState.placeholderList(6)
//        MBoltSearchBar(
//            query = "Jo",
//            onQueryChange = {},
//            searchBarState = rememberSearchBarState(initialValue = SearchBarValue.Expanded),
//            onSearch = {},
//            onExpandedChange = {},
//            onBackPressed = {},
//            onResultClick = {},
//            onClearClick = {},
//            recentSearches = emptyList(),
//            selectedChips = emptyList(),
//            typedSuffix = "",
//            onTypedSuffixChange = {},
//            onRemoveChip = {},
//            suggestions = emptyList(),
//            searchOrderItems = items,
//            isMultiSelectionEnabled = false,
//            selectedOrderIdList = emptySet(),
//            isSelectAllChecked = false,
//            isRefreshing = false,
//            onOpenInvoice = {},
//            onCheckedChange = { _, _ -> },
//            onEnterSelectionMode = {},
//            onSelectAllToggle = {},
//            onCancelSelection = {},
//            onSelectClick = {},
//            placeholderText = "Search by Order #, Name, Phone"
//        )
//    }
//}
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Preview(name = "Search Bar - Expanded Multi-Select")
//@Composable
//fun MBoltSearchBarExpandedMultiSelectPreview() {
//    GPCTheme {
//        val items = CollectOrderListItemState.placeholderList(8)
//        val selected = setOf(items[0].invoiceNumber, items[2].invoiceNumber)
//        MBoltSearchBar(
//            query = "Order",
//            onQueryChange = {},
//            searchBarState = rememberSearchBarState(initialValue = SearchBarValue.Expanded),
//            onSearch = {},
//            onExpandedChange = {},
//            onBackPressed = {},
//            onResultClick = {},
//            onClearClick = {},
//            recentSearches = emptyList(),
//            selectedChips = emptyList(),
//            typedSuffix = "",
//            onTypedSuffixChange = {},
//            onRemoveChip = {},
//            suggestions = emptyList(),
//            searchOrderItems = items,
//            isMultiSelectionEnabled = true,
//            selectedOrderIdList = selected,
//            isSelectAllChecked = false,
//            isRefreshing = false,
//            onOpenInvoice = {},
//            onCheckedChange = { _, _ -> },
//            onEnterSelectionMode = {},
//            onSelectAllToggle = {},
//            onCancelSelection = {},
//            onSelectClick = {},
//            placeholderText = "Search by Order #, Name, Phone"
//        )
//    }
//}
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Preview(name = "Search Bar - Expanded Select All")
//@Composable
//fun MBoltSearchBarExpandedSelectAllPreview() {
//    GPCTheme {
//        val items = CollectOrderListItemState.placeholderList(5)
//        val allIds = items.map { it.invoiceNumber }.toSet()
//        MBoltSearchBar(
//            query = "",
//            onQueryChange = {},
//            searchBarState = rememberSearchBarState(initialValue = SearchBarValue.Expanded),
//            onSearch = {},
//            onExpandedChange = {},
//            onBackPressed = {},
//            onResultClick = {},
//            onClearClick = {},
//            recentSearches = emptyList(),
//            selectedChips = emptyList(),
//            typedSuffix = "",
//            onTypedSuffixChange = {},
//            onRemoveChip = {},
//            suggestions = emptyList(),
//            searchOrderItems = items,
//            isMultiSelectionEnabled = true,
//            selectedOrderIdList = allIds,
//            isSelectAllChecked = true,
//            isRefreshing = false,
//            onOpenInvoice = {},
//            onCheckedChange = { _, _ -> },
//            onEnterSelectionMode = {},
//            onSelectAllToggle = {},
//            onCancelSelection = {},
//            onSelectClick = {},
//            placeholderText = "Search by Order #, Name, Phone"
//        )
//    }
//}
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Preview(name = "Search Bar - Expanded No Results")
//@Composable
//fun MBoltSearchBarExpandedNoResultsPreview() {
//    GPCTheme {
//        MBoltSearchBar(
//            query = "xyz",
//            onQueryChange = {},
//            searchBarState = rememberSearchBarState(initialValue = SearchBarValue.Expanded),
//            onSearch = {},
//            onExpandedChange = {},
//            onBackPressed = {},
//            onResultClick = {},
//            onClearClick = {},
//            recentSearches = emptyList(),
//            selectedChips = emptyList(),
//            typedSuffix = "",
//            onTypedSuffixChange = {},
//            onRemoveChip = {},
//            suggestions = emptyList(),
//            searchOrderItems = emptyList(),
//            isMultiSelectionEnabled = false,
//            selectedOrderIdList = emptySet(),
//            isSelectAllChecked = false,
//            isRefreshing = false,
//            onOpenInvoice = {},
//            onCheckedChange = { _, _ -> },
//            onEnterSelectionMode = {},
//            onSelectAllToggle = {},
//            onCancelSelection = {},
//            onSelectClick = {},
//            placeholderText = "Search by Order #, Name, Phone"
//        )
//    }
//}
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Preview(name = "Search Bar - Expanded Refreshing")
//@Composable
//fun MBoltSearchBarExpandedRefreshingPreview() {
//    GPCTheme {
//        val items = CollectOrderListItemState.placeholderList(4)
//        MBoltSearchBar(
//            query = "Smith",
//            onQueryChange = {},
//            searchBarState = rememberSearchBarState(initialValue = SearchBarValue.Expanded),
//            onSearch = {},
//            onExpandedChange = {},
//            onBackPressed = {},
//            onResultClick = {},
//            onClearClick = {},
//            recentSearches = emptyList(),
//            selectedChips = emptyList(),
//            typedSuffix = "",
//            onTypedSuffixChange = {},
//            onRemoveChip = {},
//            suggestions = emptyList(),
//            searchOrderItems = items,
//            isMultiSelectionEnabled = false,
//            selectedOrderIdList = emptySet(),
//            isSelectAllChecked = false,
//            isRefreshing = true,
//            onOpenInvoice = {},
//            onCheckedChange = { _, _ -> },
//            onEnterSelectionMode = {},
//            onSelectAllToggle = {},
//            onCancelSelection = {},
//            onSelectClick = {},
//            placeholderText = "Search by Order #, Name, Phone"
//        )
//    }
//}


@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
private fun UnifiedSearchBarInputField(
    textFieldState: androidx.compose.foundation.text.input.TextFieldState,
    searchBarState: androidx.compose.material3.SearchBarState,
    scrollState: androidx.compose.foundation.ScrollState,
    isExpanded: Boolean,
    placeholderText: String,
    onSearch: (String) -> Unit,
    onBackPressed: () -> Unit,
    onClearClick: () -> Unit,
    modifier: Modifier = Modifier,
    colors: androidx.compose.material3.TextFieldColors = androidx.compose.material3.SearchBarDefaults.inputFieldColors(
        focusedContainerColor = Color.Transparent,
        unfocusedContainerColor = Color.Transparent,
        disabledContainerColor = Color.Transparent,
    ),
    selectedChipList: List<SearchSuggestion>,
    onRemoveChip: (SearchSuggestion) -> Unit,
    reservedForTyping: Dp = 72.dp,
) {
    val hasChips = selectedChipList.isNotEmpty()

    Surface(
        color = Color.Transparent,
        contentColor = MaterialTheme.colorScheme.onSurface,
        tonalElevation = 0.dp,
        shadowElevation = 0.dp,
        modifier = modifier.height(56.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Dimens.Space.medium),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Leading icon area (fixed)
            AnimatedContent(
                targetState = isExpanded,
                label = "leading-icon"
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

            // Scrollable content: chips + text area
            val lastChipRequester = remember { BringIntoViewRequester() }
            var previousSize = remember { mutableIntStateOf(selectedChipList.size) }
            LaunchedEffect(selectedChipList.size) {
                val newSize = selectedChipList.size
                if (newSize > previousSize.intValue && newSize > 0) {
                    lastChipRequester.bringIntoView()
                }
                previousSize.intValue = newSize
            }

            Row(
                modifier = Modifier
                    .weight(1f)
                    .horizontalScroll(scrollState),
                horizontalArrangement = Arrangement.spacedBy(Dimens.Space.small),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CompositionLocalProvider(LocalMinimumInteractiveComponentSize provides Dp.Unspecified) {
                    selectedChipList.forEachIndexed { index, chip ->
                        val chipModifier = if (index == selectedChipList.lastIndex) {
                            Modifier.bringIntoViewRequester(lastChipRequester)
                        } else Modifier

                        InputChip(
                            selected = false,
                            leadingIcon = { ChipIcon(chip) },
                            onClick = { /* no-op */ },
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
                            },
                            modifier = chipModifier
                        )
                    }
                }

                // Text input area with a minimum width to keep room for typing
                Box(modifier = Modifier.widthIn(min = reservedForTyping)) {
                    if (!hasChips && textFieldState.text.isEmpty()) {
                        Text(
                            text = placeholderText,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    // State-based text binding via classic BasicTextField
                    val textValue = remember(textFieldState) { mutableStateOf(textFieldState.text.toString()) }
                    LaunchedEffect(textFieldState.text) {
                        val t = textFieldState.text.toString()
                        if (t != textValue.value) textValue.value = t
                    }
                    androidx.compose.foundation.text.BasicTextField(
                        value = textValue.value,
                        onValueChange = { new ->
                            textValue.value = new
                            textFieldState.setTextAndPlaceCursorAtEnd(new)
                        },
                        singleLine = true,
                        textStyle = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.onSurface),
                        cursorBrush = androidx.compose.ui.graphics.SolidColor(MaterialTheme.colorScheme.primary),
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(imeAction = androidx.compose.ui.text.input.ImeAction.Search),
                        keyboardActions = androidx.compose.foundation.text.KeyboardActions(
                            onSearch = { onSearch(textValue.value) }
                        )
                    )
                }
            }

            // Trailing icon area (fixed)
            val showTrailing = isExpanded && !(textFieldState.text.isEmpty() && !hasChips)
            if (showTrailing) {
                IconButton(onClick = { onClearClick() }) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "Clear search",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
