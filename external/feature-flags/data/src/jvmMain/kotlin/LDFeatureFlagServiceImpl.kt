import com.gpcasiapac.storesystems.external.feature_flags.api.FeatureFlagService
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

class LDFeatureFlagServiceImpl : FeatureFlagService<LDFeatureConfig> {

    private var ldClient: LDClient? = null
    private var ldContext: LDContext? = null
    private var ldConfig: LDConfig? = null

    override fun initialize(
        config: LDFeatureConfig,
        contextBuilder: MultiContextBuilder.() -> Unit
    ): Boolean {
        ldConfig = LDConfig.Builder()
            .build()
        val multiContext = MultiContextBuilder().apply(contextBuilder).build()
        ldContext = createContext(multiContext)
        ldClient = LDClient(
            config.apiKey, ldConfig,
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
        if (ldContext == null) return key.default
        return ldClient?.boolVariation(key.name, ldContext, key.default) ?: return key.default
    }

    override fun getFeatureString(key: FlagKey<String>): String {
        if (ldContext == null) return key.default
        return ldClient?.stringVariation(key.name, ldContext, key.default) ?: return key.default
    }

    override fun getFeatureInt(key: FlagKey<Int>): Int {
        if (ldContext == null) return key.default
        return ldClient?.intVariation(key.name, ldContext, key.default) ?: return key.default
    }

    override fun getFeatureDouble(key: FlagKey<Double>): Double {
        if (ldContext == null) return key.default
        return ldClient?.doubleVariation(key.name, ldContext, key.default) ?: return key.default
    }

    override fun observeFeatureString(key: FlagKey<String>): Flow<String> = callbackFlow {
        val listener = FlagChangeListener {
            val actualValue = getFeatureString(key)
            trySend(actualValue)
        }
        ldClient?.flagTracker?.addFlagChangeListener(listener)
        awaitClose {
            ldClient?.flagTracker?.removeFlagChangeListener(listener)
        }
    }

    override fun observeFeatureInt(key: FlagKey<Int>): Flow<Int> = callbackFlow {
        val listener = FlagChangeListener {
            val actualValue = getFeatureInt(key)
            trySend(actualValue)
        }
        ldClient?.flagTracker?.addFlagChangeListener(listener)
        awaitClose {
            ldClient?.flagTracker?.removeFlagChangeListener(listener)
        }
    }

    override fun observeFeatureDouble(key: FlagKey<Double>): Flow<Double> = callbackFlow {
        val listener = FlagChangeListener {
            val actualValue = getFeatureDouble(key)
            trySend(actualValue)
        }
        ldClient?.flagTracker?.addFlagChangeListener(listener)
        awaitClose {
            ldClient?.flagTracker?.removeFlagChangeListener(listener)
        }
    }


    override fun observeFeatureBoolean(key: FlagKey<Boolean>): Flow<Boolean> = callbackFlow {
        val listener = FlagChangeListener {
            val actualValue = getFeatureBoolean(key)
            trySend(actualValue)
        }
        ldClient?.flagTracker?.addFlagChangeListener(listener)
        awaitClose {
            ldClient?.flagTracker?.removeFlagChangeListener(listener)
        }
    }
}
