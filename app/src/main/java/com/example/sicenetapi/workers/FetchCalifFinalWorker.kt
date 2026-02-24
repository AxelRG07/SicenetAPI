package com.example.sicenetapi.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.WorkerParameters
import com.example.sicenetapi.SicenetApplication
import kotlinx.coroutines.flow.firstOrNull

class FetchCalifFinalWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val appContainer = (applicationContext as SicenetApplication).container
        val repository = appContainer.sicenetRepository
        val alumnoDao = appContainer.alumnoDao

        return try {
            val alumnoActual = alumnoDao.getPerfilActual().firstOrNull()
            if (alumnoActual == null) return Result.failure()

            val modEducativo = alumnoActual.modEducativo
            val matricula = alumnoActual.matricula
            val contrasena = alumnoActual.contrasena

            var result = repository.getCalifFinal(modEducativo)

            if (result.isFailure && result.exceptionOrNull()?.message == "Servidor devolvió HTML") {
                val loginResult = repository.login(matricula, contrasena)
                if (loginResult.isSuccess) {
                    result = repository.getCalifFinal(modEducativo) // Segundo intento
                } else {
                    return Result.failure()
                }
            }

            if (result.isSuccess) {
                val listaCalif = result.getOrNull()!!

                val sb = StringBuilder()
                for (item in listaCalif) {
                    val matLimpia = item.materia.replace("|", " ").replace(";;", " ")
                    val obsLimpia = item.observaciones.replace("|", " ").replace(";;", " ")

                    sb.append("${matLimpia}|${item.grupo}|${item.calificacion}|${item.acreditacion}|${obsLimpia};;")
                }

                val outputData = Data.Builder()
                    .putString("calif_final_csv", sb.toString())
                    .build()

                Result.success(outputData)
            } else {
                Result.failure()
            }
        } catch (e: Exception) {
            Result.retry()
        }
    }
}