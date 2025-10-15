import android.app.Application
import com.gpcasiapac.storesystems.external.feature_flags.api.FeatureFlagService
import com.gpcasiapac.storesystems.external.feature_flags.api.FlagKey
import com.gpcasiapac.storesystems.external.feature_flags.api.MultiContext
import com.gpcasiapac.storesystems.external.feature_flags.api.MultiContextBuilder
import com.gpcasiapac.storesystems.external.feature_flags.data.LDFeatureConfig
import com.launchdarkly.sdk.ContextKind
import com.launchdarkly.sdk.LDContext
import com.launchdarkly.sdk.android.FeatureFlagChangeListener
import com.launchdarkly.sdk.android.LDClient
import com.launchdarkly.sdk.android.LDConfig
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class LDFeatureFlagServiceImpl(
    private val context: Application
) : FeatureFlagService<LDFeatureConfig> {

    private var ldClient: LDClient? = null
    private var ldConfig: LDConfig? = null

    override fun initialize(
        config: LDFeatureConfig,
        contextBuilder: MultiContextBuilder.() -> Unit
    ): Boolean {
        ldConfig = LDConfig.Builder(LDConfig.Builder.AutoEnvAttributes.Enabled)
            .mobileKey(config.apiKey)
            .build()
        val multiContext = MultiContextBuilder().apply(contextBuilder).build()
        val ldContext = createContext(multiContext)
        ldClient = LDClient.init(
            context, ldConfig, ldContext, config.initializationTimeoutMs
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

    override fun getFeatureBoolean(key: FlagKey<Boolean>): Boolean {
        return ldClient?.boolVariation(key.name, key.default) ?: return key.default
    }

    override fun getFeatureString(key: FlagKey<String>): String {
        return ldClient?.stringVariation(key.name, key.default) ?: return key.default
    }

    override fun getFeatureInt(key: FlagKey<Int>): Int {
        return ldClient?.intVariation(key.name, key.default) ?: return key.default
    }

    override fun getFeatureDouble(key: FlagKey<Double>): Double {
        return ldClient?.doubleVariation(key.name, key.default) ?: return key.default
    }

    override fun observeFeatureString(key: FlagKey<String>): Flow<String> = callbackFlow {
        val listener = FeatureFlagChangeListener {
            val actualValue = getFeatureString(key)
            trySend(actualValue)
        }
        ldClient?.registerFeatureFlagListener(key.name, listener)
        awaitClose {
            ldClient?.unregisterFeatureFlagListener(key.name, listener)
        }
    }

    override fun observeFeatureInt(key: FlagKey<Int>): Flow<Int> = callbackFlow {
        val listener = FeatureFlagChangeListener {
            val actualValue = getFeatureInt(key)
            trySend(actualValue)
        }
        ldClient?.registerFeatureFlagListener(key.name, listener)
        awaitClose {
            ldClient?.unregisterFeatureFlagListener(key.name, listener)
        }
    }

    override fun observeFeatureDouble(key: FlagKey<Double>): Flow<Double> = callbackFlow {
        val listener = FeatureFlagChangeListener {
            val actualValue = getFeatureDouble(key)
            trySend(actualValue)
        }
        ldClient?.registerFeatureFlagListener(key.name, listener)
        awaitClose {
            ldClient?.unregisterFeatureFlagListener(key.name, listener)
        }
    }


    override fun observeFeatureBoolean(key: FlagKey<Boolean>): Flow<Boolean> = callbackFlow {
        val listener = FeatureFlagChangeListener {
            val actualValue = getFeatureBoolean(key)
            trySend(actualValue)
        }
        ldClient?.registerFeatureFlagListener(key.name, listener)
        awaitClose {
            ldClient?.unregisterFeatureFlagListener(key.name, listener)
        }
    }
}
