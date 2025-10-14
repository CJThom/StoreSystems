package com.gpcasiapac.storesystems.feature.collect.presentation.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderlist.OrderListScreenContract
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderlist.component.MultiSelectBottomBar
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
            Column(modifier = Modifier.fillMaxHeight(),verticalArrangement = Arrangement.SpaceBetween) {
                // Search results content
                Box {
                    if (searchResults.isEmpty() && query.isNotEmpty()) {
                        // Empty state
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
                    } else {
                        searchResults.forEach { result ->
                            ListItem(
                                headlineContent = { Text(result) },
                                modifier = Modifier
                                    .clickable { onResultClick(result) }
                                    .fillMaxWidth()
                                    .padding(
                                        horizontal = Dimens.Space.medium,
                                        vertical = Dimens.Space.extraSmall
                                    )
                            )
                        }
                    }
                }
                MultiSelectBottomBar(
                    selectedCount = 2,
                    isSelectAllChecked = false,
                    onSelectAllToggle = { checked ->
                       // onEventSent(OrderListScreenContract.Event.SelectAll(checked))
                    },
                    onCancelClick = {
                        //onEventSent(OrderListScreenContract.Event.CancelSelection)
                    },
                    onSelectClick = {
                       // onEventSent(OrderListScreenContract.Event.ConfirmSelection)
                    }
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
            placeholderText = "Search by Order #, Name, Phone"
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(name = "Search Bar - Expanded with Results")
@Composable
fun MBoltSearchBarExpandedPreview() {
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
            placeholderText = "Search by Order #, Name, Phone"
        )
    }
}
