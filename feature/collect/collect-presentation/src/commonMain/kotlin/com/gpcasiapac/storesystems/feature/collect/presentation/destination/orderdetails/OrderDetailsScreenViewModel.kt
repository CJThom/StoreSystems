package com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderdetails

import androidx.lifecycle.viewModelScope
import com.gpcasiapac.storesystems.common.feedback.haptic.HapticEffect
import com.gpcasiapac.storesystems.common.feedback.sound.SoundEffect
import com.gpcasiapac.storesystems.common.presentation.mvi.MVIViewModel
import com.gpcasiapac.storesystems.feature.collect.domain.usecase.FetchOrderListUseCase
import com.gpcasiapac.storesystems.feature.collect.domain.usecase.GetCollectOrderWithCustomerWithLineItemsFlowUseCase
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderlist.mapper.toState
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class OrderDetailsScreenViewModel(
    private val fetchOrderListUseCase: FetchOrderListUseCase,
    private val getCollectOrderWithCustomerWithLineItemsFlowUseCase: GetCollectOrderWithCustomerWithLineItemsFlowUseCase,
    private val setOrderSelectionUseCase: com.gpcasiapac.storesystems.feature.collect.domain.usecase.selection.SetOrderSelectionUseCase,
    private val checkOrderExistsUseCase: com.gpcasiapac.storesystems.feature.collect.domain.usecase.CheckOrderExistsUseCase,
    private val initialInvoice: String
) : MVIViewModel<
        OrderDetailsScreenContract.Event,
        OrderDetailsScreenContract.State,
        OrderDetailsScreenContract.Effect>() {

    private val invoiceKey = MutableStateFlow(initialInvoice.trim())

    override fun setInitialState(): OrderDetailsScreenContract.State {
        return OrderDetailsScreenContract.State(
            order = null,
            isLoading = true,
            error = null
        )
    }

    override fun onStart() {
        // Single reactive pipeline that switches the observed order when invoiceKey changes
        viewModelScope.launch {
            invoiceKey
                .map { it.trim() }
                .filter { it.isNotEmpty() }
                .distinctUntilChanged { a, b -> a.equals(b, ignoreCase = true) }
                .onEach { setState { copy(isLoading = true, error = null) } }
                .flatMapLatest { invoice ->
                    getCollectOrderWithCustomerWithLineItemsFlowUseCase(invoice)
                        .map { domain -> domain?.toState() }
                        .catch { e ->
                            val msg = e.message ?: "Failed to load order"
                            setState { copy(isLoading = false, error = msg) }
                            setEffect { OrderDetailsScreenContract.Effect.ShowError(msg) }
                            emit(null)
                        }
                }
                .collectLatest { orderState ->
                    if (orderState != null) {
                        setState { copy(order = orderState, isLoading = false, error = null) }
                    } else {
                        setState { copy(order = null, isLoading = false, error = "Order not found") }
                        setEffect { OrderDetailsScreenContract.Effect.ShowError("Order not found") }
                    }
                }
        }
    }

    override fun handleEvents(event: OrderDetailsScreenContract.Event) {
        when (event) {
            is OrderDetailsScreenContract.Event.Refresh -> {
                viewModelScope.launch { fetchOrders(successToast = "Orders refreshed") }
            }

            is OrderDetailsScreenContract.Event.Back -> {
                setEffect { OrderDetailsScreenContract.Effect.Outcome.Back }
            }

            is OrderDetailsScreenContract.Event.Select -> {
                viewModelScope.launch {
                    val current = invoiceKey.value
                    runCatching { setOrderSelectionUseCase(listOf(current), "mock") }
                        .onSuccess { setEffect { OrderDetailsScreenContract.Effect.Outcome.Selected(current) } }
                        .onFailure { t ->
                            val msg = t.message ?: "Failed to select order. Please try again."
                            setState { copy(error = msg) }
                            setEffect { OrderDetailsScreenContract.Effect.ShowError(msg) }
                        }
                }
            }

            is OrderDetailsScreenContract.Event.ScanInvoice -> {
                val invoice = event.invoiceNumber.trim()
                viewModelScope.launch {
                    when (val result = checkOrderExistsUseCase(invoice)) {
                        is com.gpcasiapac.storesystems.feature.collect.domain.usecase.CheckOrderExistsUseCase.UseCaseResult.Exists -> {
                            val target = result.invoiceNumber
                            if (!target.equals(invoiceKey.value, ignoreCase = true)) {
                                invoiceKey.value = target
                            }
                        }
                        is com.gpcasiapac.storesystems.feature.collect.domain.usecase.CheckOrderExistsUseCase.UseCaseResult.Error.NotFound -> {
                            setEffect { OrderDetailsScreenContract.Effect.PlayHaptic(HapticEffect.Error) }
                            setEffect { OrderDetailsScreenContract.Effect.PlaySound(SoundEffect.Error) }
                            setEffect { OrderDetailsScreenContract.Effect.ShowSnackbar(result.message) }
                        }
                        is com.gpcasiapac.storesystems.feature.collect.domain.usecase.CheckOrderExistsUseCase.UseCaseResult.Error.InvalidInput -> {
                            setEffect { OrderDetailsScreenContract.Effect.ShowSnackbar(result.message) }
                        }
                    }
                }
            }
        }
    }

    private suspend fun fetchOrders(successToast: String) {
        setState { copy(isLoading = true, error = null) }
        val result = fetchOrderListUseCase()
        result.fold(
            onSuccess = {
                setState { copy(isLoading = false) }
                setEffect { OrderDetailsScreenContract.Effect.ShowToast(successToast) }
            },
            onFailure = { t ->
                val msg = t.message ?: "Failed to refresh orders. Please try again."
                setState { copy(isLoading = false, error = msg) }
                setEffect { OrderDetailsScreenContract.Effect.ShowError(msg) }
            }
        )
    }
}