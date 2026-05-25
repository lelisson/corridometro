package com.corridometro.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface DayReportDao {
    @Query("SELECT * FROM day_reports ORDER BY dateEpochDay DESC")
    fun observeAll(): Flow<List<DayReportEntity>>

    @Query("SELECT * FROM day_reports WHERE dateEpochDay = :dateEpochDay LIMIT 1")
    suspend fun getByDay(dateEpochDay: Long): DayReportEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: DayReportEntity)

    @Query("DELETE FROM day_reports WHERE dateEpochDay = :dateEpochDay")
    suspend fun deleteByDay(dateEpochDay: Long)
}
