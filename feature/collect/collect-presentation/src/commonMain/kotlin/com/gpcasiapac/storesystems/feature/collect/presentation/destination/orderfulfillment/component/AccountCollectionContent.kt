package com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderfulfillment.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.gpcasiapac.storesystems.common.presentation.compose.placeholder.material3.placeholder
import com.gpcasiapac.storesystems.feature.collect.domain.model.Representative
import com.gpcasiapac.storesystems.feature.collect.presentation.component.RepresentativeDetails
import com.gpcasiapac.storesystems.foundation.component.CheckboxCard
import com.gpcasiapac.storesystems.foundation.design_system.Dimens
import com.gpcasiapac.storesystems.foundation.design_system.GPCTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
internal fun AccountCollectionContent(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    representatives: List<Representative>,
    selectedRepresentativeIds: Set<String>,
    onRepresentativeSelected: (id: String, isSelected: Boolean) -> Unit,
    isLoading: Boolean = false
) {
    Column(verticalArrangement = Arrangement.spacedBy(Dimens.Space.medium)) {
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .placeholder(visible = isLoading),
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            placeholder = { Text("Search by Order #, Name, Phone") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search"
                )
            }
        )

        representatives.forEach { representative ->
            CheckboxCard(
                isCheckable = true,
                isChecked = selectedRepresentativeIds.contains(representative.id),
                onClick = { onRepresentativeSelected(representative.id, !selectedRepresentativeIds.contains(representative.id)) },
                onCheckedChange = { isChecked ->
                    onRepresentativeSelected(representative.id, isChecked)
                }
            ) {
                RepresentativeDetails(
                    name = representative.name,
                    customerNumber = representative.customerNumber,
                    isLoading = isLoading
                )
            }
        }
    }
}

private val sampleRepresentatives = listOf(
    Representative("rep-1", "John Doe", "#9288180049912"),
    Representative("rep-2", "Custa Ma", "#9288180049913"),
    Representative("rep-3", "Alice Smith", "#9288180049914"),
)

@Preview
@Composable
private fun AccountCollectionContentPreview() {
    var searchQuery by remember { mutableStateOf("Jo") }
    var selectedIds by remember { mutableStateOf(setOf("rep-1")) }

    GPCTheme {
        Surface {
            AccountCollectionContent(
                searchQuery = searchQuery,
                onSearchQueryChange = { searchQuery = it },
                representatives = sampleRepresentatives.filter { it.name.contains(searchQuery, ignoreCase = true) },
                selectedRepresentativeIds = selectedIds,
                onRepresentativeSelected = { id, isSelected ->
                    selectedIds = if (isSelected) {
                        selectedIds + id
                    } else {
                        selectedIds - id
                    }
                }
            )
        }
    }
}

@Preview
@Composable
private fun AccountCollectionContentLoadingPreview() {
    GPCTheme {
        Surface {
            AccountCollectionContent(
                searchQuery = "",
                onSearchQueryChange = { },
                representatives = sampleRepresentatives.take(2),
                selectedRepresentativeIds = emptySet(),
                onRepresentativeSelected = { _, _ -> },
                isLoading = true
            )
        }
    }
}