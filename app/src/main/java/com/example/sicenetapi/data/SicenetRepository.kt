package com.example.sicenetapi.data

import com.example.sicenetapi.data.local.CalifFinalEntity
import com.example.sicenetapi.data.local.CalifUnidadesEntity
import com.example.sicenetapi.data.local.CargaAcademicaEntity
import com.example.sicenetapi.data.local.KardexEntity

interface SicenetRepository {
    suspend fun login(matricula: String, contrasenia: String): Result<String>
    suspend fun getProfile(): Result<Alumno>
    suspend fun getCargaAcademica(): Result<List<CargaAcademicaEntity>>
    suspend fun getKardex(lineamiento: Int): Result<List<KardexEntity>>
    suspend fun getCalifFinal(modEducativo: Int): Result<List<CalifFinalEntity>>
    suspend fun getCalifUnidades(): Result<List<CalifUnidadesEntity>>
}

