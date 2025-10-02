package com.gpcasiapac.storesystems.feature.collect.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.icons.Icons
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
import androidx.compose.material3.SearchBarValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSearchBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.gpcasiapac.storesystems.foundation.design_system.GPCTheme
import kotlinx.coroutines.launch
import org.jetbrains.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MBoltSearchBar(
    textFieldState: TextFieldState,
    searchBarState: androidx.compose.material3.SearchBarState,
    onSearch: (String) -> Unit,
    onExpandedChange: (Boolean) -> Unit,
    onResultClick: (String) -> Unit,
    onClearClick: () -> Unit,
    searchResults: List<String>,
    modifier: Modifier = Modifier,
    placeholderText: String = "Search..."
) {
    val scope = rememberCoroutineScope()

    // Monitor expansion state changes
    LaunchedEffect(searchBarState.currentValue) {
        onExpandedChange(searchBarState.currentValue == SearchBarValue.Expanded)
    }

    Box(modifier = modifier) {
        // Collapsed search bar
        SearchBar(
            state = searchBarState,
            shape = MaterialTheme.shapes.small,
            inputField = {
                SearchBarInputField(
                    textFieldState = textFieldState,
                    searchBarState = searchBarState,
                    placeholderText = placeholderText,
                    onSearch = { query ->
                        onSearch(query)
                        scope.launch { searchBarState.animateToCollapsed() }
                    },
                    onClearClick = onClearClick,
                )
            },
            modifier = Modifier.fillMaxWidth()
        )

        // Expanded full-screen search bar with results
        ExpandedFullScreenSearchBar(
            state = searchBarState,
            inputField = {
                SearchBarInputField(
                    textFieldState = textFieldState,
                    searchBarState = searchBarState,
                    placeholderText = placeholderText,
                    onSearch = { query ->
                        onSearch(query)
                        scope.launch { searchBarState.animateToCollapsed() }
                    },
                    onClearClick = onClearClick
                )
            }
        ) {
            // Search results content
            if (searchResults.isEmpty() && textFieldState.text.isNotEmpty()) {
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
                        .padding(vertical = 16.dp)
                )
            } else {
                searchResults.forEach { result ->
                    ListItem(
                        headlineContent = { Text(result) },
                        modifier = Modifier
                            .clickable {
                                onResultClick(result)
                            }
                            .fillMaxWidth()
                    )
                }
            }
        }
    }
}

/**
 * Extracted input field composable to avoid duplication
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchBarInputField(
    textFieldState: TextFieldState,
    searchBarState: androidx.compose.material3.SearchBarState,
    placeholderText: String,
    onSearch: (String) -> Unit,
    onClearClick: () -> Unit
) {
    SearchBarDefaults.InputField(
        textFieldState = textFieldState,
        searchBarState = searchBarState,
        shape = MaterialTheme.shapes.small,
        onSearch = onSearch,
        placeholder = { Text(placeholderText) },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        trailingIcon = {
            if (textFieldState.text.isNotEmpty()) {
                IconButton(
                    onClick = { 
                        textFieldState.clearText()
                        onClearClick()
                    }
                ) {
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
            textFieldState = rememberTextFieldState(),
            searchBarState = rememberSearchBarState(initialValue = SearchBarValue.Collapsed),
            onSearch = {},
            onExpandedChange = {},
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
            textFieldState = rememberTextFieldState(initialText = "John"),
            searchBarState = rememberSearchBarState(initialValue = SearchBarValue.Expanded),
            onSearch = {},
            onExpandedChange = {},
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

