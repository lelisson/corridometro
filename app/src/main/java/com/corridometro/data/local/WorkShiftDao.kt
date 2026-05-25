package com.corridometro.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkShiftDao {
    @Query("SELECT * FROM work_shifts ORDER BY dateEpochDay DESC, startMinutesOfDay DESC, id DESC")
    fun observeAll(): Flow<List<WorkShiftEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: WorkShiftEntity): Long

    @Delete
    suspend fun delete(entity: WorkShiftEntity)

    @Query("SELECT * FROM work_shifts WHERE dateEpochDay = :dateEpochDay ORDER BY id ASC")
    suspend fun getByDay(dateEpochDay: Long): List<WorkShiftEntity>

    @Query("DELETE FROM work_shifts WHERE dateEpochDay = :dateEpochDay")
    suspend fun deleteByDay(dateEpochDay: Long)

    @Query("SELECT * FROM work_shifts")
    suspend fun getAllOnce(): List<WorkShiftEntity>

    @Query("DELETE FROM work_shifts")
    suspend fun deleteAll()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(entities: List<WorkShiftEntity>)

    suspend fun replaceAll(entities: List<WorkShiftEntity>) {
        deleteAll()
        if (entities.isNotEmpty()) insertAll(entities)
    }
}
