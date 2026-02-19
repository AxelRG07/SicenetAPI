package com.example.sicenet.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.sicenetapi.SicenetApplication
import com.example.sicenetapi.data.local.AlumnoEntity

class SavePerfilWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        // 1. Extraemos los datos que nos pasó el FetchPerfilWorker
        // inputData contiene lo que el worker anterior puso en su outputData
        val matricula = inputData.getString("matricula") ?: return Result.failure()
        val nombre = inputData.getString("nombre") ?: ""
        val carrera = inputData.getString("carrera") ?: ""
        val semestre = inputData.getString("semestre") ?: ""
        val especialidad = inputData.getString("especialidad") ?: ""
        val estatus = inputData.getString("estatus") ?: ""
        val lineamiento = inputData.getInt("lineamiento", 3)
        val modEducativo = inputData.getInt("modEducativo", 2)

        return try {
            // 2. Obtenemos nuestro DAO desde el AppContainer
            val appContainer = (applicationContext as SicenetApplication).container
            val alumnoDao = appContainer.alumnoDao

            // 3. Creamos la entidad para la base de datos
            val alumnoEntity = AlumnoEntity(
                matricula = matricula,
                nombre = nombre,
                carrera = carrera,
                semestre = semestre,
                especialidad = especialidad,
                estatus = estatus,
                lineamiento = lineamiento,
                modEducativo = modEducativo,
                fechaSincronizacion = System.currentTimeMillis()
            )

            alumnoDao.borrarSesion()

            // 4. Guardamos en la base de datos local
            alumnoDao.insertPerfil(alumnoEntity)

            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }
}