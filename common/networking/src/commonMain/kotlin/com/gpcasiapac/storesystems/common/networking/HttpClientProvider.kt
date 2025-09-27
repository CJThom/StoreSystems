package com.gpcasiapac.storesystems.common.networking

import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.resources.Resources
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.serialization.kotlinx.json.json
import com.gpcasiapac.storesystems.foundation.config.BuildConfig

object HttpClientProvider {
    fun createHttpClient(engine: HttpClientEngine): HttpClient =
        HttpClient(engine) {
            install(Resources)
            install(ContentNegotiation) { json(JsonConfig.json) }
            install(Logging) {
                logger = CustomLogger
                level = LogLevel.ALL
            }
            // Keep default host/port from BuildConfig
            defaultRequest {
                host = BuildConfig.HOST
                port = BuildConfig.PORT
            }
        }

    private object CustomLogger : Logger {
        private const val LOG_TAG = "HttpClient"
        override fun log(message: String) {
            co.touchlab.kermit.Logger.d(LOG_TAG) { message }
        }
    }
}