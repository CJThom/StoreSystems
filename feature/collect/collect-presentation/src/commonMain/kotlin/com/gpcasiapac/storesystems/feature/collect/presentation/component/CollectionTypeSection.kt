package com.gpcasiapac.storesystems.feature.collect.presentation.component

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BusinessCenter
import androidx.compose.material.icons.outlined.LocalShipping
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gpcasiapac.storesystems.feature.collect.domain.model.CollectingType
import com.gpcasiapac.storesystems.feature.collect.domain.model.Representative
import com.gpcasiapac.storesystems.feature.collect.presentation.components.IdVerification
import com.gpcasiapac.storesystems.foundation.component.HeaderMedium
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderfulfillment.component.AccountCollectionContent
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderfulfillment.component.CourierCollectionContent
import com.gpcasiapac.storesystems.foundation.component.MBoltExpressiveButtonRow
import com.gpcasiapac.storesystems.foundation.component.MBoltSegmentedRowOptionDisplayParam
import com.gpcasiapac.storesystems.foundation.design_system.Dimens
import com.gpcasiapac.storesystems.foundation.design_system.GPCTheme


data class CollectionTypeSectionDisplayState(
    val collectingType: CollectingType,
    override val icon: ImageVector,
    override val label: String,
    override val enabled: Boolean,
) : MBoltSegmentedRowOptionDisplayParam

// TODO: Create reusable component
@Composable
fun CollectionTypeSection(
    title: String,
    value: CollectingType?,
    onValueChange: (CollectingType) -> Unit,
    optionList: List<CollectionTypeSectionDisplayState>,
    // ACCOUNT flow
    isAccountRepresentativeSelectionEnabled: Boolean = false,
    representativeSearchQuery: String = "",
    onRepresentativeSearchQueryChange: (String) -> Unit = {},
    representatives: List<Representative> = emptyList(),
    selectedRepresentativeIds: Set<String> = emptySet(),
    onRepresentativeSelected: (id: String, isSelected: Boolean) -> Unit = { _, _ -> },
    // Shared Id verification
    idVerified: Boolean = false,
    onIdVerifiedChange: (Boolean) -> Unit = {},
    // COURIER flow
    courierName: String = "",
    onCourierNameChange: (String) -> Unit = {},
    isLoading: Boolean = false,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(Dimens.Space.medium),
) {
    Column(
        modifier = modifier.padding(contentPadding),
    ) {

        HeaderMedium(
            text = title,
            contentPadding = PaddingValues(0.dp)
        )

        Spacer(Modifier.size(Dimens.Space.medium))

        MBoltExpressiveButtonRow(
            selected = value,
            optionList = optionList,
            selectionMapper = { option ->
                option.collectingType
            },
            onValueChange = onValueChange,
            equals = { a, b -> a.collectingType == b }
        )

        AnimatedContent(
            targetState = value,
            modifier = Modifier.fillMaxWidth()
        ) { v ->

            when (v) {
                CollectingType.STANDARD -> {
                    IdVerification(
                        checked = idVerified,
                        onCheckedChange = onIdVerifiedChange,
                        isLoading = isLoading,
                        contentPadding = PaddingValues(vertical = Dimens.Space.medium)
                    )
                }

                CollectingType.ACCOUNT -> {
                    Column {
                        if (isAccountRepresentativeSelectionEnabled) {
                            AccountCollectionContent(
                                searchQuery = representativeSearchQuery,
                                onSearchQueryChange = onRepresentativeSearchQueryChange,
                                representatives = representatives,
                                selectedRepresentativeIds = selectedRepresentativeIds,
                                onRepresentativeSelected = onRepresentativeSelected,
                                isLoading = isLoading,
                                contentPadding = PaddingValues(vertical = Dimens.Space.medium)
                            )
                        }

                        IdVerification(
                            checked = idVerified,
                            onCheckedChange = onIdVerifiedChange,
                            isLoading = isLoading,
                            contentPadding = PaddingValues(vertical = Dimens.Space.medium)
                        )
                    }
                }

                CollectingType.COURIER -> {
                    CourierCollectionContent(
                        courierName = courierName,
                        onCourierNameChange = onCourierNameChange,
                        isLoading = isLoading,
                        modifier = Modifier,
                        contentPadding = PaddingValues(vertical = Dimens.Space.medium)
                    )
                }

                else -> {

                }

            }
        }

    }
}


// Sample data for previews
private val sampleRepresentatives = listOf(
    Representative("1", "John Doe", "#9288180049912"),
    Representative("2", "Custa Ma", "#9288180049913"),
    Representative("3", "Alice Smith", "#9288180049914")
)

// Parameterized preview model to consolidate scenarios
private data class CollectionTypePreviewCase(
    val title: String,
    val value: CollectingType?,
    val optionList: List<CollectionTypeSectionDisplayState>,
    val isAccountRepresentativeSelectionEnabled: Boolean,
    val representativeSearchQuery: String,
    val representatives: List<Representative>,
    val selectedRepresentativeIds: Set<String>,
    val idVerified: Boolean,
    val courierName: String,
    val isLoading: Boolean,
    val label: String
) {
    override fun toString(): String = label
}

// Provider for all preview scenarios to keep them maintainable in one place
private class CollectionTypePreviewProvider :
    androidx.compose.ui.tooling.preview.PreviewParameterProvider<CollectionTypePreviewCase> {
    private val baseOptions = listOf(
        CollectionTypeSectionDisplayState(
            collectingType = CollectingType.STANDARD,
            icon = Icons.Outlined.Person,
            label = CollectingType.STANDARD.name,
            enabled = true
        ),
        CollectionTypeSectionDisplayState(
            enabled = true,
            collectingType = CollectingType.ACCOUNT,
            icon = Icons.Outlined.BusinessCenter,
            label = CollectingType.ACCOUNT.name,
        ),
        CollectionTypeSectionDisplayState(
            enabled = true,
            collectingType = CollectingType.COURIER,
            icon = Icons.Outlined.LocalShipping,
            label = CollectingType.COURIER.name,
        )
    )

    override val values: Sequence<CollectionTypePreviewCase> = sequenceOf(
        CollectionTypePreviewCase(
            title = "Who's Collecting?",
            value = CollectingType.STANDARD,
            optionList = baseOptions,
            isAccountRepresentativeSelectionEnabled = false,
            representativeSearchQuery = "",
            representatives = emptyList(),
            selectedRepresentativeIds = emptySet(),
            idVerified = false,
            courierName = "",
            isLoading = false,
            label = "Standard"
        ),
        CollectionTypePreviewCase(
            title = "Who's Collecting?",
            value = CollectingType.ACCOUNT,
            optionList = baseOptions,
            isAccountRepresentativeSelectionEnabled = true,
            representativeSearchQuery = "",
            representatives = sampleRepresentatives,
            selectedRepresentativeIds = setOf("1"),
            idVerified = false,
            courierName = "",
            isLoading = false,
            label = "Account (selection)"
        ),
        CollectionTypePreviewCase(
            title = "Who's Collecting?",
            value = CollectingType.ACCOUNT,
            optionList = baseOptions,
            isAccountRepresentativeSelectionEnabled = true,
            representativeSearchQuery = "jo",
            representatives = sampleRepresentatives,
            selectedRepresentativeIds = setOf("1", "3"),
            idVerified = true,
            courierName = "",
            isLoading = true,
            label = "Account (loading, filtered, verified)"
        ),
        CollectionTypePreviewCase(
            title = "Who's Collecting?",
            value = CollectingType.COURIER,
            optionList = baseOptions,
            isAccountRepresentativeSelectionEnabled = false,
            representativeSearchQuery = "",
            representatives = emptyList(),
            selectedRepresentativeIds = emptySet(),
            idVerified = false,
            courierName = "",
            isLoading = false,
            label = "Courier (empty)"
        ),
        CollectionTypePreviewCase(
            title = "Who's Collecting?",
            value = CollectingType.COURIER,
            optionList = baseOptions,
            isAccountRepresentativeSelectionEnabled = false,
            representativeSearchQuery = "",
            representatives = emptyList(),
            selectedRepresentativeIds = emptySet(),
            idVerified = false,
            courierName = "Australia Post",
            isLoading = false,
            label = "Courier (populated)"
        ),
        CollectionTypePreviewCase(
            title = "Who's Collecting?",
            value = CollectingType.COURIER,
            optionList = baseOptions,
            isAccountRepresentativeSelectionEnabled = false,
            representativeSearchQuery = "",
            representatives = emptyList(),
            selectedRepresentativeIds = emptySet(),
            idVerified = false,
            courierName = "DHL Express",
            isLoading = true,
            label = "Courier (loading)"
        )
    )
}

@Preview(showBackground = true)
@Composable
private fun CollectionTypeSectionPreview(
    @androidx.compose.ui.tooling.preview.PreviewParameter(CollectionTypePreviewProvider::class) case: CollectionTypePreviewCase
) {
    GPCTheme {
        Surface {
            CollectionTypeSection(
                title = case.title,
                value = case.value,
                onValueChange = {},
                optionList = case.optionList,
                isAccountRepresentativeSelectionEnabled = case.isAccountRepresentativeSelectionEnabled,
                representativeSearchQuery = case.representativeSearchQuery,
                onRepresentativeSearchQueryChange = {},
                representatives = case.representatives,
                selectedRepresentativeIds = case.selectedRepresentativeIds,
                onRepresentativeSelected = { _, _ -> },
                idVerified = case.idVerified,
                onIdVerifiedChange = {},
                courierName = case.courierName,
                onCourierNameChange = {},
                isLoading = case.isLoading
            )
        }
    }
}