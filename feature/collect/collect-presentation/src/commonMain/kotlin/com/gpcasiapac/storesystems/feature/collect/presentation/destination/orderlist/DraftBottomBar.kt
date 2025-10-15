package com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderlist

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FloatingToolbarDefaults
import androidx.compose.material3.HorizontalFloatingToolbar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gpcasiapac.storesystems.foundation.design_system.Dimens
import com.gpcasiapac.storesystems.foundation.design_system.GPCTheme

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun DraftBottomBar(
    count: Int,
    onDelete: () -> Unit,
    onView: () -> Unit,
    modifier: Modifier = Modifier,
) {

    Box(
        modifier = modifier.fillMaxWidth()
    ) {
        HorizontalFloatingToolbar(
            modifier = Modifier
                .clickable(true, onClick = onView)
                .align(Alignment.Center)
                .navigationBarsPadding()
                .padding(Dimens.Space.medium),
            expanded = true,
            colors = FloatingToolbarDefaults.standardFloatingToolbarColors(
                toolbarContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
            ),
            expandedShadowElevation = 5.dp,
            leadingContent = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                OutlinedIconButton(
                    modifier = Modifier.minimumInteractiveComponentSize()
                        .size(IconButtonDefaults.extraSmallContainerSize(IconButtonDefaults.IconButtonWidthOption.Uniform)),
                    onClick = onDelete
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Delete,
                        contentDescription = "Drafts"
                    )
                }
                Column(
                    modifier = Modifier.padding(horizontal = Dimens.Space.large),
                    //    verticalAlignment = Alignment.CenterVertically
                ) {

                    Text(
                        text = "Collection in progress",
                        style = MaterialTheme.typography.labelSmall,
                        //    color = MaterialTheme.colorScheme.onSurface
                    )
                    // Spacer(modifier = Modifier.width(Dimens.Space.small))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "${count}",
                            style = MaterialTheme.typography.titleLarge,
                            //   color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.width(Dimens.Space.small))
                        Text(
                            text = "order selected",
                            // style = MaterialTheme.typography.titleLarge,
                            //   color = MaterialTheme.colorScheme.onSurface
                        )

                    }
                }

                }
            },
//            trailingContent = {
//
//            }
        ) {
            FilledIconButton(
                modifier = Modifier.minimumInteractiveComponentSize()
                    .size(
                        IconButtonDefaults.smallContainerSize(
                            IconButtonDefaults.IconButtonWidthOption.Wide
                        )
                    ),
                //shapes = ,
                onClick = onView
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.ArrowForward,
                    contentDescription = "Drafts"
                )
            }
        }
    }
}


@Preview
@Composable
fun DraftBottomBarPreview() {
    GPCTheme {
        DraftBottomBar(
            count = 1,
            onDelete = {},
            onView = {},
        )
    }
}

