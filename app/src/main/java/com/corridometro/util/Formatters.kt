package com.corridometro.util

import java.text.NumberFormat
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.Locale

private val br = Locale.forLanguageTag("pt-BR")
private val currencyFormat = NumberFormat.getCurrencyInstance(br)
private val dateFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy", br)

fun formatCurrency(value: Double): String = currencyFormat.format(value)

fun formatKm(value: Double): String =
    "${String.format(br, "%.1f", value)} km"

fun formatLiters(value: Double): String =
    "${String.format(br, "%.2f", value)} L"

fun formatConsumption(kmPerLiter: Double): String =
    "${String.format(br, "%.1f", kmPerLiter)} km/L"

fun formatDate(epochDay: Long): String =
    LocalDate.ofEpochDay(epochDay).format(dateFormat)

fun formatDateInput(date: LocalDate = LocalDate.now()): String =
    date.format(dateFormat)

/** O DatePicker do Material usa meia-noite em UTC; converter com fuso local atrasa um dia no Brasil. */
fun localDateToPickerMillis(date: LocalDate): Long =
    date.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()

fun pickerMillisToLocalDate(millis: Long): LocalDate =
    Instant.ofEpochMilli(millis).atZone(ZoneOffset.UTC).toLocalDate()

fun parseDateInput(text: String): LocalDate? {
    val trimmed = text.trim()
    if (trimmed.isEmpty()) return null
    return try {
        LocalDate.parse(trimmed, dateFormat)
    } catch (_: DateTimeParseException) {
        null
    }
}

fun formatTime(minutesOfDay: Int): String {
    val h = minutesOfDay / 60
    val m = minutesOfDay % 60
    return "%02d:%02d".format(br, h, m)
}

fun formatDuration(totalMinutes: Int): String {
    if (totalMinutes <= 0) return "0 min"
    val h = totalMinutes / 60
    val m = totalMinutes % 60
    return if (h > 0) "${h}h ${m}min" else "${m}min"
}

fun parseAmount(text: String): Double {
    val normalized = text.trim().replace(".", "").replace(",", ".")
    return normalized.toDoubleOrNull() ?: 0.0
}

fun formatAmountInput(value: Double): String =
    if (value <= 0) "" else String.format(br, "%.2f", value).replace('.', ',')

fun parseTime(text: String): Int? {
    val trimmed = text.trim()
    if (trimmed.isEmpty()) return null
    val parts = trimmed.split(":", ".")
    if (parts.size != 2) return null
    val h = parts[0].toIntOrNull() ?: return null
    val m = parts[1].toIntOrNull() ?: return null
    if (h !in 0..23 || m !in 0..59) return null
    return h * 60 + m
}

fun parseIntAmount(text: String): Int =
    text.trim().toIntOrNull() ?: 0
