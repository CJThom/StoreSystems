package com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderlist.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FlexibleBottomAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.gpcasiapac.storesystems.foundation.design_system.Dimens
import com.gpcasiapac.storesystems.foundation.design_system.GPCTheme


@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun MultiSelectBottomBar(
    selectedCount: Int,
    isSelectAllChecked: Boolean,
    onSelectAllToggle: (Boolean) -> Unit,
    onCancelClick: () -> Unit,
    onSelectClick: () -> Unit,
    modifier: Modifier = Modifier,
) {

    FlexibleBottomAppBar(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
    ) {

        TextButton(
            onClick = {
                onSelectAllToggle(!isSelectAllChecked)
            },
            modifier = Modifier.height(ButtonDefaults.ExtraSmallContainerHeight),
            contentPadding = ButtonDefaults.contentPaddingFor(ButtonDefaults.ExtraSmallContainerHeight)
        ) {
            Checkbox(
                checked = isSelectAllChecked,
                onCheckedChange = null
            )
            Spacer(Modifier.size(Dimens.Space.medium))
            Text(
                text = "SELECT ALL",
                style = MaterialTheme.typography.bodySmall
            )
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(Dimens.Space.extraSmall),
            verticalAlignment = Alignment.CenterVertically
        ) {

            TextButton(
                onClick = onCancelClick,
                modifier = Modifier.height(ButtonDefaults.ExtraSmallContainerHeight),
                contentPadding = ButtonDefaults.contentPaddingFor(ButtonDefaults.ExtraSmallContainerHeight)
            ) {
                Text(
                    text = "CANCEL"
                )
            }

            Button(
                onClick = onSelectClick,
                modifier = Modifier.height(ButtonDefaults.ExtraSmallContainerHeight),
                contentPadding = ButtonDefaults.contentPaddingFor(ButtonDefaults.ExtraSmallContainerHeight)
            ) {
                Text(
                    text = buildString {
                        append("SELECT")
                        if (selectedCount > 0) {
                            append(" $selectedCount")
                        }
                    }
                )
            }

        }
    }

}


@Preview
@Composable
fun MultiSelectBottomBarPreview() {
    GPCTheme {
        MultiSelectBottomBar(
            selectedCount = 5,
            isSelectAllChecked = false,
            onSelectAllToggle = {},
            onCancelClick = {},
            onSelectClick = {},
        )
    }
}

//
//@Composable
//fun MultiSelectBottomBar(
//    selectedCount: Int,
//    isSelectAllChecked: Boolean,
//    onSelectAllToggle: (Boolean) -> Unit,
//    onCancelClick: () -> Unit,
//    onSelectClick: () -> Unit,
//    modifier: Modifier = Modifier,
//) {
//    BottomAppBar(
//        modifier = modifier
//            .themedBorder(shape = RectangleShape)
//            .navigationBarsPadding(),
//        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
//        contentPadding = PaddingValues(horizontal = Dimens.Space.medium, vertical = 0.dp),
//    ) {
//        Row(
//            modifier = Modifier.fillMaxWidth(),
//            verticalAlignment = Alignment.CenterVertically,
//            horizontalArrangement = Arrangement.SpaceBetween
//        ) {
//            Row(
//                verticalAlignment = Alignment.CenterVertically,
//                horizontalArrangement = Arrangement.spacedBy(Dimens.Space.semiMedium),
//                modifier = Modifier
//                    .padding(Dimens.Space.semiMedium)
//                    .clickable() {
//                        onSelectAllToggle(!isSelectAllChecked)
//                    }
//            ) {
//                Checkbox(
//                    checked = isSelectAllChecked,
//                    onCheckedChange = null
//                )
//
//                Text(
//                    text = "SELECT ALL",
//                    style = MaterialTheme.typography.bodySmall
//                )
//            }
//            Row(
//                horizontalArrangement = Arrangement.spacedBy(Dimens.Space.extraSmall),
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                TextButton(
//                    onClick = onCancelClick,
//                    shape = MaterialTheme.shapes.small,
//                ) {
//                    Text(
//                        text = "CANCEL"
//                    )
//                }
//                Button(
//                    onClick = onSelectClick,
//                    modifier = Modifier.height(Dimens.Size.buttonSizeSmall),
//                    contentPadding = PaddingValues(
//                        horizontal = Dimens.Space.medium,
//                        vertical = Dimens.Space.extraSmall
//                    )
//                ) {
//                    Text(
//                        text = buildString {
//                            append("SELECT ")
//                            if (selectedCount > 0) {
//                                append(selectedCount.toString())
//                            }
//                        }
//                    )
//                }
//            }
//        }
//    }
//}