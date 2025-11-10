package com.gpcasiapac.storesystems.common.presentation.compose


data class DialogButton(
    val label: StringWrapper.Text, // TODO: Migrate to Resource
    val action: () -> Unit,
//  val icon: Int? = null // TODO: Add icon
)