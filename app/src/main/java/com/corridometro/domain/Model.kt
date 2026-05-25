package com.corridometro.domain

enum class Platform(val label: String) {
    UBER("Uber"),
    NINETY_NINE("99"),
    INDRIVER("inDrive"),
    CABIFY("Cabify"),
    BOLT("Bolt"),
    LADY_DRIVER("Lady Driver"),
    IFOOD("iFood"),
    RAPPI("Rappi"),
    UBER_EATS("Uber Eats"),
    MERCADO_LIVRE("Mercado"),
    SHOPEE("Shopee"),
    LOGGI("Loggi"),
    AMAZON_FLEX("Amazon"),
    OUTRO("Outro app"),
}

enum class ExpenseCategory(val label: String) {
    COMBUSTIVEL("Combustível"),
    MANUTENCAO("Manutenção"),
    PEDAGIO("Pedágio"),
    SEGURO("Seguro"),
    ALUGUEL("Aluguel do veículo"),
    LAVAGEM("Lavagem"),
    ALMOCO("Almoço"),
    LANCHE("Lanche"),
    OUTRO("Outro"),
}

/** Gastos que o motorista registra na jornada (combustível já é calculado automaticamente). */
val JourneyExpenseCategories = ExpenseCategory.entries.filter { it != ExpenseCategory.COMBUSTIVEL }

enum class PeriodFilter(val label: String) {
    HOJE("Hoje"),
    SEMANA("7 dias"),
    MES("30 dias"),
    TUDO("Tudo"),
    PERSONALIZADO("Período"),
}

data class WorkShift(
    val id: Long = 0,
    val platform: Platform,
    val dateEpochDay: Long,
    val startMinutesOfDay: Int,
    val endMinutesOfDay: Int,
    val km: Double,
    val fuelKmPerLiter: Double,
    val tripCount: Int,
    val fuelPricePerLiter: Double,
    val totalEarnings: Double,
    val note: String? = null,
)

data class Expense(
    val id: Long = 0,
    val category: ExpenseCategory,
    val amount: Double,
    val dateEpochDay: Long,
    val platform: Platform? = null,
    val note: String? = null,
)

data class Summary(
    val grossEarnings: Double = 0.0,
    val fuelCost: Double = 0.0,
    val otherExpenses: Double = 0.0,
    val expenses: Double = 0.0,
    val profit: Double = 0.0,
    val totalKm: Double = 0.0,
    val profitPerKm: Double = 0.0,
    val tripCount: Int = 0,
    val shiftCount: Int = 0,
    val totalMinutes: Int = 0,
    val profitPerHour: Double = 0.0,
)
