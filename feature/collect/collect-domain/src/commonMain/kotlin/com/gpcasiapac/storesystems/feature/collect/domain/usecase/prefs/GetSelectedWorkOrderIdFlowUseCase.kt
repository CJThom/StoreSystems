//package com.gpcasiapac.storesystems.feature.collect.domain.usecase.prefs
//
//import com.gpcasiapac.storesystems.core.preferences.api.AppId
//import com.gpcasiapac.storesystems.core.preferences.api.PreferenceScope
//import com.gpcasiapac.storesystems.core.preferences.api.PreferencesRepository
//import com.gpcasiapac.storesystems.core.preferences.api.Principal
//import com.gpcasiapac.storesystems.feature.collect.domain.pref.CollectFeatures
//import com.gpcasiapac.storesystems.feature.collect.domain.pref.CollectPrefKeys
//import kotlinx.coroutines.flow.Flow
//
//class GetSelectedWorkOrderIdFlowUseCase(
//    private val prefs: PreferencesRepository,
//) {
//    operator fun invoke(userId: String?): Flow<String?> {
//        val principal = userId?.let { Principal.User(it) }
//        return prefs.flow(
//            key = CollectPrefKeys.SelectedWorkOrderId,
//            principal = principal,
//            scope = PreferenceScope.Feature(appId = AppId.Collect, feature = CollectFeatures.Orders)
//        )
//    }
//}
