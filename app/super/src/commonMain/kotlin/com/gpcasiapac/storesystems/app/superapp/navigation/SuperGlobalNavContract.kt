package com.gpcasiapac.storesystems.app.superapp.navigation

import androidx.navigation3.runtime.NavKey
import com.gpcasiapac.storesystems.common.presentation.mvi.ViewEvent
import com.gpcasiapac.storesystems.common.presentation.mvi.ViewState
import com.gpcasiapac.storesystems.feature.collect.api.CollectOutcome
import com.gpcasiapac.storesystems.feature.login.api.LoginOutcome
import kotlinx.serialization.Serializable

@Serializable
sealed interface AppShellKey : NavKey {
    @Serializable
    data object TabsHost : AppShellKey
}

enum class NetworkStatus {
    Connected,
    Disconnected,
    Limited
}

object SuperGlobalNavContract {
    sealed interface Event : ViewEvent {
        // Shell domain (no bottom tabs)
        sealed interface Shell : Event {
            data class FromLogin(val outcome: LoginOutcome) : Shell
            data class Pop(val count: Int = 1) : Shell
            // Add more shell-scoped outcomes when needed (e.g., ProductDetails)
        }

        // Tabs host domain (inside TabsHost)
        sealed interface TabsHost : Event {
            data class SelectTab(val tab: TabItem) : TabsHost
            data class FromCollect(val outcome: CollectOutcome) : TabsHost

            // data class FromPicking(val outcome: PickingOutcome) : TabsHost
            // data class FromHistory(val outcome: HistoryOutcome) : TabsHost
            data class Pop(val tab: TabItem, val count: Int = 1) : TabsHost
        }

        // App-specific state management events
        data class SetCurrentUser(val userId: String?) : Event
        data class SetNetworkStatus(val status: NetworkStatus) : Event
        data class SetLastSyncTimestamp(val timestamp: Long?) : Event
        data class AddGlobalError(val featureId: String, val error: String) : Event
        data class ClearGlobalError(val featureId: String) : Event
        data class SetGlobalLoading(val featureId: String, val isLoading: Boolean) : Event
        data class SetOfflineMode(val isOffline: Boolean) : Event
    }

    data class SuperAppState(
        // Navigation stacks
        val appShellStack: List<NavKey>,
        val pickingStack: List<NavKey>,
        val collectStack: List<NavKey>,
        val historyStack: List<NavKey>,
        val tabList: List<TabItem>,
        val selectedTab: TabItem,

        // App-specific state
        val currentUserId: String? = null,
        val networkStatus: NetworkStatus = NetworkStatus.Connected,
        val lastSyncTimestamp: Long? = null,
        val globalErrors: Map<String, String> = emptyMap(),
        val globalLoadingStates: Set<String> = emptySet(),
        val isOfflineMode: Boolean = false
    ) : ViewState

    // Keep the old State as an alias for backward compatibility
    @Deprecated("Use SuperAppState instead", ReplaceWith("SuperAppState"))
    typealias State = SuperAppState
}