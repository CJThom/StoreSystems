package com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderdetail

import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.viewModelScope
import com.gpcasiapac.storesystems.common.presentation.mvi.MVIViewModel
import com.gpcasiapac.storesystems.feature.collect.domain.model.CollectOrder
import com.gpcasiapac.storesystems.feature.collect.domain.model.CollectingType
import com.gpcasiapac.storesystems.feature.collect.domain.model.Representative
import com.gpcasiapac.storesystems.feature.collect.domain.repository.OrderQuery
import com.gpcasiapac.storesystems.feature.collect.domain.usecase.FetchOrderListUseCase
import com.gpcasiapac.storesystems.feature.collect.domain.usecase.ObserveOrderListUseCase
import com.gpcasiapac.storesystems.feature.collect.domain.usecase.selection.ObserveOrderSelectionUseCase
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderdetail.model.CollectOrderWithCustomerWithLineItemsState
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderlist.mapper.toState
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderlist.model.CollectOrderListItemState
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class OrderDetailScreenViewModel(
    private val fetchOrderListUseCase: FetchOrderListUseCase,
    private val observeOrderListUseCase: ObserveOrderListUseCase,
    private val observeOrderSelectionUseCase: ObserveOrderSelectionUseCase,
) : MVIViewModel<
        OrderDetailScreenContract.Event,
        OrderDetailScreenContract.State,
        OrderDetailScreenContract.Effect>() {

    override fun setInitialState(): OrderDetailScreenContract.State {
        return OrderDetailScreenContract.State(
            collectOrderWithCustomerWithLineItemsState = CollectOrderWithCustomerWithLineItemsState.placeholder(),
            collectOrderListItemStateList = listOf(CollectOrderListItemState.placeholder()),
            isMultiOrder = { viewState.value.collectOrderListItemStateList.count() > 1 },
            isLoading = false,
            error = null,
            collectingType = CollectingType.STANDARD,
            representativeSearchText = "",
            recentRepresentativeList = listOf(
                Representative("rep-1", "John Doe", "#9288180049912"),
                Representative("rep-2", "Custa Ma", "#9288180049912"),
                Representative("rep-3", "Alice Smith", "#9288180049912"),
            ),
            selectedRepresentativeIdList = emptySet(),
            courierName = "",
            signatureStrokes = emptyList(),
            emailChecked = true,
            printChecked = true,
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
        viewModelScope.launch { observeSelectionAndOrders() }
        // Kick off an initial fetch to populate data
        viewModelScope.launch { fetchOrders(successToast = "Orders loaded") }
    }

    // TABLE OF CONTENTS - All possible events handled here
    override fun handleEvents(event: OrderDetailScreenContract.Event) {
        when (event) {

            is OrderDetailScreenContract.Event.Refresh -> viewModelScope.launch {
                fetchOrders(successToast = "Orders refreshed")
            }

            is OrderDetailScreenContract.Event.ClearError -> {
                setState { copy(error = null) }
            }

            is OrderDetailScreenContract.Event.Back -> {
                setEffect { OrderDetailScreenContract.Effect.Outcome.Back }
            }

            // Collecting selector
            is OrderDetailScreenContract.Event.CollectingChanged -> {
                onCollectingChanged(event.type)
            }

            // Account flow
            is OrderDetailScreenContract.Event.RepresentativeSearchChanged -> {
                setState { copy(representativeSearchText = event.text) }
            }

            is OrderDetailScreenContract.Event.RepresentativeChecked -> {
                onRepresentativeChecked(event.representativeId, event.checked)
            }

            is OrderDetailScreenContract.Event.ClearRepresentativeSelection -> {
                setState { copy(selectedRepresentativeIdList = emptySet()) }
            }

            // Courier flow
            is OrderDetailScreenContract.Event.CourierNameChanged -> {
                setState { copy(courierName = event.text) }
            }

            is OrderDetailScreenContract.Event.ClearCourierName -> {
                setState { copy(courierName = "") }
            }

            // Signature
            is OrderDetailScreenContract.Event.Sign -> {
                sign()
            }

            is OrderDetailScreenContract.Event.SignatureSaved -> {
                onSignatureSaved(event.strokes)
            }

            is OrderDetailScreenContract.Event.ClearSignature -> {
                clearSignature()
            }

            // Correspondence
            is OrderDetailScreenContract.Event.ToggleEmail -> {
                setState { copy(emailChecked = event.checked) }
            }

            is OrderDetailScreenContract.Event.TogglePrint -> {
                setState { copy(printChecked = event.checked) }
            }

            is OrderDetailScreenContract.Event.EditEmail -> {
                setEffect { OrderDetailScreenContract.Effect.ShowToast("Edit email not implemented") }
            }

            is OrderDetailScreenContract.Event.EditPrinter -> {
                setEffect { OrderDetailScreenContract.Effect.ShowToast("Edit printer not implemented") }
            }

            // Final action
            is OrderDetailScreenContract.Event.Confirm -> {
                confirm()
            }
        }
    }

    private suspend fun observeSelectionAndOrders() {
        val selectionFlow = observeOrderSelectionUseCase()
        val orderListFlow = observeOrderListUseCase(OrderQuery(searchText = ""))
        combine(selectionFlow, orderListFlow) { selectedSet, orders -> selectedSet to orders }
            .collectLatest { (selectedSet, orders) ->
                when {
                    selectedSet.isEmpty() -> {
                        val default: CollectOrder? = orders.firstOrNull()
                        setState {
                            copy(
                                collectOrderListItemStateList = default?.let { listOf(it.toState()) }
                                    ?: emptyList(),
                                isLoading = false,
                                error = null
                            )
                        }
                    }
                    selectedSet.size == 1 -> {
                        val id = selectedSet.first()
                        val selected = orders.firstOrNull { it.id == id }
                        setState {
                            copy(
                                collectOrderListItemStateList = selected?.let { listOf(it.toState()) }
                                    ?: emptyList(),
                                isLoading = false,
                                error = null
                            )
                        }
                    }
                    else -> {
                        val selectedOrders = orders.filter { it.id in selectedSet }
                        setState {
                            copy(
                                collectOrderListItemStateList = selectedOrders.toState(),
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
                setEffect { OrderDetailScreenContract.Effect.ShowToast(successToast) }
            },
            onFailure = { t ->
                val msg = t.message ?: "Failed to refresh orders. Please try again."
                setState {
                    copy(
                        isLoading = false,
                        error = msg
                    )
                }
                setEffect { OrderDetailScreenContract.Effect.ShowError(msg) }
            }
        )
    }

    private fun onCollectingChanged(type: CollectingType) {
        val current = viewState.value
        if (current.collectingType == type) return
        setState {
            copy(
                collectingType = type,
                representativeSearchText = if (type == CollectingType.ACCOUNT) representativeSearchText else "",
                selectedRepresentativeIdList = if (type == CollectingType.ACCOUNT) selectedRepresentativeIdList else emptySet(),
                courierName = if (type == CollectingType.COURIER) courierName else "",
            )
        }
    }

    private fun onRepresentativeChecked(representativeId: String, checked: Boolean) {
        setState {
            val newSet = selectedRepresentativeIdList.toMutableSet()
            if (checked) newSet.add(representativeId) else newSet.remove(representativeId)
            copy(selectedRepresentativeIdList = newSet)
        }
    }

    private fun sign() {
        // Navigate to signature screen - this will be handled by navigation layer
        setEffect {
            OrderDetailScreenContract.Effect.Outcome.SignatureRequested
        }
    }

    private fun onSignatureSaved(strokes: List<List<Offset>>) {
        setState { copy(signatureStrokes = strokes) }
        setEffect { OrderDetailScreenContract.Effect.ShowToast("Signature saved") }
    }

    private fun clearSignature() {
        if (viewState.value.signatureStrokes.isNotEmpty()) {
            setState { copy(signatureStrokes = emptyList()) }
            setEffect { OrderDetailScreenContract.Effect.ShowToast("Signature cleared") }
        }
    }

    private fun confirm() {
        val s = viewState.value
        val hasOrders = s.collectOrderListItemStateList.isNotEmpty()
        if (!hasOrders) {
            setEffect { OrderDetailScreenContract.Effect.ShowError("No orders to confirm") }
            return
        }
        when (s.collectingType) {
            CollectingType.ACCOUNT -> {
                if (s.selectedRepresentativeIdList.isEmpty()) {
                    setEffect { OrderDetailScreenContract.Effect.ShowError("Please select at least one representative") }
                    return
                }
            }

            CollectingType.COURIER -> {
                if (s.courierName.isBlank()) {
                    setEffect { OrderDetailScreenContract.Effect.ShowError("Please enter the courier name") }
                    return
                }
            }

            CollectingType.STANDARD -> Unit
        }
        setEffect { OrderDetailScreenContract.Effect.Outcome.Confirmed }
    }

}
