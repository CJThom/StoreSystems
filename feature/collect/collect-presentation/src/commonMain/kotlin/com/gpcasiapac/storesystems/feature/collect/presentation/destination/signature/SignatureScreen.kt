package com.gpcasiapac.storesystems.feature.collect.presentation.destination.signature

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.ui.unit.dp
import androidx.window.core.layout.WindowSizeClass
import com.gpcasiapac.storesystems.feature.collect.domain.model.CustomerType
import com.gpcasiapac.storesystems.feature.collect.presentation.components.CustomerDetails
import com.gpcasiapac.storesystems.feature.collect.presentation.components.DrawCanvas
import com.gpcasiapac.storesystems.feature.collect.presentation.components.UniformAspectBox
import com.gpcasiapac.storesystems.foundation.component.MBoltAppBar
import com.gpcasiapac.storesystems.foundation.component.TopBarTitle
import com.gpcasiapac.storesystems.foundation.design_system.Dimens
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignatureScreen(
    state: SignatureScreenContract.State,
    onEventSent: (event: SignatureScreenContract.Event) -> Unit,
    effectFlow: Flow<SignatureScreenContract.Effect>,
    onOutcome: (outcome: SignatureScreenContract.Effect.Outcome) -> Unit,
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scrollState = rememberScrollState()

    val adaptiveInfo = currentWindowAdaptiveInfo()
    val isLandscape =
        adaptiveInfo.windowSizeClass.minHeightDp < WindowSizeClass.HEIGHT_DP_MEDIUM_LOWER_BOUND

    LaunchedEffect(effectFlow) {
        effectFlow.collectLatest { effect ->
            when (effect) {
                is SignatureScreenContract.Effect.ShowToast ->
                    snackbarHostState.showSnackbar(
                        effect.message,
                        duration = SnackbarDuration.Short
                    )

                is SignatureScreenContract.Effect.ShowError ->
                    snackbarHostState.showSnackbar(effect.error, duration = SnackbarDuration.Long)

                is SignatureScreenContract.Effect.Outcome -> onOutcome(effect)
            }
        }
    }

    Scaffold(
        topBar = {
            MBoltAppBar(
                title = {
                    TopBarTitle("Signature")
                },
                navigationIcon = {
                    IconButton(onClick = {
                        onEventSent(SignatureScreenContract.Event.Back)
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
        ) {
            // Content with orientation-aware layout
            if (isLandscape) {
                // Landscape: Row-based layout
                Row(
                    modifier = Modifier
                        .fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(Dimens.Space.medium)
                ) {
                    // Left side content (Customer details + controls)
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = Dimens.Space.medium),
                        verticalArrangement = Arrangement.spacedBy(Dimens.Space.medium)
                    ) {
                        // Customer Details Card
                        CustomerDetails(
                            customerName = "Customer Name",
                            customerNumber = "Customer Number",
                            phoneNumber = "Phone Number",
                            customerType = CustomerType.B2C
                        )
                        HorizontalDivider()
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Text(
                                "Please sign here",
                                style = MaterialTheme.typography.titleLarge
                            )
                            OutlinedButton(
                                enabled = state.signatureStrokes.isNotEmpty(),
                                onClick = {
                                    onEventSent(SignatureScreenContract.Event.ClearSignature)
                                }
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.Close, contentDescription = "Clear")
                                    Text("Clear All")
                                }
                            }
                        }

                        // Confirm Button
                        Button(
                            onClick = {
                                if (state.signatureBitmap != null) {
                                    onEventSent(SignatureScreenContract.Event.StartCapture)
                                }
                            },
                            enabled = !state.isLoading && state.signatureBitmap != null,
                            modifier = Modifier.fillMaxWidth(),
                            shape = MaterialTheme.shapes.small
                        ) {
                            if (state.isLoading) {
                                CircularProgressIndicator(
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                            } else {
                                Text(
                                    text = "SAVE SIGNATURE",
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }

                    // Right side: DrawCanvas with 16:9 ratio
                    UniformAspectBox { w, h ->
                        DrawCanvas(
                            onComplete = { image ->
                                onEventSent(SignatureScreenContract.Event.SignatureCompleted(image))
                            },
                            modifier = Modifier
                                .width(w)
                                .height(h),
//                                .width(380.dp)
//                                .height(214.dp),
                            strokes = state.signatureStrokes,
                            onStrokesChange = { strokes ->
                                onEventSent(SignatureScreenContract.Event.StrokesChanged(strokes))
                            },
                            strokeColor = Color.Black,
                        )
                    }
                }
            } else {
                // Portrait: Column-based layout (original)
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(Dimens.Space.medium)
                ) {
                    // Customer Details Card
                    CustomerDetails(
                        customerName = "Customer Name",
                        customerNumber = "Customer Number",
                        phoneNumber = "Phone Number",
                        customerType = CustomerType.B2C
                    )
                    HorizontalDivider()
                    Row(
                        modifier = Modifier.fillMaxWidth()
                            .padding(horizontal = Dimens.Space.medium),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text(
                            "Please sign here",
                            style = MaterialTheme.typography.titleLarge
                        )
                        OutlinedButton(
                            enabled = state.signatureStrokes.isNotEmpty(),
                            onClick = {
                                onEventSent(SignatureScreenContract.Event.ClearSignature)
                            }
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Close, contentDescription = "Clear")
                                Text("Clear All")
                            }
                        }
                    }


                    // DrawCanvas with 16:9 fixed dimensions
                    UniformAspectBox { w, h ->
                        DrawCanvas(
                            onComplete = { image ->
                                onEventSent(
                                    SignatureScreenContract.Event.SignatureCompleted(
                                        image
                                    )
                                )
                            },
                            modifier = Modifier
                                .width(w)
                                .height(h),

                            strokes = state.signatureStrokes,
                            onStrokesChange = { strokes ->
                                onEventSent(SignatureScreenContract.Event.StrokesChanged(strokes))
                            },
                            strokeColor = Color.Black,
                        )
                    }

                    // Confirm Button
                    Button(
                        onClick = {
                            if (state.signatureBitmap != null) {
                                onEventSent(SignatureScreenContract.Event.StartCapture)
                            }
                        },
                        enabled = !state.isLoading && state.signatureBitmap != null,
                        modifier = Modifier
                            .padding(Dimens.Space.medium)
                            .fillMaxWidth(),
                        shape = MaterialTheme.shapes.small
                    ) {
                        if (state.isLoading) {
                            CircularProgressIndicator(
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        } else {
                            Text(
                                text = "SAVE SIGNATURE",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }
    }
}

