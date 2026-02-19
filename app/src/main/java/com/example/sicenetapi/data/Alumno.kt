package com.example.sicenetapi.data

data class Alumno(
    val nombre: String,
    var matricula: String,
    val carrera: String,
    val semestre: String,
    val especialidad: String,
    val estatus: String,
    val lineamiento: Int = 3,
    val modEducativo: Int = 2
)
