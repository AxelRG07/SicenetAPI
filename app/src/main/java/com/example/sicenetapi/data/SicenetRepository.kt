package com.example.sicenetapi.data

interface SicenetRepository {
    suspend fun login(matricula: String, contrasenia: String): Result<String>
    suspend fun getProfile(): Result<Alumno>
}

