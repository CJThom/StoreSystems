package com.gpcasiapac.storesystems.common.scanning.android

import android.content.Intent

/**
 * Safely parse DataWedge raw decode bytes from the Intent extras, handling multiple shapes:
 * - ByteArray directly under key "com.symbol.datawedge.decode_data"
 * - Bundle with key "data" containing ByteArray
 * - ArrayList<Bundle> or ArrayList<ByteArray>
 * - Array<Bundle> or Array<ByteArray>
 */
internal fun parseDecodeData(intent: Intent): ByteArray? =
    runCatching {
        val extras = intent.extras ?: return null
        val any = extras.get("com.symbol.datawedge.decode_data") ?: return null
        when (any) {
            is ByteArray -> any
            is android.os.Bundle -> any.getByteArray("data")
            is java.util.ArrayList<*> -> {
                val first = any.firstOrNull()
                when (first) {
                    is android.os.Bundle -> first.getByteArray("data")
                    is ByteArray -> first
                    else -> null
                }
            }
            is Array<*> -> {
                val first = any.firstOrNull()
                when (first) {
                    is android.os.Bundle -> first.getByteArray("data")
                    is ByteArray -> first
                    else -> null
                }
            }
            else -> null
        }
    }.getOrNull()
