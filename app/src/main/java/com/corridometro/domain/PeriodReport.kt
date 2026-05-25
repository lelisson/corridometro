package com.corridometro.domain

import java.time.LocalDate

data class PeriodReport(
    val startEpochDay: Long,
    val endEpochDay: Long,
    val label: String,
    val summary: Summary,
    val expenseBreakdown: List<ExpenseCategoryTotal>,
    val platformBreakdown: List<Pair<Platform, Summary>>,
    val shifts: List<WorkShift>,
    val dayCount: Int,
)

fun buildPeriodReport(
    startEpochDay: Long,
    endEpochDay: Long,
    label: String,
    allShifts: List<WorkShift>,
    allExpenses: List<Expense>,
): PeriodReport {
    val shifts = allShifts.filter { it.dateEpochDay in startEpochDay..endEpochDay }
    val expenses = allExpenses.filter { it.dateEpochDay in startEpochDay..endEpochDay }
    return PeriodReport(
        startEpochDay = startEpochDay,
        endEpochDay = endEpochDay,
        label = label,
        summary = calculateSummary(shifts, expenses),
        expenseBreakdown = groupExpensesByCategory(expenses),
        platformBreakdown = buildPlatformBreakdown(shifts, expenses, emptySet()),
        shifts = shifts,
        dayCount = shifts.map { it.dateEpochDay }.distinct().size,
    )
}

fun epochRangeForDaysBack(daysBack: Int, today: LocalDate = LocalDate.now()): Pair<Long, Long> {
    val end = today.toEpochDay()
    val start = today.minusDays((daysBack - 1).toLong()).toEpochDay()
    return start to end
}
