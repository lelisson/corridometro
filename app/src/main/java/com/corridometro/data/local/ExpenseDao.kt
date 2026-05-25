package com.corridometro.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpenseDao {
    @Query("SELECT * FROM expenses ORDER BY dateEpochDay DESC, id DESC")
    fun observeAll(): Flow<List<ExpenseEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: ExpenseEntity): Long

    @Delete
    suspend fun delete(entity: ExpenseEntity)

    @Query("SELECT * FROM expenses WHERE dateEpochDay = :dateEpochDay")
    suspend fun getByDay(dateEpochDay: Long): List<ExpenseEntity>

    @Query("SELECT * FROM expenses")
    suspend fun getAllOnce(): List<ExpenseEntity>

    @Query("DELETE FROM expenses")
    suspend fun deleteAll()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(entities: List<ExpenseEntity>)

    suspend fun replaceAll(entities: List<ExpenseEntity>) {
        deleteAll()
        if (entities.isNotEmpty()) insertAll(entities)
    }
}
