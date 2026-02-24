package com.example.sicenetapi.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CargaAcademicaDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCargaAcademica(materias: List<CargaAcademicaEntity>)

    @Query("SELECT * FROM carga_academica")
    fun getCargaAcademica(): Flow<List<CargaAcademicaEntity>>

    @Query("DELETE FROM carga_academica")
    suspend fun borrarCarga()
}