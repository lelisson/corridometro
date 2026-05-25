package com.corridometro.data.remote

import com.corridometro.data.auth.GoogleAuthManager
import com.corridometro.data.local.AppDatabase
import com.corridometro.data.local.ExpenseEntity
import com.corridometro.data.local.WorkShiftEntity
import com.corridometro.data.local.toDomain
import com.corridometro.data.local.toEntity
import com.corridometro.domain.Expense
import com.corridometro.domain.ExpenseCategory
import com.corridometro.domain.Platform
import com.corridometro.domain.WorkShift
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await

class CloudSyncService(
    private val database: AppDatabase,
    private val authManager: GoogleAuthManager,
) {
    private val firestore: FirebaseFirestore? = runCatching { FirebaseFirestore.getInstance() }.getOrNull()

    private fun userCollection(name: String): com.google.firebase.firestore.CollectionReference? {
        val uid = authManager.userId ?: return null
        return firestore?.collection("users")?.document(uid)?.collection(name)
    }

    suspend fun syncAll(): Result<Unit> {
        if (!authManager.isCloudConfigured || !authManager.isSignedIn) {
            return Result.failure(IllegalStateException("Entre com a conta Google para sincronizar."))
        }
        return runCatching {
            pullFromCloud()
            pushLocalToCloud()
        }
    }

    private suspend fun pullFromCloud() {
        val shiftsCol = userCollection("work_shifts") ?: return
        val expensesCol = userCollection("expenses") ?: return

        val shiftDocs = shiftsCol.get().await().documents
        val expenseDocs = expensesCol.get().await().documents

        val shifts = shiftDocs.mapNotNull { it.toWorkShift() }
        val expenses = expenseDocs.mapNotNull { it.toExpense() }

        database.workShiftDao().replaceAll(shifts.map { it.toEntity() })
        database.expenseDao().replaceAll(expenses.map { it.toEntity() })
    }

    private suspend fun pushLocalToCloud() {
        val shiftsCol = userCollection("work_shifts") ?: return
        val expensesCol = userCollection("expenses") ?: return

        val shifts = database.workShiftDao().getAllOnce().map { it.toDomain() }
        val expenses = database.expenseDao().getAllOnce().map { it.toDomain() }

        shifts.forEach { shift ->
            shiftsCol.document(shift.id.toString()).set(shift.toCloudMap(), SetOptions.merge()).await()
        }
        expenses.forEach { expense ->
            expensesCol.document(expense.id.toString()).set(expense.toCloudMap(), SetOptions.merge()).await()
        }
    }

    suspend fun uploadShift(shift: WorkShift) {
        if (!authManager.isSignedIn) return
        val col = userCollection("work_shifts") ?: return
        col.document(shift.id.toString()).set(shift.toCloudMap(), SetOptions.merge()).await()
    }

    suspend fun uploadExpense(expense: Expense) {
        if (!authManager.isSignedIn) return
        val col = userCollection("expenses") ?: return
        col.document(expense.id.toString()).set(expense.toCloudMap(), SetOptions.merge()).await()
    }

    suspend fun deleteShift(shiftId: Long) {
        if (!authManager.isSignedIn) return
        userCollection("work_shifts")?.document(shiftId.toString())?.delete()?.await()
    }

    suspend fun deleteExpense(expenseId: Long) {
        if (!authManager.isSignedIn) return
        userCollection("expenses")?.document(expenseId.toString())?.delete()?.await()
    }

    private fun WorkShift.toCloudMap(): Map<String, Any?> = mapOf(
        "platform" to platform.name,
        "dateEpochDay" to dateEpochDay,
        "startMinutesOfDay" to startMinutesOfDay,
        "endMinutesOfDay" to endMinutesOfDay,
        "km" to km,
        "fuelKmPerLiter" to fuelKmPerLiter,
        "tripCount" to tripCount,
        "fuelPricePerLiter" to fuelPricePerLiter,
        "totalEarnings" to totalEarnings,
        "note" to note,
    )

    private fun Expense.toCloudMap(): Map<String, Any?> = mapOf(
        "category" to category.name,
        "amount" to amount,
        "dateEpochDay" to dateEpochDay,
        "platform" to platform?.name,
        "note" to note,
    )

    private fun com.google.firebase.firestore.DocumentSnapshot.toWorkShift(): WorkShift? {
        val id = id.toLongOrNull() ?: return null
        val platformName = getString("platform") ?: return null
        return WorkShift(
            id = id,
            platform = Platform.valueOf(platformName),
            dateEpochDay = getLong("dateEpochDay") ?: return null,
            startMinutesOfDay = getLong("startMinutesOfDay")?.toInt() ?: return null,
            endMinutesOfDay = getLong("endMinutesOfDay")?.toInt() ?: return null,
            km = getDouble("km") ?: return null,
            fuelKmPerLiter = getDouble("fuelKmPerLiter") ?: return null,
            tripCount = getLong("tripCount")?.toInt() ?: return null,
            fuelPricePerLiter = getDouble("fuelPricePerLiter") ?: return null,
            totalEarnings = getDouble("totalEarnings") ?: return null,
            note = getString("note"),
        )
    }

    private fun com.google.firebase.firestore.DocumentSnapshot.toExpense(): Expense? {
        val id = id.toLongOrNull() ?: return null
        val categoryName = getString("category") ?: return null
        val platformName = getString("platform")
        return Expense(
            id = id,
            category = ExpenseCategory.valueOf(categoryName),
            amount = getDouble("amount") ?: return null,
            dateEpochDay = getLong("dateEpochDay") ?: return null,
            platform = platformName?.let { Platform.valueOf(it) },
            note = getString("note"),
        )
    }
}
