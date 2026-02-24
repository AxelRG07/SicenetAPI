package com.example.sicenetapi.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface KardexDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertKardex(materias: List<KardexEntity>)

    @Query("SELECT * FROM kardex_alumno")
    fun getKardex(): Flow<List<KardexEntity>>

    @Query("DELETE FROM kardex_alumno")
    suspend fun borrarKardex()
}