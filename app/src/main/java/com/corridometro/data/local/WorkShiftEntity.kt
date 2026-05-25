package com.corridometro.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.corridometro.domain.Platform
import com.corridometro.domain.WorkShift

@Entity(tableName = "work_shifts")
data class WorkShiftEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
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

fun WorkShiftEntity.toDomain() = WorkShift(
    id = id,
    platform = platform,
    dateEpochDay = dateEpochDay,
    startMinutesOfDay = startMinutesOfDay,
    endMinutesOfDay = endMinutesOfDay,
    km = km,
    fuelKmPerLiter = fuelKmPerLiter,
    tripCount = tripCount,
    fuelPricePerLiter = fuelPricePerLiter,
    totalEarnings = totalEarnings,
    note = note,
)

fun WorkShift.toEntity() = WorkShiftEntity(
    id = id,
    platform = platform,
    dateEpochDay = dateEpochDay,
    startMinutesOfDay = startMinutesOfDay,
    endMinutesOfDay = endMinutesOfDay,
    km = km,
    fuelKmPerLiter = fuelKmPerLiter,
    tripCount = tripCount,
    fuelPricePerLiter = fuelPricePerLiter,
    totalEarnings = totalEarnings,
    note = note,
)
