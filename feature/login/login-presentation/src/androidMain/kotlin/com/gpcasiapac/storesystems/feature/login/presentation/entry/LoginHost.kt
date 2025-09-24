package com.gpcasiapac.storesystems.feature.login.presentation.entry

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSavedStateNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.gpcasiapac.storesystems.feature.login.api.LoginFeatureEntry
import com.gpcasiapac.storesystems.feature.login.api.LoginOutcome
import com.gpcasiapac.storesystems.feature.login.presentation.navigation.LoginNavContract
import com.gpcasiapac.storesystems.feature.login.presentation.navigation.LoginNavigationViewModel
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun LoginHost(

    onComplete: () -> Unit,
) {

}
