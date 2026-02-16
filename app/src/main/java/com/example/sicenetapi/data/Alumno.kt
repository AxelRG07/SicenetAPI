package com.example.sicenetapi.data

data class Alumno(
    val nombre: String,
    val matricula: String,
    val carrera: String,
    val semestre: String,
    val especialidad: String,
    val estatus: String,
    // Agregamos los campos clave para el Kardex y Calificaciones
    val lineamiento: Int = 3, // Valor por defecto basado en tu imagen
    val modEducativo: Int = 2
)
