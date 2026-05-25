package com.corridometro.data

import com.corridometro.data.local.AppDatabase
import com.corridometro.data.local.DayReportEntity
import com.corridometro.data.local.toDomain
import com.corridometro.data.local.toEntity
import com.corridometro.data.remote.CloudSyncService
import com.corridometro.domain.Expense
import com.corridometro.domain.JourneyExpenseCategories
import com.corridometro.domain.Platform
import com.corridometro.domain.WorkShift
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class CorridometroRepository(
    database: AppDatabase,
    private val cloudSync: CloudSyncService,
) {
    private val workShiftDao = database.workShiftDao()
    private val expenseDao = database.expenseDao()
    private val dayReportDao = database.dayReportDao()

    val workShifts: Flow<List<WorkShift>> =
        workShiftDao.observeAll().map { list -> list.map { it.toDomain() } }

    val expenses: Flow<List<Expense>> =
        expenseDao.observeAll().map { list -> list.map { it.toDomain() } }

    val finalizedDays: Flow<Set<Long>> =
        dayReportDao.observeAll().map { reports -> reports.map { it.dateEpochDay }.toSet() }

    val finalizedAtByDay: Flow<Map<Long, Long>> =
        dayReportDao.observeAll().map { reports ->
            reports.associate { it.dateEpochDay to it.finalizedAtEpochMillis }
        }

    suspend fun syncWithCloud(): Result<Unit> = cloudSync.syncAll()

    suspend fun getShiftsForDay(dateEpochDay: Long): List<WorkShift> =
        workShiftDao.getByDay(dateEpochDay).map { it.toDomain() }

    suspend fun getFinalizedAt(dateEpochDay: Long): Long? =
        dayReportDao.getByDay(dateEpochDay)?.finalizedAtEpochMillis

    suspend fun finalizeDay(dateEpochDay: Long) {
        dayReportDao.upsert(
            DayReportEntity(
                dateEpochDay = dateEpochDay,
                finalizedAtEpochMillis = System.currentTimeMillis(),
            ),
        )
    }

    suspend fun addWorkShift(shift: WorkShift) {
        val id = workShiftDao.insert(shift.copy(id = 0).toEntity())
        cloudSync.uploadShift(shift.copy(id = id))
    }

    suspend fun updateWorkShift(shift: WorkShift) {
        workShiftDao.insert(shift.toEntity())
        cloudSync.uploadShift(shift)
    }

    suspend fun replaceShiftsForDay(dateEpochDay: Long, shifts: List<WorkShift>) {
        val existing = workShiftDao.getByDay(dateEpochDay)
        existing.forEach {
            workShiftDao.delete(it)
            cloudSync.deleteShift(it.id)
        }
        shifts.forEach { shift ->
            val id = workShiftDao.insert(shift.copy(id = 0).toEntity())
            cloudSync.uploadShift(shift.copy(id = id))
        }
    }

    suspend fun addExpense(expense: Expense) {
        val id = expenseDao.insert(expense.copy(id = 0).toEntity())
        cloudSync.uploadExpense(expense.copy(id = id))
    }

    /**
     * Atualiza gastos de jornada só do [platform] informado; mantém gastos de outros apps no mesmo dia.
     */
    suspend fun mergeJourneyExpensesForDay(
        dateEpochDay: Long,
        platform: Platform,
        expenses: List<Expense>,
    ) {
        val journeyCategories = JourneyExpenseCategories.toSet()
        expenseDao.getByDay(dateEpochDay)
            .map { it.toDomain() }
            .filter { expense ->
                expense.category in journeyCategories &&
                    (expense.platform == null || expense.platform == platform)
            }
            .forEach { deleteExpense(it) }
        expenses
            .filter { it.amount > 0 }
            .map { it.copy(platform = platform) }
            .forEach { addExpense(it) }
    }

    suspend fun deleteWorkShift(shift: WorkShift) {
        workShiftDao.delete(shift.toEntity())
        cloudSync.deleteShift(shift.id)
    }

    suspend fun deleteExpense(expense: Expense) {
        expenseDao.delete(expense.toEntity())
        cloudSync.deleteExpense(expense.id)
    }
}
