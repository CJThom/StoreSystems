package com.gpcasiapac.storesystems.common.kotlin

sealed class DataResult<out T> {
    data class Success<out T>(val data: T) : DataResult<T>()

    sealed class Error(
        open val message: String,
        open val throwable: Throwable? = null,
    ) : DataResult<Nothing>() {

        sealed class Client(
            override val message: String,
            override val throwable: Throwable? = null,
        ) : Error(message, throwable) {

            data class Database(
                override val message: String = "Database error",
                override val throwable: Throwable? = null,
            ) : Client(message, throwable)

            data class Mapping(
                override val message: String = "Mapping error",
                override val throwable: Throwable? = null,
            ) : Client(message, throwable)

            data class UnexpectedError(
                override val message: String = "Unexpected client error",
                override val throwable: Throwable? = null,
            ) : Client(message, throwable)
        }

        sealed class Network(
            override val throwable: Throwable? = null,
            override val message: String,
        ) : Error(message, throwable) {

            data class HttpError(
                override val throwable: Throwable? = null,
                val code: Int,
                override val message: String = "HTTP error: $code",
            ) : Network(throwable, message)

            data class ConnectionError(
                override val throwable: Throwable? = null,
                override val message: String = "Network connection error",
            ) : Network(throwable, message)

            data class SerializationError(
                override val throwable: Throwable? = null,
                override val message: String = "Serialization error",
            ) : Network(throwable, message)

            data class UnknownError(
                override val throwable: Throwable? = null,
                override val message: String = "Unknown network error",
            ) : Network(throwable, message)
        }
    }
}