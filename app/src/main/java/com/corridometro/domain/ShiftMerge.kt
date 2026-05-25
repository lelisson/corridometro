package com.corridometro.domain

enum class DayUpdateMode {
    /** Novo registro no mesmo app (dois turnos Uber no dia, por exemplo). */
    ADD,
    SUM,
    REPLACE,
}

/** Soma jornadas do mesmo app no dia (mantém a plataforma do registro novo). */
fun mergeShiftsForDay(existing: List<WorkShift>, incoming: WorkShift): WorkShift {
    val all = existing.filter { it.platform == incoming.platform } + incoming
    val totalKm = all.sumOf { it.km }
    val totalLiters = all.sumOf { fuelLitersUsed(it.km, it.fuelKmPerLiter) }
    val fuelKmPerLiter = if (totalLiters > 0) totalKm / totalLiters else incoming.fuelKmPerLiter
    val fuelPricePerLiter = if (totalLiters > 0) {
        all.sumOf { fuelLitersUsed(it.km, it.fuelKmPerLiter) * it.fuelPricePerLiter } / totalLiters
    } else {
        incoming.fuelPricePerLiter
    }
    val notes = (existing.mapNotNull { it.note } + listOfNotNull(incoming.note))
        .filter { it.isNotBlank() }
        .distinct()
        .joinToString(" | ")
        .ifBlank { null }

    return incoming.copy(
        id = 0,
        km = totalKm,
        tripCount = all.sumOf { it.tripCount },
        totalEarnings = all.sumOf { it.totalEarnings },
        fuelKmPerLiter = fuelKmPerLiter,
        fuelPricePerLiter = fuelPricePerLiter,
        startMinutesOfDay = all.minOf { it.startMinutesOfDay },
        endMinutesOfDay = all.maxOf { it.endMinutesOfDay },
        note = notes,
    )
}

fun groupExpensesByCategory(expenses: List<Expense>): List<ExpenseCategoryTotal> =
    expenses
        .groupBy { it.category }
        .map { (category, items) ->
            ExpenseCategoryTotal(
                category = category,
                total = items.sumOf { it.amount },
                count = items.size,
                items = items.sortedByDescending { it.amount },
            )
        }
        .sortedByDescending { it.total }

data class ExpenseCategoryTotal(
    val category: ExpenseCategory,
    val total: Double,
    val count: Int,
    val items: List<Expense> = emptyList(),
)
