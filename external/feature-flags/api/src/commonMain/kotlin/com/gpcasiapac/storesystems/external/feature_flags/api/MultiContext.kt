package com.gpcasiapac.storesystems.external.feature_flags.api

/**
 * Represents a single feature flag context with a kind, key, and attributes.
 */
data class FeatureFlagContext(
    val kind: String,
    val key: String,
    val attributes: Map<String, Any> = emptyMap()
)

/**
 * Container for multiple contexts that can be evaluated together.
 */
data class MultiContext(
    val contexts: List<FeatureFlagContext>
) {
    companion object {
        fun single(kind: String, key: String, attributes: Map<String, Any> = emptyMap()) =
            MultiContext(listOf(FeatureFlagContext(kind, key, attributes)))

        //TODO Do we need anonymous contexts?

        fun user(key: String, attributes: Map<String, Any> = emptyMap()) =
            single("user", key, attributes)

        fun organization(key: String, attributes: Map<String, Any> = emptyMap()) =
            single("organization", key, attributes)

        fun device(key: String, attributes: Map<String, Any> = emptyMap()) =
            single("device", key, attributes)
    }

    fun addContext(context: FeatureFlagContext): MultiContext {
        return MultiContext(contexts + context)
    }

    fun getContext(kind: String): FeatureFlagContext? {
        return contexts.find { it.kind == kind }
    }

    fun hasContext(kind: String): Boolean {
        return contexts.any { it.kind == kind }
    }
}

/**
 * Builder pattern for easier MultiContext creation.
 */
class MultiContextBuilder {
    private val contexts = mutableListOf<FeatureFlagContext>()

    fun user(key: String, attributes: Map<String, Any> = emptyMap()): MultiContextBuilder {
        contexts.add(FeatureFlagContext("user", key, attributes))
        return this
    }

    fun organization(key: String, attributes: Map<String, Any> = emptyMap()): MultiContextBuilder {
        contexts.add(FeatureFlagContext("organization", key, attributes))
        return this
    }

    fun device(key: String, attributes: Map<String, Any> = emptyMap()): MultiContextBuilder {
        contexts.add(FeatureFlagContext("device", key, attributes))
        return this
    }

    fun custom(
        kind: String,
        key: String,
        attributes: Map<String, Any> = emptyMap()
    ): MultiContextBuilder {
        contexts.add(FeatureFlagContext(kind, key, attributes))
        return this
    }

    fun build(): MultiContext = MultiContext(contexts.toList())
}