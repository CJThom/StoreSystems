package com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderlist.component

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.AppBarRow
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingToolbarDefaults
import androidx.compose.material3.HorizontalFloatingToolbar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.animateFloatingActionButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.reverseDifference
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gpcasiapac.storesystems.foundation.design_system.Dimens
import com.gpcasiapac.storesystems.foundation.design_system.GPCTheme


@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ToolbarFabContainer(
    hasDraft: Boolean,
    count: Int,
    onNewTask: () -> Unit,
    onDelete: () -> Unit,
    onView: () -> Unit,
    modifier: Modifier = Modifier,
    expanded: Boolean = true,
) {


    HorizontalFloatingToolbar(
        expanded = expanded,
        floatingActionButton = {
            Box(modifier = Modifier.fillMaxSize()) {
                FloatingToolbarDefaults.VibrantFloatingActionButton(
                    onClick = if (hasDraft) onView else onNewTask,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(56.dp)
                ) {
                    AnimatedContent(
                         targetState = hasDraft,
                         label = "FabIconSwap"
                     ) { showDraft ->
                         if (showDraft) {
                             Icon(
                                 imageVector = Icons.AutoMirrored.Outlined.ArrowForward,
                                 contentDescription = "View drafts"
                             )
                         } else {
                             Icon(
                                 imageVector = Icons.Outlined.Add,
                                 contentDescription = "Start work order",
                             )
                         }
                     }
                }
            }
        },
        contentPadding = PaddingValues(
            horizontal = Dimens.Space.medium,
            vertical = Dimens.Space.small
        ),
        modifier = modifier,
        animationSpec = MaterialTheme.motionScheme.defaultEffectsSpec(),
        colors = FloatingToolbarDefaults.standardFloatingToolbarColors(
            toolbarContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
        ),
        expandedShadowElevation = 5.dp,
    ) {
        AppBarRow(
            overflowIndicator = { menuState ->
                IconButton(onClick = { menuState.show() }) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "More"
                    )
                }
            },
            modifier = Modifier.fillMaxWidth(),
            maxItemCount = 3,
        ) {
            // Orders selected block as a custom AppBar item so it can overflow gracefully
            customItem(
                appbarContent = {
                    Row(
                        modifier = Modifier.height(
                            IconButtonDefaults.extraSmallContainerSize(
                                IconButtonDefaults.IconButtonWidthOption.Uniform
                            ).height
                        ),
                        verticalAlignment = Alignment.CenterVertically,
                        //   horizontalArrangement = Arrangement.spacedBy(Dimens.Space.extraSmall)
                    ) {
                        Text(
                            text = "$count",
                            style = MaterialTheme.typography.bodyLargeEmphasized,
                        )
                        Spacer(Modifier.size(Dimens.Space.extraSmall))
                        Text(
                            text = "order selected",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(Modifier.size(Dimens.Space.small))
                    }
                },
                menuContent = { state ->
                    DropdownMenuItem(
                        text = { Text("$count order selected") },
                        onClick = { state.dismiss() }
                    )
                }
            )
            customItem(
                appbarContent = {
                    IconButton(
                        modifier = Modifier
                            .size(
                                IconButtonDefaults.extraSmallContainerSize(
                                    IconButtonDefaults.IconButtonWidthOption.Uniform
                                )
                            ),
                        onClick = onDelete
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Delete,
                            contentDescription = "Delete drafts"
                        )
                    }
                },
                menuContent = { state ->
                    DropdownMenuItem(
                        text = { Text("Delete drafts") },
                        onClick = {
                            onDelete()
                            state.dismiss()
                        }
                    )
                }
            )
        }
    }
}
//
//
//@OptIn(ExperimentalMaterial3ExpressiveApi::class)
//@Composable
//fun DraftFab(
//    count: Int,
//    onDelete: () -> Unit,
//    onView: () -> Unit,
//    modifier: Modifier = Modifier,
//    expanded: Boolean = true
//) {
//    FloatingActionButton(
//        onClick = {
//
//        },
//        modifier = modifier,
//    ) {
//        Row(
//            modifier = Modifier.padding(horizontal = Dimens.Space.large),
//            verticalAlignment = Alignment.CenterVertically,
//        ) {
//            OutlinedIconButton(
//                modifier = Modifier.minimumInteractiveComponentSize()
//                    .size(IconButtonDefaults.extraSmallContainerSize(IconButtonDefaults.IconButtonWidthOption.Uniform)),
//                onClick = onDelete
//            ) {
//                Icon(
//                    imageVector = Icons.Outlined.Delete,
//                    contentDescription = "Drafts"
//                )
//            }
//
//            Column(
//                modifier = Modifier,
//                horizontalAlignment = Alignment.CenterHorizontally
//            ) {
//
//                Text(
//                    text = "Collection in progress",
//                    style = MaterialTheme.typography.labelSmall,
//                    //    color = MaterialTheme.colorScheme.onSurface
//                )
//                // Spacer(modifier = Modifier.width(Dimens.Space.small))
//                Row(verticalAlignment = Alignment.CenterVertically) {
//                    Text(
//                        text = "${count}",
//                        style = MaterialTheme.typography.titleLarge,
//                        //   color = MaterialTheme.colorScheme.onSurface
//                    )
//                    Spacer(modifier = Modifier.width(Dimens.Space.small))
//                    Text(
//                        text = "order selected",
//                        // style = MaterialTheme.typography.titleLarge,
//                        //   color = MaterialTheme.colorScheme.onSurface
//                    )
//
//                }
//            }
//            Spacer(Modifier.size(Dimens.Space.medium))
//            FilledIconButton(
//                modifier = Modifier.minimumInteractiveComponentSize()
//                    .size(
//                        IconButtonDefaults.smallContainerSize(
//                            IconButtonDefaults.IconButtonWidthOption.Wide
//                        )
//                    ),
//                //shapes = ,
//                onClick = onView
//            ) {
//                Icon(
//                    imageVector = Icons.AutoMirrored.Outlined.ArrowForward,
//                    contentDescription = "Drafts"
//                )
//            }
//        }
//
//    }
//}
//
//@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
//@Composable
//fun DraftBottomBar(
//    count: Int,
//    onDelete: () -> Unit,
//    onView: () -> Unit,
//    modifier: Modifier = Modifier,
//    expanded: Boolean = true
//) {
//
////    Box(
////        modifier = modifier.fillMaxWidth()
////    ) {
//    HorizontalFloatingToolbar(
//        modifier = Modifier
//            //    .align(Alignment.Center)
//            .padding(Dimens.Space.medium)
//            .height(IntrinsicSize.Min),
//        expanded = expanded,
//        colors = FloatingToolbarDefaults.standardFloatingToolbarColors(
//            toolbarContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
//        ),
//        expandedShadowElevation = 5.dp,
//        leadingContent = {
//            OutlinedIconButton(
//                modifier = Modifier.minimumInteractiveComponentSize()
//                    .size(IconButtonDefaults.extraSmallContainerSize(IconButtonDefaults.IconButtonWidthOption.Uniform)),
//                onClick = onDelete
//            ) {
//                Icon(
//                    imageVector = Icons.Outlined.Delete,
//                    contentDescription = "Drafts"
//                )
//            }
//        },
//
//        ) {
//        Row(
//            verticalAlignment = Alignment.CenterVertically,
//        ) {
//            Column(
//                modifier = Modifier.padding(horizontal = Dimens.Space.large),
//                horizontalAlignment = Alignment.CenterHorizontally
//            ) {
//
//                Text(
//                    text = "Collection in progress",
//                    style = MaterialTheme.typography.labelSmall,
//                    //    color = MaterialTheme.colorScheme.onSurface
//                )
//                // Spacer(modifier = Modifier.width(Dimens.Space.small))
//                Row(verticalAlignment = Alignment.CenterVertically) {
//                    Text(
//                        text = "${count}",
//                        style = MaterialTheme.typography.titleLarge,
//                        //   color = MaterialTheme.colorScheme.onSurface
//                    )
//                    Spacer(modifier = Modifier.width(Dimens.Space.small))
//                    Text(
//                        text = "order selected",
//                        // style = MaterialTheme.typography.titleLarge,
//                        //   color = MaterialTheme.colorScheme.onSurface
//                    )
//
//                }
//            }
//            FilledIconButton(
//                modifier = Modifier.minimumInteractiveComponentSize()
//                    .size(
//                        IconButtonDefaults.smallContainerSize(
//                            IconButtonDefaults.IconButtonWidthOption.Wide
//                        )
//                    ),
//                //shapes = ,
//                onClick = onView
//            ) {
//                Icon(
//                    imageVector = Icons.AutoMirrored.Outlined.ArrowForward,
//                    contentDescription = "Drafts"
//                )
//            }
//        }
//
//        //    }
//
//    }
//}

@Preview
@Composable
fun DraftBottomBarNEwPreview() {
    GPCTheme {
        ToolbarFabContainer(
            count = 1,
            onDelete = {},
            onView = {},
            hasDraft = true,
            onNewTask = {},
            expanded = true,
        )
    }
}

@Preview
@Composable
fun ToolbarFabExpandedPreview() {
    GPCTheme {
        ToolbarFabContainer(
            hasDraft = true,
            count = 2,
            onNewTask = {},
            onDelete = {},
            onView = {},
            expanded = true,
        )
    }
}

@Preview
@Composable
fun ToolbarFabExpanded2Preview() {
    GPCTheme {
        ToolbarFabContainer(
            hasDraft = true,
            count = 5,
            onNewTask = {},
            onDelete = {},
            onView = {},
            expanded = true,
        )
    }
}

@Preview
@Composable
fun ToolbarFabCollapsedPreview() {
    GPCTheme {
        ToolbarFabContainer(
            hasDraft = false,
            count = 0,
            onNewTask = {},
            onDelete = {},
            onView = {},
            expanded = false,
        )
    }
}


