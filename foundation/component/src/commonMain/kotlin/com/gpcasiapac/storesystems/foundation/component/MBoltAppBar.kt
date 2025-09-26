package com.gpcasiapac.storesystems.foundation.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowLeft
import androidx.compose.material.icons.filled.ArrowLeft
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import com.gpcasiapac.storesystems.foundation.design_system.GPCTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MBoltAppBar(
    title: @Composable () -> Unit,
    actionBar: @Composable (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {},
    navigationIcon: @Composable (() -> Unit)? = null,
    scrollBehavior: TopAppBarScrollBehavior? = null,
) {
    Column {
        TopAppBar(
            actions = actions,
            navigationIcon = {
                navigationIcon?.invoke()
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primary,
                scrolledContainerColor = MaterialTheme.colorScheme.primary,
            ),
            scrollBehavior = scrollBehavior,
            title = {
                title()
            },

            )
        actionBar?.invoke()
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
            actionBar = {

            }, navigationIcon = {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
        )
    }
}