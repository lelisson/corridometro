package com.corridometro.domain

import java.time.LocalDate

fun LocalDate.toEpochDayLong(): Long = toEpochDay()

fun Long.toLocalDate(): LocalDate = LocalDate.ofEpochDay(this)

fun shiftDurationMinutes(startMinutesOfDay: Int, endMinutesOfDay: Int): Int =
    if (endMinutesOfDay >= startMinutesOfDay) {
        endMinutesOfDay - startMinutesOfDay
    } else {
        (24 * 60 - startMinutesOfDay) + endMinutesOfDay
    }

fun fuelLitersUsed(km: Double, fuelKmPerLiter: Double): Double =
    if (fuelKmPerLiter > 0 && km > 0) km / fuelKmPerLiter else 0.0

fun fuelCostForShift(shift: WorkShift): Double =
    fuelLitersUsed(shift.km, shift.fuelKmPerLiter) * shift.fuelPricePerLiter

fun netProfitForShift(shift: WorkShift): Double =
    shift.totalEarnings - fuelCostForShift(shift)

fun profitPerKmForShift(shift: WorkShift): Double =
    if (shift.km > 0) netProfitForShift(shift) / shift.km else 0.0

fun profitPerHourForShift(shift: WorkShift): Double {
    val minutes = shiftDurationMinutes(shift.startMinutesOfDay, shift.endMinutesOfDay)
    val hours = minutes / 60.0
    return if (hours > 0) netProfitForShift(shift) / hours else 0.0
}

fun avgEarningPerTrip(shift: WorkShift): Double =
    if (shift.tripCount > 0) shift.totalEarnings / shift.tripCount else 0.0

fun isInPeriod(
    dateEpochDay: Long,
    period: PeriodFilter,
    customRangeStart: Long? = null,
    customRangeEnd: Long? = null,
    today: LocalDate = LocalDate.now(),
): Boolean {
    if (period == PeriodFilter.PERSONALIZADO) {
        val start = customRangeStart ?: return true
        val end = customRangeEnd ?: return true
        return dateEpochDay in minOf(start, end)..maxOf(start, end)
    }
    if (period == PeriodFilter.TUDO) return true
    val date = dateEpochDay.toLocalDate()
    return when (period) {
        PeriodFilter.HOJE -> date == today
        PeriodFilter.SEMANA -> !date.isBefore(today.minusDays(7))
        PeriodFilter.MES -> !date.isBefore(today.minusDays(30))
        PeriodFilter.TUDO -> true
        PeriodFilter.PERSONALIZADO -> true
    }
}

/** [platforms] vazio = todas as plataformas; caso contrário, só as listadas. */
fun filterShifts(
    shifts: List<WorkShift>,
    period: PeriodFilter,
    platforms: Set<Platform>,
    customRangeStart: Long? = null,
    customRangeEnd: Long? = null,
): List<WorkShift> = shifts.filter { shift ->
    isInPeriod(shift.dateEpochDay, period, customRangeStart, customRangeEnd) &&
        (platforms.isEmpty() || shift.platform in platforms)
}

fun filterExpenses(
    expenses: List<Expense>,
    period: PeriodFilter,
    platforms: Set<Platform>,
    customRangeStart: Long? = null,
    customRangeEnd: Long? = null,
): List<Expense> = expenses.filter { expense ->
    isInPeriod(expense.dateEpochDay, period, customRangeStart, customRangeEnd) &&
        (platforms.isEmpty() || expense.platform == null || expense.platform in platforms)
}

fun buildPlatformBreakdown(
    shifts: List<WorkShift>,
    expenses: List<Expense>,
    platformFilter: Set<Platform> = emptySet(),
): List<Pair<Platform, Summary>> = Platform.entries.map { platform ->
    platform to calculateSummary(
        shifts.filter { it.platform == platform },
        expenses.filter { it.platform == null || it.platform == platform },
    )
}.filter { (platform, summary) ->
    (summary.shiftCount > 0 || summary.otherExpenses > 0) &&
        (platformFilter.isEmpty() || platform in platformFilter)
}

fun calculateSummary(shifts: List<WorkShift>, expenses: List<Expense>): Summary {
    val grossEarnings = shifts.sumOf { it.totalEarnings }
    val fuelCost = shifts.sumOf { fuelCostForShift(it) }
    val otherExpenses = expenses.sumOf { it.amount }
    val totalExpenses = fuelCost + otherExpenses
    val profit = grossEarnings - totalExpenses
    val totalKm = shifts.sumOf { it.km }
    val profitPerKm = if (totalKm > 0) profit / totalKm else 0.0
    val tripCount = shifts.sumOf { it.tripCount }
    val totalMinutes = shifts.sumOf {
        shiftDurationMinutes(it.startMinutesOfDay, it.endMinutesOfDay)
    }
    val profitPerHour = if (totalMinutes > 0) profit / (totalMinutes / 60.0) else 0.0

    return Summary(
        grossEarnings = grossEarnings,
        fuelCost = fuelCost,
        otherExpenses = otherExpenses,
        expenses = totalExpenses,
        profit = profit,
        totalKm = totalKm,
        profitPerKm = profitPerKm,
        tripCount = tripCount,
        shiftCount = shifts.size,
        totalMinutes = totalMinutes,
        profitPerHour = profitPerHour,
    )
}
