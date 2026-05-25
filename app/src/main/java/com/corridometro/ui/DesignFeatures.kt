package com.corridometro.ui

import com.corridometro.BuildConfig

/**
 * Variantes de design baseadas nos mockups gerados:
 * - [MOCKUP_INICIO] — APK A (imagem do dashboard / Início)
 * - [MOCKUP_JORNADA] — APK B (imagem da Jornada em blocos)
 */
object DesignFeatures {
    const val MOCKUP_INICIO = "INICIO"
    const val MOCKUP_JORNADA = "JORNADA"

    val mockupVariant: String get() = BuildConfig.MOCKUP_VARIANT
    val isMockupInicio: Boolean get() = mockupVariant == MOCKUP_INICIO
    val isMockupJornada: Boolean get() = mockupVariant == MOCKUP_JORNADA
    val designLabel: String get() = BuildConfig.DESIGN_LABEL
}
