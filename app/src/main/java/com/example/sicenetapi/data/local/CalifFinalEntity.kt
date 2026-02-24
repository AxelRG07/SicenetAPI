package com.example.sicenetapi.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "calificacion_final")
data class CalifFinalEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val materia: String,
    val grupo: String,
    val calificacion: String,
    val acreditacion: String,
    val observaciones: String,
    val fechaSincronizacion: Long = System.currentTimeMillis()
)