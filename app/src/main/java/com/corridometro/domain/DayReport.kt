package com.corridometro.domain

data class DayReport(
    val dateEpochDay: Long,
    val summary: Summary,
    val expenseBreakdown: List<ExpenseCategoryTotal>,
    val shifts: List<WorkShift>,
    val expenses: List<Expense>,
    val platformBreakdown: List<Pair<Platform, Summary>>,
    val finalizedAtEpochMillis: Long?,
    val isFinalized: Boolean = finalizedAtEpochMillis != null,
)

data class DayHistoryItem(
    val dateEpochDay: Long,
    val shiftCount: Int,
    val tripCount: Int,
    val grossEarnings: Double,
    val isFinalized: Boolean,
    val platformLabels: List<String> = emptyList(),
)

fun buildDayReport(
    dateEpochDay: Long,
    allShifts: List<WorkShift>,
    allExpenses: List<Expense>,
    finalizedAtEpochMillis: Long?,
): DayReport {
    val shifts = allShifts.filter { it.dateEpochDay == dateEpochDay }
    val expenses = allExpenses.filter { it.dateEpochDay == dateEpochDay }
    val summary = calculateSummary(shifts, expenses)
    val platformBreakdown = buildPlatformBreakdown(shifts, expenses)

    return DayReport(
        dateEpochDay = dateEpochDay,
        summary = summary,
        expenseBreakdown = groupExpensesByCategory(expenses),
        shifts = shifts,
        expenses = expenses,
        platformBreakdown = platformBreakdown,
        finalizedAtEpochMillis = finalizedAtEpochMillis,
    )
}

fun buildDayHistory(
    allShifts: List<WorkShift>,
    finalizedDays: Set<Long>,
): List<DayHistoryItem> =
    allShifts
        .groupBy { it.dateEpochDay }
        .map { (day, shifts) ->
            DayHistoryItem(
                dateEpochDay = day,
                shiftCount = shifts.size,
                tripCount = shifts.sumOf { it.tripCount },
                grossEarnings = shifts.sumOf { it.totalEarnings },
                isFinalized = day in finalizedDays,
                platformLabels = shifts.map { it.platform.label }.distinct(),
            )
        }
        .sortedByDescending { it.dateEpochDay }
