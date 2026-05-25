package com.corridometro.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.corridometro.domain.Expense
import com.corridometro.domain.ExpenseCategory
import com.corridometro.domain.Platform

@Entity(tableName = "expenses")
data class ExpenseEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val category: ExpenseCategory,
    val amount: Double,
    val dateEpochDay: Long,
    val platform: Platform? = null,
    val note: String? = null,
)

fun ExpenseEntity.toDomain() = Expense(
    id = id,
    category = category,
    amount = amount,
    dateEpochDay = dateEpochDay,
    platform = platform,
    note = note,
)

fun Expense.toEntity() = ExpenseEntity(
    id = id,
    category = category,
    amount = amount,
    dateEpochDay = dateEpochDay,
    platform = platform,
    note = note,
)
