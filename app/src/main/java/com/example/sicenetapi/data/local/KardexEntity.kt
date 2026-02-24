package com.example.sicenetapi.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "kardex_alumno")
data class KardexEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val claveMateria: String,
    val materia: String,
    val calificacion: String,
    val periodo: String,
    val creditos: String,
    val fechaSincronizacion: Long = System.currentTimeMillis()
)