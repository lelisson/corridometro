package com.corridometro.data.local

import androidx.room.TypeConverter
import com.corridometro.domain.ExpenseCategory
import com.corridometro.domain.Platform

class Converters {
    @TypeConverter
    fun fromPlatform(value: Platform): String = value.name

    @TypeConverter
    fun toPlatform(value: String): Platform = Platform.valueOf(value)

    @TypeConverter
    fun fromCategory(value: ExpenseCategory): String = value.name

    @TypeConverter
    fun toCategory(value: String): ExpenseCategory = ExpenseCategory.valueOf(value)
}
