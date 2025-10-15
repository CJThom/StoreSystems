package com.gpcasiapac.storesystems.external.feature_flags.data.internal

import com.gpcasiapac.storesystems.external.feature_flags.api.FeatureFlags
import com.gpcasiapac.storesystems.external.feature_flags.api.FeatureFlagConfig
import com.gpcasiapac.storesystems.external.feature_flags.api.FlagKey
import com.gpcasiapac.storesystems.external.feature_flags.api.MultiContext
import com.gpcasiapac.storesystems.external.feature_flags.api.MultiContextBuilder
import com.gpcasiapac.storesystems.external.feature_flags.data.LDFeatureConfig
import com.launchdarkly.sdk.ContextKind
import com.launchdarkly.sdk.LDContext
import com.launchdarkly.sdk.server.LDClient
import com.launchdarkly.sdk.server.LDConfig
import com.launchdarkly.sdk.server.interfaces.FlagChangeListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

/**
 * JVM/Desktop implementation of LaunchDarkly feature flags using LaunchDarkly Server SDK.
 */
internal actual class LaunchDarklyFeatureFlags actual constructor(
    private val config: FeatureFlagConfig.LaunchDarkly,
    private val contextBuilder: MultiContextBuilder.() -> Unit
) : FeatureFlags {

    private var ldClient: LDClient? = null
    private var ldContext: LDContext? = null
    private var ldConfig: LDConfig? = null
    
    init {
        initialize()
    }

    private fun initialize(): Boolean {
        ldConfig = LDConfig.Builder()
            .build()
        val multiContext = MultiContextBuilder().apply(contextBuilder).build()
        ldContext = createContext(multiContext)
        
        val ldFeatureConfig = LDFeatureConfig(
            apiKey = config.apiKey,
            initializationTimeoutMs = config.initializationTimeoutMs,
            environment = config.environment
        )
        
        ldClient = LDClient(
            ldFeatureConfig.apiKey, ldConfig,
        )
        return ldClient != null && ldClient!!.isInitialized
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
        if (ldContext == null) return key.default
        return ldClient?.boolVariation(key.name, ldContext, key.default) ?: return key.default
    }

    override fun getString(key: FlagKey<String>): String {
        if (ldContext == null) return key.default
        return ldClient?.stringVariation(key.name, ldContext, key.default) ?: return key.default
    }

    override fun getInt(key: FlagKey<Int>): Int {
        if (ldContext == null) return key.default
        return ldClient?.intVariation(key.name, ldContext, key.default) ?: return key.default
    }

    override fun getDouble(key: FlagKey<Double>): Double {
        if (ldContext == null) return key.default
        return ldClient?.doubleVariation(key.name, ldContext, key.default) ?: return key.default
    }

    override fun observeBoolean(key: FlagKey<Boolean>): Flow<Boolean> = callbackFlow {
        val listener = FlagChangeListener {
            val actualValue = isEnabled(key)
            trySend(actualValue)
        }
        ldClient?.flagTracker?.addFlagChangeListener(listener)
        awaitClose {
            ldClient?.flagTracker?.removeFlagChangeListener(listener)
        }
    }

    override fun observeString(key: FlagKey<String>): Flow<String> = callbackFlow {
        val listener = FlagChangeListener {
            val actualValue = getString(key)
            trySend(actualValue)
        }
        ldClient?.flagTracker?.addFlagChangeListener(listener)
        awaitClose {
            ldClient?.flagTracker?.removeFlagChangeListener(listener)
        }
    }

    override fun observeInt(key: FlagKey<Int>): Flow<Int> = callbackFlow {
        val listener = FlagChangeListener {
            val actualValue = getInt(key)
            trySend(actualValue)
        }
        ldClient?.flagTracker?.addFlagChangeListener(listener)
        awaitClose {
            ldClient?.flagTracker?.removeFlagChangeListener(listener)
        }
    }

    override fun observeDouble(key: FlagKey<Double>): Flow<Double> = callbackFlow {
        val listener = FlagChangeListener {
            val actualValue = getDouble(key)
            trySend(actualValue)
        }
        ldClient?.flagTracker?.addFlagChangeListener(listener)
        awaitClose {
            ldClient?.flagTracker?.removeFlagChangeListener(listener)
        }
    }
}