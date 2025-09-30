package com.gpcasiapac.storesystems.feature.collect.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BusinessCenter
import androidx.compose.material.icons.outlined.LocalShipping
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import com.gpcasiapac.storesystems.feature.collect.domain.model.CollectingType
import com.gpcasiapac.storesystems.foundation.component.MBoltSegmentedButtonRow
import com.gpcasiapac.storesystems.foundation.component.MBoltSegmentedRowOptionDisplayParam
import com.gpcasiapac.storesystems.foundation.design_system.Dimens
import com.gpcasiapac.storesystems.foundation.design_system.GPCTheme
import org.jetbrains.compose.ui.tooling.preview.Preview


data class CollectionTypeSectionDisplayParam(
    val collectingType: CollectingType,
    override val icon: ImageVector,
    override val label: String,
    override val enabled: Boolean,
) : MBoltSegmentedRowOptionDisplayParam

@Composable
fun CollectionTypeSection(
    title: String,
    value: CollectingType,
    options: List<CollectionTypeSectionDisplayParam>,
    modifier: Modifier = Modifier,
    onValueChange: (CollectingType) -> Unit,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(Dimens.Space.medium)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium
        )
        MBoltSegmentedButtonRow(
            selected = value,
            options = options,
            selectionMapper = { option ->
                option.collectingType
            },
            onValueChange = onValueChange,
            equals = { a, b -> a.collectingType == b }
        )
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
                title = "Who's Collecting",
                options = listOf(
                    CollectionTypeSectionDisplayParam(
                        collectingType = CollectingType.STANDARD,
                        icon = Icons.Outlined.Person,
                        label = CollectingType.STANDARD.name,
                        enabled = true
                    ),
                    CollectionTypeSectionDisplayParam(
                        enabled = true,
                        collectingType = CollectingType.ACCOUNT,
                        icon = Icons.Outlined.BusinessCenter,
                        label = CollectingType.ACCOUNT.name,
                    ),
                    CollectionTypeSectionDisplayParam(
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
