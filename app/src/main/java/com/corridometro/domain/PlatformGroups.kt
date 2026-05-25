package com.corridometro.domain

/** Corridas / passageiros. */
val PlatformRideApps: List<Platform> = listOf(
    Platform.UBER,
    Platform.NINETY_NINE,
    Platform.INDRIVER,
    Platform.CABIFY,
    Platform.BOLT,
    Platform.LADY_DRIVER,
)

/** Entregas / delivery. */
val PlatformDeliveryApps: List<Platform> = listOf(
    Platform.IFOOD,
    Platform.RAPPI,
    Platform.UBER_EATS,
    Platform.MERCADO_LIVRE,
    Platform.SHOPEE,
    Platform.LOGGI,
    Platform.AMAZON_FLEX,
)

/** Apps exibidos direto no seletor (variante padrão). */
val PlatformMainApps: List<Platform> = listOf(
    Platform.UBER,
    Platform.NINETY_NINE,
    Platform.INDRIVER,
)

/** Apps adicionais ao tocar em "Outras". */
val PlatformMoreApps: List<Platform> = listOf(
    Platform.CABIFY,
    Platform.BOLT,
    Platform.LADY_DRIVER,
    Platform.IFOOD,
    Platform.RAPPI,
    Platform.UBER_EATS,
    Platform.MERCADO_LIVRE,
    Platform.SHOPEE,
    Platform.LOGGI,
    Platform.AMAZON_FLEX,
    Platform.OUTRO,
)

/** Todos os apps para o APK A (corrida + entrega, sem menu «Outras»). */
val PlatformAppAAll: List<Platform> = PlatformRideApps + PlatformDeliveryApps

fun Platform.isFromMoreMenu(): Boolean = this in PlatformMoreApps

fun Platform.isDeliveryApp(): Boolean = this in PlatformDeliveryApps
