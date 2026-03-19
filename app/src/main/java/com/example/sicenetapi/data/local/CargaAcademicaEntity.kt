package com.example.sicenetapi.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "carga_academica")
data class CargaAcademicaEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val materia: String,
    val docente: String,
    val grupo: String,
    val creditos: String,
    val lunes: String = "",
    val martes: String = "",
    val miercoles: String = "",
    val jueves: String = "",
    val viernes: String = "",
    val fechaSincronizacion: Long = System.currentTimeMillis()
)