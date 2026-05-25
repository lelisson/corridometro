package com.corridometro.services

/**
 * Ponto de extensão para integração futura com a API da Uber (OAuth + sync de corridas).
 * Documentação: https://developer.uber.com/
 */
object UberApiPlaceholder {
    suspend fun isConnected(): Boolean = false

    suspend fun connect() {
        throw UnsupportedOperationException(
            "Integracao Uber ainda nao configurada. Use o registro manual em Jornada.",
        )
    }
}
