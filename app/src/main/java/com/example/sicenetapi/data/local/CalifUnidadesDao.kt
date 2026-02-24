package com.example.sicenetapi.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CalifUnidadesDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCalifUnidades(materias: List<CalifUnidadesEntity>)

    @Query("SELECT * FROM calif_unidades")
    fun getCalifUnidades(): Flow<List<CalifUnidadesEntity>>

    @Query("DELETE FROM calif_unidades")
    suspend fun borrarCalifUnidades()
}