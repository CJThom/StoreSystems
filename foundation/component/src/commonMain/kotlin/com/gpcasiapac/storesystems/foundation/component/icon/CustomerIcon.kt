package com.gpcasiapac.storesystems.foundation.component.icon

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.gpcasiapac.storesystems.common.presentation.compose.placeholder.material3.placeholder
import com.gpcasiapac.storesystems.foundation.design_system.Dimens
import com.gpcasiapac.storesystems.foundation.design_system.GPCTheme
import org.jetbrains.compose.ui.tooling.preview.Preview


object CustomerIconDefaults {

    const val SIZE = 22
    const val PADDING = 3

}

@Composable
fun B2BIcon(
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
) {

    Icon(
        modifier = modifier
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primary)
           //
            .size(CustomerIconDefaults.SIZE.dp)
            .padding(CustomerIconDefaults.PADDING.dp)
            .placeholder(isLoading, shape = CircleShape),
        imageVector = Icons.Default.Business, contentDescription = "Business",
        tint = MaterialTheme.colorScheme.onPrimary,
    )
//    Surface(
//        modifier = modifier.size(CustomerIconDefaults.SIZE.dp)
//            .placeholder(isLoading, shape = CircleShape),
//        color = MaterialTheme.colorScheme.surfaceContainerHighest,
//        border = MaterialTheme.borderStroke(),
//        shape = CircleShape
//    ) {
//        Icon(
//            imageVector = Icons.Outlined.BusinessCenter,
//            contentDescription = "Business",
//            tint = MaterialTheme.colorScheme.primary,
//            modifier = Modifier.padding(Dimens.Space.extraSmall)
//        )
//    }
}


//@Composable
//fun B2BIcon(
//    modifier: Modifier = Modifier,
//    isLoading: Boolean = false,
//) {
//    Icon(
//        modifier = modifier.size(CustomerIconDefaults.SIZE.dp) .placeholder(isLoading, shape = CircleShape),
//        imageVector = Icons.Default.Business, contentDescription = "Business",
//        tint = MaterialTheme.colorScheme.primary,
//    )
////    Surface(
////        modifier = modifier.size(CustomerIconDefaults.SIZE.dp)
////            .placeholder(isLoading, shape = CircleShape),
////        color = MaterialTheme.colorScheme.surfaceContainerHighest,
////        border = MaterialTheme.borderStroke(),
////        shape = CircleShape
////    ) {
////        Icon(
////            imageVector = Icons.Outlined.BusinessCenter,
////            contentDescription = "Business",
////            tint = MaterialTheme.colorScheme.primary,
////            modifier = Modifier.padding(Dimens.Space.extraSmall)
////        )
////    }
//}


//@Composable
//fun B2BIcon(
//    modifier: Modifier = Modifier,
//    isLoading: Boolean = false,
//) {
//    Surface(
//        modifier = modifier.size(CustomerIconDefaults.SIZE.dp)
//            .placeholder(isLoading, shape = CircleShape),
//        color = MaterialTheme.colorScheme.surfaceContainerHighest,
//        border = MaterialTheme.borderStroke(),
//        shape = CircleShape
//    ) {
//        Icon(
//            imageVector = Icons.Outlined.BusinessCenter,
//            contentDescription = "Business",
//            tint = MaterialTheme.colorScheme.primary,
//            modifier = Modifier.padding(Dimens.Space.extraSmall)
//        )
//    }
//}

@Composable
fun B2CIcon(
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
) {
    Icon(
        modifier = modifier
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primaryContainer)
           //
            .size(CustomerIconDefaults.SIZE.dp)
            .padding(3.dp)
            .placeholder(isLoading, shape = CircleShape),
        imageVector = Icons.Default.Person,
        contentDescription = "Person",
        tint = MaterialTheme.colorScheme.primary,
    )
}


//@Composable
//fun B2CIcon(
//    modifier: Modifier = Modifier,
//    isLoading: Boolean = false,
//) {
//    Box(modifier = modifier  .size(20.dp + 4.dp), contentAlignment = Alignment.Center) {
//        Box(
//            modifier = Modifier
//                .clip(CircleShape)
//                .background(MaterialTheme.colorScheme.primaryContainer)
//                .size(20.dp + 4.dp)
//        ) {
//
//        }
//        Icon(
//            modifier = modifier
//                //.padding(3.dp)
//                .padding(3.dp)
//                .clip(CircleShape)
//
//                 .size(40.dp + 4.dp)
//
//
//
//                // .clip(CircleShape)
//                //.background(MaterialTheme.colorScheme.primaryContainer)
//
//                .placeholder(isLoading, shape = CircleShape),
//            imageVector = Icons.Outlined.AccountCircle, contentDescription = "Person",
//             tint = MaterialTheme.colorScheme.outline,
//        )
//    }
//    Icon(
//        modifier = modifier
//            .clip(CircleShape)
//            .background(MaterialTheme.colorScheme.outlineVariant)
//            .padding(3.5.dp)
//            .size(CustomerIconDefaults.SIZE.dp)
//            .placeholder(isLoading, shape = CircleShape),
//        imageVector = Icons.Default.Person, contentDescription = "Person",
//        tint = MaterialTheme.colorScheme.onPrimary,
//    )
//    Surface(
//        modifier = modifier.size(CustomerIconDefaults.SIZE.dp)
//            .placeholder(isLoading, shape = CircleShape),
//        border = MaterialTheme.borderStroke(),
//        shape = CircleShape
//    ) {
//        Icon(
//            imageVector = Icons.Outlined.Person,
//            contentDescription = "Person",
//            modifier = Modifier.padding(Dimens.Space.extraSmall)
//        )
//    }
//}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
fun B2CIconPreview() {
    GPCTheme {
        Box(Modifier.padding(Dimens.Space.medium)) {
            B2CIcon()
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
fun B2CIconLoadingPreview() {
    GPCTheme {
        Box(Modifier.padding(Dimens.Space.medium)) {
            B2CIcon(isLoading = true)
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
fun B2BIconPreview() {
    GPCTheme {
        Box(Modifier.padding(Dimens.Space.medium)) {
            B2BIcon()
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
fun B2BIconLoadingPreview() {
    GPCTheme {
        Box(Modifier.padding(Dimens.Space.medium)) {
            B2BIcon(isLoading = true)
        }
    }
}
