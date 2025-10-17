package com.gpcasiapac.storesystems.feature.collect.presentation.destination.signature

import androidx.compose.runtime.Immutable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.ImageBitmap
import com.gpcasiapac.storesystems.common.presentation.mvi.ViewEvent
import com.gpcasiapac.storesystems.common.presentation.mvi.ViewSideEffect
import com.gpcasiapac.storesystems.common.presentation.mvi.ViewState

object SignatureScreenContract {

    @Immutable
    data class State(
        val isLoading: Boolean,
        val isSigned: Boolean,
        val error: String?,
        val signatureStrokes: List<List<Offset>>,
        val signatureBitmap: ImageBitmap? = null
    ) : ViewState

    sealed interface Event : ViewEvent {
        data object StartCapture : Event
        data class StrokesChanged(val strokes: List<List<Offset>>) : Event
        data object ClearSignature : Event
        data class SignatureCompleted(val signatureBitmap: ImageBitmap) : Event
        data object ClearError : Event
        data object Back : Event
    }

    sealed interface Effect : ViewSideEffect {
        data class ShowToast(val message: String) : Effect
        data class ShowError(val error: String) : Effect

        sealed interface Outcome : Effect {
            data object Back : Outcome
            data class SignatureSaved(val strokes: List<List<Offset>>) : Outcome
        }
    }
}
