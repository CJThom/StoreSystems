package com.gpcasiapac.storesystems.common.scanning.android

import android.content.Context
import android.content.Intent
import android.os.Bundle

/**
 * Small helper for sending Zebra DataWedge API intents.
 */
internal object DataWedgeIntents {
    const val ACTION = "com.symbol.datawedge.api.ACTION"
    const val RESULT_ACTION = "com.symbol.datawedge.api.RESULT_ACTION"

    const val EXTRA_CREATE_PROFILE = "com.symbol.datawedge.api.CREATE_PROFILE"
    const val EXTRA_SET_CONFIG = "com.symbol.datawedge.api.SET_CONFIG"
    const val EXTRA_SWITCH_TO_PROFILE = "com.symbol.datawedge.api.SWITCH_TO_PROFILE"
    const val EXTRA_SOFT_SCAN_TRIGGER = "com.symbol.datawedge.api.SOFT_SCAN_TRIGGER"

    fun sendCommand(context: Context, key: String, value: String) {
        context.sendBroadcast(Intent(ACTION).putExtra(key, value))
    }

    fun sendBundle(context: Context, key: String, bundle: Bundle) {
        context.sendBroadcast(Intent(ACTION).putExtra(key, bundle))
    }
}
