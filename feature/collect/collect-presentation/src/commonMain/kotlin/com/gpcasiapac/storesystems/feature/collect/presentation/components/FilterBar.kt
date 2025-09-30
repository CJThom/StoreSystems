package com.gpcasiapac.storesystems.feature.collect.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Sort
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.FilterAlt
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material.icons.outlined.Receipt
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderlist.model.FilterChip as UiFilterChip
import com.gpcasiapac.storesystems.foundation.design_system.Dimens
import com.gpcasiapac.storesystems.foundation.design_system.GPCTheme
import org.jetbrains.compose.ui.tooling.preview.Preview


/**
 * Filter bar component with filter chips and generic additional filter toggles
 * Follows Material Design 3 guidelines and theme-driven styling
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterBar(
    selectedFilters: List<String>,
    additionalFilters: List<UiFilterChip>,
    onFilterToggle: (String) -> Unit,
    onAdditionalFilterRemove: (UiFilterChip) -> Unit,
    onSelectAction: () -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues,
    scrollBehavior: StickyHeaderScrollBehavior? = null,
) {
    val isLifted = scrollBehavior?.isLifted ?: false

    Surface(
        modifier = modifier.fillMaxWidth(),
        border = if (isLifted) BorderStroke(
            Dimens.Stroke.thin,
            MaterialTheme.colorScheme.outlineVariant
        ) else null,
        color = if (isLifted) MaterialTheme.colorScheme.surfaceContainerLow else Color.Transparent,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(contentPadding),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Left side - Filter icon and chips
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Dimens.Space.small)
            ) {
                // Filter icon button
                IconButton(
                    onClick = { /* Handle filter action */ },
                    modifier = Modifier.border(
                        border = BorderStroke(
                            Dimens.Stroke.thin,
                            MaterialTheme.colorScheme.outlineVariant
                        ),
                        shape = RoundedCornerShape(Dimens.Space.large)
                    ).size(Dimens.Size.iconLarge),
                ) {
                    Icon(
                        imageVector = Icons.Outlined.FilterAlt,
                        contentDescription = "Filter",
                        modifier = Modifier.size(Dimens.Size.iconSmall)
                    )
                }

                // Filter chips row
                Row(
                    horizontalArrangement = Arrangement.spacedBy(Dimens.Space.extraSmall),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    FilterChip(
                        label = "B2B",
                        isSelected = selectedFilters.contains("B2B"),
                        onToggle = { onFilterToggle("B2B") }
                    )

                    FilterChip(
                        label = "B2C",
                        isSelected = selectedFilters.contains("B2C"),
                        onToggle = { onFilterToggle("B2C") }
                    )

                    // Additional filter chips
                    additionalFilters.forEach { filterChip ->
                        AdditionalFilterChip(
                            filterChip = filterChip,
                            onRemove = { onAdditionalFilterRemove(filterChip) }
                        )
                    }
                }
                // Filter icon button
                IconButton(
                    onClick = { /* Handle filter action */ },
                    modifier = Modifier.border(
                        border = BorderStroke(
                            Dimens.Stroke.thin,
                            MaterialTheme.colorScheme.outlineVariant
                        ),
                        shape = RoundedCornerShape(Dimens.Space.small)
                    ).size(Dimens.Size.iconLarge),
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.Sort,
                        contentDescription = "Filter",
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(Dimens.Size.iconSmall)
                    )
                }
            }

            // Right side - SELECT button
            OutlinedButton(
                onClick = onSelectAction,
                modifier = Modifier.height(32.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = Color.Transparent,
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                ),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                shape = RoundedCornerShape(Dimens.Space.small),
                contentPadding = PaddingValues(horizontal = Dimens.Space.medium, vertical = 0.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Checklist,
                    contentDescription = null,
                    modifier = Modifier.size(ButtonDefaults.IconSize)
                )
                Spacer(Modifier.width(ButtonDefaults.IconSpacing))
                Text("SELECT")
            }
        }
    }
}

/**
 * Individual filter chip component
 */
@Composable
private fun FilterChip(
    label: String,
    isSelected: Boolean,
    onToggle: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography

    val backgroundColor = if (isSelected) colors.secondaryContainer else Color.Transparent
    val contentColor = if (isSelected) colors.onSecondaryContainer else colors.onSurface
    val borderColor = if (isSelected) colors.secondaryContainer else colors.outline

    Row(
        modifier = modifier
            .height(32.dp)
            .clip(RoundedCornerShape(Dimens.Space.small))
            .background(backgroundColor)
            .border(
                width = 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(Dimens.Space.small)
            )
            .clickable { onToggle(!isSelected) }
            .padding(horizontal = if (isSelected) Dimens.Space.small else Dimens.Space.medium),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Dimens.Space.extraSmall)
    ) {
        // Selected icon
        if (isSelected) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.size(18.dp)
            )
        }

        // Label text
        Text(
            text = label,
            style = typography.labelLarge.copy(
                color = contentColor,
                fontWeight = FontWeight.Medium
            )
        )
    }
}

/**
 * Additional filter chip with close button for any type of filter
 */
@Composable
private fun AdditionalFilterChip(
    filterChip: UiFilterChip,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography

    Row(
        modifier = modifier
            .height(32.dp)
            .clip(RoundedCornerShape(Dimens.Space.small))
            .background(colors.surfaceContainerHigh)
            .padding(start = Dimens.Space.small, end = Dimens.Space.extraSmall),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Dimens.Space.extraSmall)
    ) {
        // Type-specific icon
        Icon(
            imageVector = when (filterChip.type.name) {
                "PHONE" -> Icons.Outlined.Phone
                "ORDER_NUMBER" -> Icons.Outlined.Receipt
                "NAME" -> Icons.Outlined.Person
                else -> Icons.Outlined.FilterAlt
            },
            contentDescription = null,
            tint = colors.onSurface,
            modifier = Modifier.size(18.dp)
        )

        // Filter value text
        Text(
            text = filterChip.label,
            style = typography.labelLarge.copy(
                color = colors.onSurface,
                fontWeight = FontWeight.Medium
            )
        )

        // Close button
        IconButton(
            onClick = onRemove,
            modifier = Modifier.size(18.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Remove ${filterChip.type.name.lowercase()} filter",
                tint = colors.onSurface,
                modifier = Modifier.size(12.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
private fun FilterBarNoPhonePreview() {
    GPCTheme {
        Surface {
            var selectedFilters by remember { mutableStateOf(listOf("B2B")) }

            FilterBar(
                selectedFilters = selectedFilters,
                additionalFilters = emptyList(),
                onFilterToggle = { filter ->
                    selectedFilters = if (selectedFilters.contains(filter)) {
                        selectedFilters - filter
                    } else {
                        selectedFilters + filter
                    }
                },
                onAdditionalFilterRemove = { },
                onSelectAction = { /* Handle select */ },
                contentPadding = PaddingValues(Dimens.Space.medium),
            )
        }
    }
}
