package com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderfulfillment

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BusinessCenter
import androidx.compose.material.icons.outlined.LocalShipping
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.SnackbarDuration
import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.viewModelScope
import com.gpcasiapac.storesystems.common.feedback.haptic.HapticEffect
import com.gpcasiapac.storesystems.common.feedback.sound.SoundEffect
import com.gpcasiapac.storesystems.common.presentation.mvi.MVIViewModel
import com.gpcasiapac.storesystems.feature.collect.domain.model.CollectingType
import com.gpcasiapac.storesystems.feature.collect.domain.model.Representative
import com.gpcasiapac.storesystems.feature.collect.domain.usecase.CheckOrderExistsUseCase
import com.gpcasiapac.storesystems.feature.collect.domain.usecase.FetchOrderListUseCase
import com.gpcasiapac.storesystems.feature.collect.domain.usecase.ObserveLatestOpenWorkOrderUseCase
import com.gpcasiapac.storesystems.feature.collect.domain.usecase.ObserveLatestOpenWorkOrderIdUseCase
import com.gpcasiapac.storesystems.feature.collect.domain.usecase.ObserveWorkOrderItemsInScanOrderUseCase
import com.gpcasiapac.storesystems.feature.collect.domain.usecase.selection.RemoveOrderSelectionUseCase
import com.gpcasiapac.storesystems.feature.collect.presentation.component.CollectionTypeSectionDisplayState
import com.gpcasiapac.storesystems.feature.collect.presentation.components.CorrespondenceItemDisplayParam
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderlist.mapper.toListItemState
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderlist.model.CollectOrderListItemState
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import com.gpcasiapac.storesystems.feature.collect.domain.model.CollectOrderWithCustomer
import com.gpcasiapac.storesystems.feature.collect.domain.usecase.SetWorkOrderCollectingTypeUseCase
import com.gpcasiapac.storesystems.feature.collect.domain.usecase.SetWorkOrderCourierNameUseCase
import com.gpcasiapac.storesystems.feature.collect.domain.usecase.selection.AddOrderSelectionUseCase
import com.gpcasiapac.storesystems.feature.collect.presentation.util.Debouncer


class OrderFulfilmentScreenViewModel(
    private val fetchOrderListUseCase: FetchOrderListUseCase,
    private val removeOrderSelectionUseCase: RemoveOrderSelectionUseCase,
    private val observeLatestOpenWorkOrderUseCase: ObserveLatestOpenWorkOrderUseCase,
    private val observeLatestOpenWorkOrderIdUseCase: ObserveLatestOpenWorkOrderIdUseCase,
    private val observeWorkOrderItemsInScanOrderUseCase: ObserveWorkOrderItemsInScanOrderUseCase,
    private val setWorkOrderCollectingTypeUseCase: SetWorkOrderCollectingTypeUseCase,
    private val setWorkOrderCourierNameUseCase: SetWorkOrderCourierNameUseCase,
    private val addOrderSelectionUseCase: AddOrderSelectionUseCase,
    private val checkOrderExistsUseCase: CheckOrderExistsUseCase,
) : MVIViewModel<
        OrderFulfilmentScreenContract.Event,
        OrderFulfilmentScreenContract.State,
        OrderFulfilmentScreenContract.Effect>() {

    private val userRefId = "mock"

    // Shared keyed debouncer for persisting user edits
    private val debouncer = Debouncer(viewModelScope)

    override fun setInitialState(): OrderFulfilmentScreenContract.State {
        return OrderFulfilmentScreenContract.State(
            collectOrderListItemStateList = listOf(CollectOrderListItemState.placeholder()),
            isLoading = false,
            error = null,
            featureFlags = OrderFulfilmentScreenContract.State.FeatureFlags(
                isAccountCollectingFeatureEnabled = false,
                isCorrespondenceSectionVisible = false,
            ),
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
            representativeSearchQuery = "",
            representativeList = listOf(
                Representative("rep-1", "John Doe", "#9288180049912"),
                Representative("rep-2", "Custa Ma", "#9288180049912"),
                Representative("rep-3", "Alice Smith", "#9288180049912"),
            ),
            selectedRepresentativeIds = emptySet(),
            courierName = "",
            signatureStrokes = emptyList(),
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
            )
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
        // Observe header (latest open Work Order entity)
        viewModelScope.launch {
            observeLatestOpenWorkOrderUseCase(userRefId).collectLatest { wo ->
                setState {
                    copy(
                        signatureBase64 = wo?.signature,
                        collectingType = wo?.collectingType ?: CollectingType.STANDARD,
                        courierName = wo?.courierName ?: "",
                        isLoading = false,
                        error = null
                    )
                }
            }
        }
        // Observe ordered items for latest open Work Order
        viewModelScope.launch {
            observeLatestOpenWorkOrderIdUseCase(userRefId)
                .flatMapLatest { id ->
                    if (id == null) flowOf<List<CollectOrderWithCustomer>>(emptyList())
                    else observeWorkOrderItemsInScanOrderUseCase(id)
                }
                .collectLatest { items ->
                    setState { copy(collectOrderListItemStateList = items.toListItemState()) }
                }
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
                val s = viewState.value
                val isMulti = s.collectOrderListItemStateList.size > 1
                val hasSignature = s.signatureStrokes.isNotEmpty()
                val hasProgress = isMulti || hasSignature
                if (hasProgress) {
                    setEffect { OrderFulfilmentScreenContract.Effect.ShowSaveDiscardDialog() }
                } else {
                    setEffect { OrderFulfilmentScreenContract.Effect.Outcome.Back }
                }
            }

            is OrderFulfilmentScreenContract.Event.ConfirmBackSave -> {
                setEffect { OrderFulfilmentScreenContract.Effect.Outcome.SaveAndExit }
            }

            is OrderFulfilmentScreenContract.Event.ConfirmBackDiscard -> {
                setEffect { OrderFulfilmentScreenContract.Effect.Outcome.DiscardAndExit }
            }

            is OrderFulfilmentScreenContract.Event.CancelBackDialog -> {
                // no-op; UI should simply dismiss the dialog
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
                // Optimistic UI update
                setState { copy(courierName = event.text) }

                // Debounced persistence using screen preset
                debouncer.submit(OrderFulfilmentScreenContract.Debounce.CourierName) {
                    val s = viewState.value
                    if (s.collectingType == CollectingType.COURIER) {
                        setWorkOrderCourierNameUseCase(userRefId, s.courierName.trim())
                            .onFailure { /* avoid spamming errors for text input */ }
                    }
                }
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
                setEffect { OrderFulfilmentScreenContract.Effect.ShowSnackbar("Edit ${event.id} not implemented") }
            }

            // Final action
            is OrderFulfilmentScreenContract.Event.Confirm -> {
                confirm()
            }

            // Search-origin selection confirmation
            is OrderFulfilmentScreenContract.Event.ConfirmSearchSelection -> {
                setEffect { OrderFulfilmentScreenContract.Effect.ShowConfirmSelectionDialog() }
            }

            is OrderFulfilmentScreenContract.Event.ConfirmSearchSelectionProceed -> {
                // No-op; SearchViewModel will handle persistence and collapse
            }

            is OrderFulfilmentScreenContract.Event.DismissConfirmSearchSelectionDialog -> {
                // No-op; UI dismisses dialog
            }

            is OrderFulfilmentScreenContract.Event.OrderClicked -> {
                setEffect {
                    OrderFulfilmentScreenContract.Effect.Outcome.NavigateToOrderDetails(
                        event.invoiceNumber
                    )
                }
            }

            is OrderFulfilmentScreenContract.Event.ScanInvoice -> {
                val invoice = event.invoiceNumber.trim()
                // Collapse search on scan (handled by UI via effect)
                setEffect { OrderFulfilmentScreenContract.Effect.CollapseSearchBar }
                viewModelScope.launch {
                    when (val result = checkOrderExistsUseCase(invoice)) {
                        is CheckOrderExistsUseCase.UseCaseResult.Exists -> {
                            if (event.autoSelect) {
                                val addResult = addOrderSelectionUseCase(result.invoiceNumber, userRefId)
                                when (addResult) {
                                    is com.gpcasiapac.storesystems.feature.collect.domain.usecase.selection.AddOrderSelectionUseCase.UseCaseResult.Added -> {
                                        setEffect { OrderFulfilmentScreenContract.Effect.PlayHaptic(HapticEffect.Success) }
                                        setEffect { OrderFulfilmentScreenContract.Effect.PlaySound(SoundEffect.Success) }
                                    }
                                    is com.gpcasiapac.storesystems.feature.collect.domain.usecase.selection.AddOrderSelectionUseCase.UseCaseResult.Duplicate -> {
                                        setEffect { OrderFulfilmentScreenContract.Effect.PlayHaptic(HapticEffect.SelectionChanged) }
                                        setEffect { OrderFulfilmentScreenContract.Effect.PlaySound(SoundEffect.Warning) }
                                        setEffect { OrderFulfilmentScreenContract.Effect.ShowSnackbar("Order already added: \"${addResult.invoiceNumber}\"") }
                                    }
                                    is com.gpcasiapac.storesystems.feature.collect.domain.usecase.selection.AddOrderSelectionUseCase.UseCaseResult.Error -> {
                                        setEffect { OrderFulfilmentScreenContract.Effect.PlayHaptic(HapticEffect.Error) }
                                        setEffect { OrderFulfilmentScreenContract.Effect.PlaySound(SoundEffect.Error) }
                                        setEffect { OrderFulfilmentScreenContract.Effect.ShowSnackbar(addResult.message) }
                                    }
                                }
                            } else {
                                setEffect {
                                    OrderFulfilmentScreenContract.Effect.Outcome.NavigateToOrderDetails(
                                        result.invoiceNumber
                                    )
                                }
                            }
                        }
                        is CheckOrderExistsUseCase.UseCaseResult.Error -> {
                            setEffect { OrderFulfilmentScreenContract.Effect.PlayHaptic(HapticEffect.Error) }
                            setEffect { OrderFulfilmentScreenContract.Effect.PlaySound(SoundEffect.Error) }
                            setEffect { OrderFulfilmentScreenContract.Effect.ShowSnackbar(result.message) }
                        }
                    }
                }
            }

            is OrderFulfilmentScreenContract.Event.DeselectOrder -> {
                viewModelScope.launch {
                    removeOrderSelectionUseCase(event.invoiceNumber, userRefId)
                }
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
                setEffect { OrderFulfilmentScreenContract.Effect.ShowSnackbar(successToast) }
            },
            onFailure = { t ->
                val msg = t.message ?: "Failed to refresh orders. Please try again."
                setState {
                    copy(
                        isLoading = false,
                        error = msg
                    )
                }
                setEffect { OrderFulfilmentScreenContract.Effect.ShowSnackbar(msg, duration = SnackbarDuration.Long) }
            }
        )
    }

    private fun onCollectingChanged(type: CollectingType) {
        val current = viewState.value
        if (current.collectingType == type) return

        // Optimistic UI update
        setState {
            copy(
                collectingType = type,
                representativeSearchQuery = if (type == CollectingType.ACCOUNT) representativeSearchQuery else "",
                selectedRepresentativeIds = if (type == CollectingType.ACCOUNT) selectedRepresentativeIds else emptySet(),
                courierName = if (type == CollectingType.COURIER) courierName else "",
            )
        }

        // Debounced DB update to keep Work Order as source of truth
        debouncer.submit(
            OrderFulfilmentScreenContract.Debounce.CollectingType
        ) {
            setWorkOrderCollectingTypeUseCase(userRefId, type)
                .onFailure { t ->
                    setEffect { OrderFulfilmentScreenContract.Effect.ShowSnackbar(t.message ?: "Failed to save collecting type", duration = SnackbarDuration.Long) }
                }
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
        setEffect { OrderFulfilmentScreenContract.Effect.ShowSnackbar("Signature saved") }
    }

    private fun clearSignature() {
        if (viewState.value.signatureStrokes.isNotEmpty()) {
            setState { copy(signatureStrokes = emptyList()) }
            setEffect { OrderFulfilmentScreenContract.Effect.ShowSnackbar("Signature cleared") }
        }
    }

    private fun confirm() {
        val s = viewState.value
        val hasOrders = s.collectOrderListItemStateList.isNotEmpty()
        if (!hasOrders) {
            setEffect { OrderFulfilmentScreenContract.Effect.ShowSnackbar("No orders to confirm", duration = SnackbarDuration.Long) }
            return
        }
        when (s.collectingType) {
            CollectingType.ACCOUNT -> {
                if (s.selectedRepresentativeIds.isEmpty()) {
                    setEffect { OrderFulfilmentScreenContract.Effect.ShowSnackbar("Please select at least one representative", duration = SnackbarDuration.Long) }
                    return
                }
            }

            CollectingType.COURIER -> {
                if (s.courierName.isBlank()) {
                    setEffect { OrderFulfilmentScreenContract.Effect.ShowSnackbar("Please enter the courier name", duration = SnackbarDuration.Long) }
                    return
                }
            }

            CollectingType.STANDARD -> Unit
        }
        setEffect { OrderFulfilmentScreenContract.Effect.Outcome.Confirmed }
    }

}
