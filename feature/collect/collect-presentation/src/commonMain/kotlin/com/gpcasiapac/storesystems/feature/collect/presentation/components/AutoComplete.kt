package com.gpcasiapac.storesystems.feature.collect.presentation.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.jetbrains.compose.ui.tooling.preview.Preview


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> AutoSuggestSearchBar(
    value: T?,
    onValueChange: (T) -> Unit,
    label: @Composable (T) -> Unit,
    fetchData: suspend (String) -> List<T>,
    placeholder: String = "Search...",
    modifier: Modifier = Modifier,
) {
    var query by remember { mutableStateOf(TextFieldValue("")) }
    var results by remember { mutableStateOf<List<T>>(emptyList()) }
    var loading by remember { mutableStateOf(false) }
    var expanded by remember { mutableStateOf(false) }
    var empty by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    var searchJob by remember { mutableStateOf<Job?>(null) }

    fun doSearch(q: String) {
        searchJob?.cancel()
        searchJob = scope.launch {
            loading = true
            val data = fetchData(q)
            results = data
            empty = data.isEmpty()
            loading = false
        }
    }

    ExposedDropdownMenuBox(
        expanded = expanded && (results.isNotEmpty() || empty),
        onExpandedChange = { expanded = it }
    ) {
        TextField(
            modifier = modifier
                .menuAnchor(MenuAnchorType.PrimaryEditable, true)
                .fillMaxWidth(),
            value = query,
            onValueChange = {
                query = it
                doSearch(it.text)
                expanded = true
            },
            leadingIcon = {
                Icon(Icons.Default.Search, contentDescription = "Search")
            },
            placeholder = { Text(placeholder) },
            trailingIcon = {
                if (loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp
                    )
                }
            },
            singleLine = true
        )

        ExposedDropdownMenu(
            expanded = expanded && (results.isNotEmpty() || empty),
            onDismissRequest = { expanded = false }
        ) {
            if (empty) {
                DropdownMenuItem(
                    text = { Text("No results found") },
                    onClick = { }
                )
            } else {
                results.forEach { item ->
                    DropdownMenuItem(
                        text = {
                            label(item)
                        },
                        onClick = {
                            onValueChange(item)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun AsyncAutocompletePreview() {
    var selected by remember { mutableStateOf<SearchResultItem?>(null) }
    AutoSuggestSearchBar(
        value = selected,
        onValueChange = { selected = it },
        label = {
            SearchResultItemSuggestion(
                onFilterClick = {},
                text = "123582897426",
                icon = Icons.Default.Search,
            )
        },
        fetchData = { q -> listOf() },
        placeholder = "Search by Order No, Customer."
    )
}

data class SearchResultItem(
    val text: String,
    val icon: ImageVector,
    val type: SearchResultType
)

enum class SearchResultType {
    PHONE, WEBSITE, RECEIPT
}