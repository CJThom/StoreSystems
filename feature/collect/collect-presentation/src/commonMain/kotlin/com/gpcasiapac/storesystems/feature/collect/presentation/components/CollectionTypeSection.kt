package com.gpcasiapac.storesystems.feature.collect.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BusinessCenter
import androidx.compose.material.icons.outlined.LocalShipping
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.gpcasiapac.storesystems.feature.collect.domain.model.CollectingType
import com.gpcasiapac.storesystems.foundation.design_system.Dimens
import com.gpcasiapac.storesystems.foundation.design_system.GPCTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun CollectionTypeSection(
    modifier: Modifier = Modifier,
    onValueChange: (CollectingType) -> Unit,
) {
    var selectedIndex by remember {
        mutableIntStateOf(0)
    }
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(Dimens.Space.medium)
    ) {
        Text(
            text = "Who's collecting?",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Medium),
            color = MaterialTheme.colorScheme.onBackground
        )
        SingleChoiceSegmentedButtonRow {
            CustomerTypeSegmentedButton(
                label = "STANDARD",
                icon = Icons.Outlined.Person,
                isActive = selectedIndex == 0,
                index = 0,
                count = 3,
                enabled = false,
                onClick = {
                    selectedIndex = 0
                    onValueChange(CollectingType.STANDARD)
                }
            )
            CustomerTypeSegmentedButton(
                label = "ACCOUNT",
                icon = Icons.Outlined.BusinessCenter,
                isActive = selectedIndex == 1,
                index = 1,
                count = 3,

                onClick = {
                    selectedIndex = 1
                    onValueChange(CollectingType.ACCOUNT)
                }
            )
            CustomerTypeSegmentedButton(
                label = "COURIER",
                icon = Icons.Outlined.LocalShipping,
                isActive = selectedIndex == 2,
                index = 2,
                count = 3,
                onClick = {
                    selectedIndex = 2
                    onValueChange(CollectingType.COURIER)
                }
            )
        }

    }
}

@Preview
@Composable
fun CollectionTypeSectionPreview() {
    GPCTheme {
        Surface {
            CollectionTypeSection(
                onValueChange = {

                }
            )
        }
    }
}
