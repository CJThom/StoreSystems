package com.gpcasiapac.storesystems.feature.collect.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gpcasiapac.storesystems.common.presentation.compose.theme.borderStroke
import com.gpcasiapac.storesystems.foundation.component.HeaderMedium
import com.gpcasiapac.storesystems.foundation.design_system.Dimens
import com.gpcasiapac.storesystems.foundation.design_system.GPCTheme
import kotlin.time.Instant

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SignaturePreviewImage(
    image: String?,
    onSignClick: () -> Unit,
    enabled: Boolean = true,
    modifier: Modifier = Modifier,
    onRetakeClick: () -> Unit = {},
    signerName: String? = null,
    signedDateTime: Instant? = null,
    contentPadding: PaddingValues = PaddingValues(Dimens.Space.medium)
) {
    Column(
        modifier = modifier.padding(contentPadding),
        verticalArrangement = Arrangement.spacedBy(Dimens.Space.medium)
    ) {

        HeaderMedium(
            text = "Signature",
            isLoading = false,
            contentPadding = PaddingValues()
        )

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Dimens.Space.medium)
        ) {
            Card(
                modifier = Modifier
                    .height(height = 260.dp)
                    .fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
                border = MaterialTheme.borderStroke(),
            ) {
                if (image != null) {
                    // Show signature preview
                    Base64ImageView(
                        base64 = image,
                        contentScale = ContentScale.Inside
                    )

                } else {
                    // Show sign button when no signature
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Button(
                            onClick = onSignClick,
                            enabled = enabled,
                            contentPadding = ButtonDefaults.contentPaddingFor(ButtonDefaults.MinHeight)
                        ) {
                            Icon(imageVector = Icons.Outlined.Edit, contentDescription = "Edit")
                            Spacer(Modifier.size(ButtonDefaults.iconSpacingFor(ButtonDefaults.MinHeight)))
                            Text(text = "SIGN")
                        }
                    }
                }
            }
            // Signature details row at the bottom when image is present
            if (image != null) {
                SignatureDetails(
                    name = signerName,
                    dateTime = signedDateTime,
                    onRetakeClick = onRetakeClick,
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues()
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SignaturePreviewImagePreview() {
    GPCTheme {
        SignaturePreviewImage(
            image = "dummy_base64_string",
            onSignClick = {},
            signerName = "John Doe",
            signedDateTime = Instant.fromEpochMilliseconds(1692505600000)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SignaturePreviewImageNullImagePreview() {
    GPCTheme {
        SignaturePreviewImage(
            image = null,
            onSignClick = {}
        )
    }
}
