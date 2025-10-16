package com.gpcasiapac.storesystems.external.feature_flags.data.internal

import android.app.Application
import com.gpcasiapac.storesystems.external.feature_flags.api.FeatureFlagConfig
import com.gpcasiapac.storesystems.external.feature_flags.api.FeatureFlags
import com.gpcasiapac.storesystems.external.feature_flags.api.FlagKey
import com.gpcasiapac.storesystems.external.feature_flags.api.MultiContext
import com.gpcasiapac.storesystems.external.feature_flags.api.MultiContextBuilder
import com.launchdarkly.sdk.ContextKind
import com.launchdarkly.sdk.LDContext
import com.launchdarkly.sdk.android.FeatureFlagChangeListener
import com.launchdarkly.sdk.android.LDClient
import com.launchdarkly.sdk.android.LDConfig
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

/**
 * Android implementation of LaunchDarkly feature flags using LaunchDarkly Android Client SDK.
 */
class LaunchDarklyFeatureFlagsAndroidImpl(
    private val application: Application,
    private val config: FeatureFlagConfig.LaunchDarkly,
) : FeatureFlags {

    private var ldClient: LDClient? = null
    private var ldConfig: LDConfig? = null

    override fun initialize(contextBuilder: MultiContextBuilder.() -> Unit): Boolean {
        ldConfig = LDConfig.Builder(LDConfig.Builder.AutoEnvAttributes.Enabled)
            .mobileKey(config.apiKey)
            .build()
        val multiContext = MultiContextBuilder().apply(contextBuilder).build()
        val ldContext = createContext(multiContext)
        ldClient = LDClient.init(
            application,
            ldConfig,
            ldContext,
            config.initializationTimeoutMs
        )
        return ldClient != null && ldClient!!.isInitialized
    }

    override fun updateContext(contextBuilder: MultiContextBuilder.() -> Unit) {
        val multiContext = MultiContextBuilder().apply(contextBuilder).build()
        val newContext = createContext(multiContext)
        ldClient?.identify(newContext)
    }

    private fun createContext(multiContext: MultiContext): LDContext? {
        if (multiContext.contexts.size == 1) {
            val contextInformation = multiContext.contexts.first()
            val newContext =
                LDContext.builder(
                    ContextKind.of(contextInformation.kind),
                    contextInformation.key
                )
            contextInformation.attributes.forEach { attribute ->
                when (attribute.value) {
                    is String -> newContext.set(attribute.key, attribute.value as String)
                    is Int -> newContext.set(attribute.key, attribute.value as Int)
                }
            }
            return newContext.build()
        } else {
            val contextList = multiContext.contexts.mapNotNull { contextInformation ->
                val newContext =
                    LDContext.builder(
                        ContextKind.of(contextInformation.kind),
                        contextInformation.key
                    )
                contextInformation.attributes.forEach { attribute ->
                    when (attribute.value) {
                        is String -> newContext.set(attribute.key, attribute.value as String)
                        is Int -> newContext.set(attribute.key, attribute.value as Int)
                        is Double -> newContext.set(attribute.key, attribute.value as Double)
                        is Boolean -> newContext.set(attribute.key, attribute.value as Boolean)
                        else -> newContext.set(attribute.key, attribute.value.toString())
                    }
                }
                newContext.build()
            }
            val multiContext = LDContext.createMulti(*contextList.toTypedArray())
            return multiContext
        }
    }

    override fun isEnabled(key: FlagKey<Boolean>): Boolean {
        return ldClient?.boolVariation(key.name, key.default) ?: key.default
    }

    override fun getString(key: FlagKey<String>): String {
        return ldClient?.stringVariation(key.name, key.default) ?: key.default
    }

    override fun getInt(key: FlagKey<Int>): Int {
        return ldClient?.intVariation(key.name, key.default) ?: key.default
    }

    override fun getDouble(key: FlagKey<Double>): Double {
        return ldClient?.doubleVariation(key.name, key.default) ?: key.default
    }

    override fun observeBoolean(key: FlagKey<Boolean>): Flow<Boolean> = callbackFlow {
        val listener = FeatureFlagChangeListener {
            val actualValue = isEnabled(key)
            trySend(actualValue)
        }
        ldClient?.registerFeatureFlagListener(key.name, listener)
        awaitClose {
            ldClient?.unregisterFeatureFlagListener(key.name, listener)
        }
    }

    override fun observeString(key: FlagKey<String>): Flow<String> = callbackFlow {
        val listener = FeatureFlagChangeListener {
            val actualValue = getString(key)
            trySend(actualValue)
        }
        ldClient?.registerFeatureFlagListener(key.name, listener)
        awaitClose {
            ldClient?.unregisterFeatureFlagListener(key.name, listener)
        }
    }

    override fun observeInt(key: FlagKey<Int>): Flow<Int> = callbackFlow {
        val listener = FeatureFlagChangeListener {
            val actualValue = getInt(key)
            trySend(actualValue)
        }
        ldClient?.registerFeatureFlagListener(key.name, listener)
        awaitClose {
            ldClient?.unregisterFeatureFlagListener(key.name, listener)
        }
    }

    override fun observeDouble(key: FlagKey<Double>): Flow<Double> = callbackFlow {
        val listener = FeatureFlagChangeListener {
            val actualValue = getDouble(key)
            trySend(actualValue)
        }
        ldClient?.registerFeatureFlagListener(key.name, listener)
        awaitClose {
            ldClient?.unregisterFeatureFlagListener(key.name, listener)
        }
    }
}