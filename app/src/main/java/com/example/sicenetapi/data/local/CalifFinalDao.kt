package com.example.sicenetapi.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CalifFinalDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCalifFinal(materias: List<CalifFinalEntity>)

    @Query("SELECT * FROM calificacion_final")
    fun getCalifFinal(): Flow<List<CalifFinalEntity>>

    @Query("DELETE FROM calificacion_final")
    suspend fun borrarCalifFinal()
}