package com.corridometro.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "day_reports")
data class DayReportEntity(
    @PrimaryKey val dateEpochDay: Long,
    val finalizedAtEpochMillis: Long,
)
