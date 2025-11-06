package com.gpcasiapac.storesystems.feature.history.presentation.composable

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import com.gpcasiapac.storesystems.foundation.design_system.Dimens


@Composable
fun ErrorInfo(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(Dimens.Space.medium),
    message: String,
    attempts: Int
) {
    Card(
        modifier = modifier
            .fillMaxWidth(),
        border = BorderStroke(
            width = Dimens.Stroke.thin,
            color = MaterialTheme.colorScheme.error,
        ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer,
        )
    ) {
        Row(
            modifier = Modifier
                .padding(contentPadding),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {

            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Error",
                tint = MaterialTheme.colorScheme.onErrorContainer,
                modifier = Modifier
                    .size(Dimens.Size.iconSmall)
            )

            Row(modifier = Modifier.padding(
                horizontal = Dimens.Space.small
            ).weight(1f)) {
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    maxLines = 4,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Text(
                text = attempts.toString(),
                modifier = Modifier.padding(Dimens.Space.small),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error,
            )
        }
    }
}

@Preview
@Composable
fun ErrorInfoLongPreview() {
    ErrorInfo(
        message = "There was an error trying to reach the server, If the error persists, please contact the administrator for further assistance.",
        attempts = 5
    )
}

@Preview
@Composable
fun ErrorInfoPreview() {
    ErrorInfo(
        message = "There was an error trying to reach the server",
        attempts = 5
    )
}