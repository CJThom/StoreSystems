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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.gpcasiapac.storesystems.feature.collect.domain.model.CollectingType
import com.gpcasiapac.storesystems.feature.collect.domain.model.Representative
import com.gpcasiapac.storesystems.feature.collect.presentation.components.HeaderMedium
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderfulfillment.component.AccountCollectionContent
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderfulfillment.component.CourierCollectionContent
import com.gpcasiapac.storesystems.foundation.component.MBoltSegmentedButtonRow
import com.gpcasiapac.storesystems.foundation.component.MBoltSegmentedRowOptionDisplayParam
import com.gpcasiapac.storesystems.foundation.design_system.Dimens
import com.gpcasiapac.storesystems.foundation.design_system.GPCTheme
import org.jetbrains.compose.ui.tooling.preview.Preview


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
    value: CollectingType,
    onValueChange: (CollectingType) -> Unit,
    optionList: List<CollectionTypeSectionDisplayState>,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(Dimens.Space.medium),
    content: @Composable (selectedType: CollectingType) -> Unit = {}
) {
    Column(
        modifier = modifier.padding(contentPadding),
    ) {

        HeaderMedium(
            text = title,
            contentPadding = PaddingValues(0.dp)
        )

        Spacer(Modifier.size(Dimens.Space.medium))

        MBoltSegmentedButtonRow(
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
            content(v)
        }

    }
}


@Preview
@Composable
fun CollectionTypeSectionPreview() {
    GPCTheme {
        Surface {
            CollectionTypeSection(
                value = CollectingType.STANDARD,
                onValueChange = {

                },
                title = "Who's Collecting?",
                optionList = listOf(
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
            )
        }
    }
}

private val sampleRepresentatives = listOf(
    Representative("1", "John Doe", "#9288180049912"),
    Representative("2", "Custa Ma", "#9288180049913"),
    Representative("3", "Alice Smith", "#9288180049914")
)

@Preview
@Composable
fun CollectionTypeSectionAccountPreview() {
    var searchQuery by remember { mutableStateOf("") }
    var selectedRepresentatives by remember { mutableStateOf(setOf("1")) }

    GPCTheme {
        Surface {
            CollectionTypeSection(
                value = CollectingType.ACCOUNT,
                onValueChange = {},
                title = "Who's Collecting?",
                optionList = listOf(
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
                ),
            ) {
                AccountCollectionContent(
                    searchQuery = searchQuery,
                    onSearchQueryChange = { searchQuery = it },
                    representatives = sampleRepresentatives,
                    selectedRepresentativeIds = selectedRepresentatives,
                    onRepresentativeSelected = { id, isSelected ->
                        if (isSelected) {
                            selectedRepresentatives += id
                        } else {
                            selectedRepresentatives -= id
                        }
                    }
                )
            }
        }
    }
}

@Preview
@Composable
fun CollectionTypeSectionCourierPreview() {
    var courierName by remember { mutableStateOf("Postman Pat") }

    GPCTheme {
        Surface {
            CollectionTypeSection(
                value = CollectingType.COURIER,
                onValueChange = {},
                title = "Who's Collecting?",
                optionList = listOf(
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
                ),
            ) {
                CourierCollectionContent(
                    courierName = courierName,
                    onCourierNameChange = { courierName = it }
                )
            }
        }
    }
}