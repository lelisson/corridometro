package com.corridometro.data.billing

/** IDs das assinaturas — devem existir na Play Console (Monetização > Assinaturas). */
const val PREMIUM_MONTHLY_ID = "corridometro_premium_mensal"
const val PREMIUM_SEMESTER_ID = "corridometro_premium_semestral"
const val PREMIUM_ANNUAL_ID = "corridometro_premium_anual"

val PREMIUM_SUBSCRIPTION_IDS = listOf(
    PREMIUM_MONTHLY_ID,
    PREMIUM_SEMESTER_ID,
    PREMIUM_ANNUAL_ID,
)

/** Mantido para compatibilidade com documentação antiga. */
const val PREMIUM_SUBSCRIPTION_ID = PREMIUM_MONTHLY_ID

enum class PremiumPlan(
    val productId: String,
    val title: String,
    val fallbackPrice: String,
    val billingPeriodLabel: String,
    val savingsHint: String? = null,
) {
    MONTHLY(
        productId = PREMIUM_MONTHLY_ID,
        title = "Mensal",
        fallbackPrice = "R$ 20,00",
        billingPeriodLabel = "por mês",
    ),
    SEMESTER(
        productId = PREMIUM_SEMESTER_ID,
        title = "Semestral",
        fallbackPrice = "R$ 100,00",
        billingPeriodLabel = "6 meses",
        savingsHint = "Economize vs. mensal",
    ),
    ANNUAL(
        productId = PREMIUM_ANNUAL_ID,
        title = "Anual",
        fallbackPrice = "R$ 200,00",
        billingPeriodLabel = "por ano",
        savingsHint = "Melhor custo-benefício",
    ),
}
