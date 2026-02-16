package com.example.sicenetapi.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "perfil_alumno")
data class AlumnoEntity(
    @PrimaryKey
    val matricula: String,
    val nombre: String,
    val carrera: String,
    val semestre: String,
    val especialidad: String,
    val estatus: String,
    val lineamiento: Int,
    val modEducativo: Int,
    val fechaSincronizacion: Long
)