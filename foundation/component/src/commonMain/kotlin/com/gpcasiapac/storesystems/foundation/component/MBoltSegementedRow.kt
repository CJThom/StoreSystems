package com.gpcasiapac.storesystems.foundation.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector


interface MBoltSegmentedRowOptionDisplayParam {
    val icon: ImageVector
    val label: String
    val enabled: Boolean
}

@Composable
fun <T : MBoltSegmentedRowOptionDisplayParam, R> MBoltSegmentedButtonRow(
    modifier: Modifier = Modifier,
    selected: R,
    onValueChange: (R) -> Unit,
    optionList: List<T>,
    selectionMapper: (T) -> R,
    equals: (T, R) -> Boolean = { a, b -> a == b },
) {
    SingleChoiceSegmentedButtonRow(modifier = modifier.fillMaxWidth()) {
        optionList.forEachIndexed { index, option ->
            MBoltSegmentedButton(
                modifier = Modifier.weight(1f),
                label = option.label,
                icon = option.icon,
                isActive = equals(option, selected),
                index = index,
                count = optionList.size,
                enabled = option.enabled,
                onClick = {
                    val selectedOption = selectionMapper(option)
                    onValueChange(selectedOption)
                }
            )
        }
    }
}