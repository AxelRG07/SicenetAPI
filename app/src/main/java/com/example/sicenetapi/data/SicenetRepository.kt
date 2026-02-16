package com.example.sicenetapi.data

interface SicenetRepository {
    suspend fun login(matricula: String, contrasenia: String): Result<String>
    suspend fun getProfile(): Result<Alumno>
    suspend fun getCargaAcademica(): Result<String>
    suspend fun getKardex(lineamiento: Int): Result<String>
    suspend fun getCalifFinal(modEducativo: Int): Result<String>
    suspend fun getCalifUnidades(): Result<String>
}

