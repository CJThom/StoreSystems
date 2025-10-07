package com.gpcasiapac.storesystems.foundation.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import com.gpcasiapac.storesystems.foundation.design_system.GPCTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

object MBoltAppBarDefaults {

    @Composable
    fun topAppBarColors(
        containerColor: Color = MaterialTheme.colorScheme.primary,
        scrolledContainerColor: Color = MaterialTheme.colorScheme.primary,
        navigationIconContentColor: Color = MaterialTheme.colorScheme.onPrimary,
        titleContentColor: Color = MaterialTheme.colorScheme.onPrimary,
        actionIconContentColor: Color = MaterialTheme.colorScheme.onPrimary,
        subtitleContentColor: Color = MaterialTheme.colorScheme.onPrimary,
    ): TopAppBarColors = TopAppBarDefaults.topAppBarColors(
        containerColor = containerColor,
        navigationIconContentColor = navigationIconContentColor,
        scrolledContainerColor = scrolledContainerColor,
        titleContentColor = titleContentColor,
        actionIconContentColor = actionIconContentColor,
        subtitleContentColor = subtitleContentColor
    )

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MBoltAppBar(
    title: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    navigationIcon: @Composable () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {},
    expandedHeight: Dp = TopAppBarDefaults.TopAppBarExpandedHeight,
    windowInsets: WindowInsets = TopAppBarDefaults.windowInsets,
    colors: TopAppBarColors = MBoltAppBarDefaults.topAppBarColors(),
    scrollBehavior: TopAppBarScrollBehavior? = null,
) {
    TopAppBar(
        title = title,
        modifier = modifier,
        actions = actions,
        navigationIcon = navigationIcon,
        colors = colors,
        expandedHeight = expandedHeight,
        windowInsets = windowInsets,
        scrollBehavior = scrollBehavior,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MBoltAppBar(
    title: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    navigationIcon: @Composable () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {},
    secondaryAppBar: @Composable (() -> Unit)? = null,
    expandedHeight: Dp = TopAppBarDefaults.TopAppBarExpandedHeight,
    windowInsets: WindowInsets = TopAppBarDefaults.windowInsets,
    colors: TopAppBarColors = MBoltAppBarDefaults.topAppBarColors(),
    scrollBehavior: TopAppBarScrollBehavior? = null,
) {
    Column {
        MBoltAppBar(
            title = title,
            modifier = modifier,
            actions = actions,
            navigationIcon = navigationIcon,
            colors = colors,
            expandedHeight = expandedHeight,
            windowInsets = windowInsets,
            scrollBehavior = scrollBehavior
        )
        secondaryAppBar?.invoke()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun MBoltAppBarPreview() {
    GPCTheme {
        MBoltAppBar(
            title = {
                GPCLogoTitle("Store Systems")
            },
            secondaryAppBar = {

            }, navigationIcon = {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
        )
    }
}