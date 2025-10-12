package com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderfulfillment

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BusinessCenter
import androidx.compose.material.icons.outlined.LocalShipping
import androidx.compose.material.icons.outlined.Person
import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.viewModelScope
import com.gpcasiapac.storesystems.common.presentation.mvi.MVIViewModel
import com.gpcasiapac.storesystems.feature.collect.domain.model.CollectingType
import com.gpcasiapac.storesystems.feature.collect.domain.model.OrderSelectionResult
import com.gpcasiapac.storesystems.feature.collect.domain.model.Representative
import com.gpcasiapac.storesystems.feature.collect.domain.usecase.FetchOrderListUseCase
import com.gpcasiapac.storesystems.feature.collect.domain.usecase.ObserveOrderSelectionResultUseCase
import com.gpcasiapac.storesystems.feature.collect.presentation.component.CollectionTypeSectionDisplayState
import com.gpcasiapac.storesystems.feature.collect.presentation.components.CorrespondenceItemDisplayParam
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderfulfillment.model.CollectOrderWithCustomerWithLineItemsState
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderlist.mapper.toListItemState
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderlist.mapper.toState
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderlist.model.CollectOrderListItemState
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

private const val COLLAPSED_PRODUCT_LIST_COUNT = 2

class OrderFulfilmentScreenViewModel(
    private val fetchOrderListUseCase: FetchOrderListUseCase,
    private val observeOrderSelectionResultUseCase: ObserveOrderSelectionResultUseCase,
) : MVIViewModel<
        OrderFulfilmentScreenContract.Event,
        OrderFulfilmentScreenContract.State,
        OrderFulfilmentScreenContract.Effect>() {

    override fun setInitialState(): OrderFulfilmentScreenContract.State {
        return OrderFulfilmentScreenContract.State(
            collectOrderWithCustomerWithLineItemsState = CollectOrderWithCustomerWithLineItemsState.placeholder(),
            collectOrderListItemStateList = listOf(CollectOrderListItemState.placeholder()),
            isLoading = false,
            error = null,
            collectingType = CollectingType.STANDARD,
            collectionTypeOptionList = listOf(
                CollectionTypeSectionDisplayState(
                    enabled = true,
                    collectingType = CollectingType.STANDARD,
                    icon = Icons.Outlined.Person,
                    label = CollectingType.STANDARD.name,
                ), CollectionTypeSectionDisplayState(
                    enabled = true,
                    collectingType = CollectingType.ACCOUNT,
                    icon = Icons.Outlined.BusinessCenter,
                    label = CollectingType.ACCOUNT.name,
                ), CollectionTypeSectionDisplayState(
                    enabled = true,
                    collectingType = CollectingType.COURIER,
                    icon = Icons.Outlined.LocalShipping,
                    label = CollectingType.COURIER.name,
                )
            ),
            isAccountCollectingFeatureEnabled = true, // Feature Toggle
            representativeSearchQuery = "",
            representativeList = listOf(
                Representative("rep-1", "John Doe", "#9288180049912"),
                Representative("rep-2", "Custa Ma", "#9288180049912"),
                Representative("rep-3", "Alice Smith", "#9288180049912"),
            ),
            selectedRepresentativeIds = emptySet(),
            courierName = "",
            signatureStrokes = emptyList(),
            isCorrespondenceSectionVisible = false,
            correspondenceOptionList = listOf(
                CorrespondenceItemDisplayParam(
                    id = "email",
                    type = "Email",
                    detail = "Send email to customer",
                    isEnabled = true
                ),
                CorrespondenceItemDisplayParam(
                    id = "print",
                    type = "Print",
                    detail = "Send invoice to printer",
                    isEnabled = true
                )
            ),
            visibleProductListItemCount = COLLAPSED_PRODUCT_LIST_COUNT
        )

    }
    override suspend fun awaitReadiness(): Boolean {
        // This placeholder screen has no special readiness requirement
        return true
    }

    override fun handleReadinessFailed() {
        // Not applicable in this placeholder
    }

    override fun onStart() {
        // Observe selection from repository and current order list to render single vs multi
        viewModelScope.launch {
            observeOrderSelectionResult()
        }
    }

    // TABLE OF CONTENTS - All possible events handled here
    override fun handleEvents(event: OrderFulfilmentScreenContract.Event) {
        when (event) {

            is OrderFulfilmentScreenContract.Event.Refresh -> {
                viewModelScope.launch {
                    fetchOrders(successToast = "Orders refreshed")
                }
            }

            is OrderFulfilmentScreenContract.Event.ClearError -> {
                setState { copy(error = null) }
            }

            is OrderFulfilmentScreenContract.Event.Back -> {
                setEffect { OrderFulfilmentScreenContract.Effect.Outcome.Back }
            }

            // Collecting selector
            is OrderFulfilmentScreenContract.Event.CollectingChanged -> {
                onCollectingChanged(event.type)
            }

            // Account flow
            is OrderFulfilmentScreenContract.Event.RepresentativeSearchQueryChanged -> {
                setState { copy(representativeSearchQuery = event.query) }
            }

            is OrderFulfilmentScreenContract.Event.RepresentativeSelected -> {
                onRepresentativeChecked(event.id, event.isSelected)
            }

            is OrderFulfilmentScreenContract.Event.ClearRepresentativeSelection -> {
                setState { copy(selectedRepresentativeIds = emptySet()) }
            }

            // Courier flow
            is OrderFulfilmentScreenContract.Event.CourierNameChanged -> {
                setState { copy(courierName = event.text) }
            }

            is OrderFulfilmentScreenContract.Event.ClearCourierName -> {
                setState { copy(courierName = "") }
            }

            // Signature
            is OrderFulfilmentScreenContract.Event.Sign -> {
                sign()
            }

            is OrderFulfilmentScreenContract.Event.SignatureSaved -> {
                onSignatureSaved(event.strokes)
            }

            is OrderFulfilmentScreenContract.Event.ClearSignature -> {
                clearSignature()
            }

            // Correspondence
            is OrderFulfilmentScreenContract.Event.ToggleCorrespondence -> {
                onCorrespondenceToggled(event.id)
            }

            is OrderFulfilmentScreenContract.Event.EditCorrespondence -> {
                setEffect { OrderFulfilmentScreenContract.Effect.ShowToast("Edit ${event.id} not implemented") }
            }

            // Final action
            is OrderFulfilmentScreenContract.Event.Confirm -> {
                confirm()
            }
            is OrderFulfilmentScreenContract.Event.ToggleProductListExpansion -> {
                toggleProductListExpansion()
            }
            is OrderFulfilmentScreenContract.Event.OrderClicked -> {
                setEffect { OrderFulfilmentScreenContract.Effect.Outcome.NavigateToOrderDetails(event.invoiceNumber) }
            }
        }
    }

    private fun onCorrespondenceToggled(id: String) {
        setState {
            val updatedOptions = correspondenceOptionList.map {
                if (it.id == id) {
                    it.copy(isEnabled = !it.isEnabled)
                } else {
                    it
                }
            }
            copy(correspondenceOptionList = updatedOptions)
        }
    }

    private fun toggleProductListExpansion() {
        val totalItemCount = viewState.value.collectOrderWithCustomerWithLineItemsState?.lineItemList?.size ?: 0
        setState {
            copy(
                visibleProductListItemCount = if (visibleProductListItemCount == totalItemCount) {
                    COLLAPSED_PRODUCT_LIST_COUNT
                } else {
                    totalItemCount
                }
            )
        }
    }

    private suspend fun observeOrderSelectionResult() {
        observeOrderSelectionResultUseCase().collectLatest { result ->
            when (result) {
                is OrderSelectionResult.Single -> {
                    val orderState = result.order?.toState()
                    setState {
                        copy(
                            collectOrderWithCustomerWithLineItemsState = orderState,
                            collectOrderListItemStateList = emptyList(),
                            isLoading = false,
                            error = null,
                            visibleProductListItemCount = orderState?.lineItemList?.size?.coerceAtMost(COLLAPSED_PRODUCT_LIST_COUNT) ?: 0
                        )
                    }
                }
                is OrderSelectionResult.Multi -> {
                    setState {
                        copy(
                            collectOrderWithCustomerWithLineItemsState = null,
                            collectOrderListItemStateList = result.orderList.toListItemState(),
                            isLoading = false,
                            error = null
                        )
                    }
                }
            }
        }
    }

    private suspend fun fetchOrders(successToast: String) {
        setState {
            copy(
                isLoading = true,
                error = null
            )
        }
        val result = fetchOrderListUseCase()
        result.fold(
            onSuccess = {
                setState { copy(isLoading = false) }
                setEffect { OrderFulfilmentScreenContract.Effect.ShowToast(successToast) }
            },
            onFailure = { t ->
                val msg = t.message ?: "Failed to refresh orders. Please try again."
                setState {
                    copy(
                        isLoading = false,
                        error = msg
                    )
                }
                setEffect { OrderFulfilmentScreenContract.Effect.ShowError(msg) }
            }
        )
    }

    private fun onCollectingChanged(type: CollectingType) {
        val current = viewState.value
        if (current.collectingType == type) return
        setState {
            copy(
                collectingType = type,
                representativeSearchQuery = if (type == CollectingType.ACCOUNT) representativeSearchQuery else "",
                selectedRepresentativeIds = if (type == CollectingType.ACCOUNT) selectedRepresentativeIds else emptySet(),
                courierName = if (type == CollectingType.COURIER) courierName else "",
            )
        }
    }

    private fun onRepresentativeChecked(representativeId: String, isSelected: Boolean) {
        setState {
            val newSet = selectedRepresentativeIds.toMutableSet()
            if (isSelected) newSet.add(representativeId) else newSet.remove(representativeId)
            copy(selectedRepresentativeIds = newSet)
        }
    }

    private fun sign() {
        // Navigate to signature screen - this will be handled by navigation layer
        setEffect {
            OrderFulfilmentScreenContract.Effect.Outcome.SignatureRequested
        }
    }

    private fun onSignatureSaved(strokes: List<List<Offset>>) {
        setState { copy(signatureStrokes = strokes) }
        setEffect { OrderFulfilmentScreenContract.Effect.ShowToast("Signature saved") }
    }

    private fun clearSignature() {
        if (viewState.value.signatureStrokes.isNotEmpty()) {
            setState { copy(signatureStrokes = emptyList()) }
            setEffect { OrderFulfilmentScreenContract.Effect.ShowToast("Signature cleared") }
        }
    }

    private fun confirm() {
        val s = viewState.value
        val hasOrders = s.collectOrderListItemStateList.isNotEmpty()
        if (!hasOrders) {
            setEffect { OrderFulfilmentScreenContract.Effect.ShowError("No orders to confirm") }
            return
        }
        when (s.collectingType) {
            CollectingType.ACCOUNT -> {
                if (s.selectedRepresentativeIds.isEmpty()) {
                    setEffect { OrderFulfilmentScreenContract.Effect.ShowError("Please select at least one representative") }
                    return
                }
            }

            CollectingType.COURIER -> {
                if (s.courierName.isBlank()) {
                    setEffect { OrderFulfilmentScreenContract.Effect.ShowError("Please enter the courier name") }
                    return
                }
            }

            CollectingType.STANDARD -> Unit
        }
        setEffect { OrderFulfilmentScreenContract.Effect.Outcome.Confirmed }
    }

}