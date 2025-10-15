package com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderlist

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Drafts
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalFloatingToolbar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun DraftBottomBar(
    count: Int,
    onDelete: () -> Unit,
    onView: () -> Unit,
    modifier: Modifier = Modifier,
) {
//    Surface(
//        tonalElevation = 3.dp,
//        shadowElevation = 6.dp,
//        color = MaterialTheme.colorScheme.surface,
//        modifier = modifier.navigationBarsPadding()
//    ) {


    HorizontalFloatingToolbar(
        expanded = true,
        leadingContent = {
            IconButton(
                //  shapes = IconButtonShapes(MaterialTheme.shapes.small),
                onClick = {

                }
            ) {
                Icon(imageVector = Icons.Outlined.Drafts, contentDescription = "Drafts")
            }
        }
    ) {
        Text(
            text = "Draft in progress • $count selected",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }


//    VerticalFloatingToolbar(
//        expanded = false,
//    ){
//            Text(
//                text = "Draft in progress • $count selected",
//                style = MaterialTheme.typography.bodyMedium,
//                color = MaterialTheme.colorScheme.onSurface
//            )
//            Row(horizontalArrangement = Arrangement.spacedBy(Dimens.Space.small)) {
//                TextButton(onClick = onDelete) {
//                    Text("Delete")
//                }
//                TextButton(onClick = onView) {
//                    Text("View")
//                }
//            }
//    }

//    FlexibleBottomAppBar(
//
//    ){
//        Row(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(horizontal = Dimens.Space.medium, vertical = Dimens.Space.small),
//            horizontalArrangement = Arrangement.SpaceBetween
//        ) {
//            Text(
//                text = "Draft in progress • $count selected",
//                style = MaterialTheme.typography.bodyMedium,
//                color = MaterialTheme.colorScheme.onSurface
//            )
//            Row(horizontalArrangement = Arrangement.spacedBy(Dimens.Space.small)) {
//                TextButton(onClick = onDelete) {
//                    Text("Delete")
//                }
//                TextButton(onClick = onView) {
//                    Text("View")
//                }
//            }
//        }
//    }
}

@Preview
@Composable
fun DraftBottomBarPreview() {
    DraftBottomBar(
        count = 1,
        onDelete = {},
        onView = {},
    )
}

