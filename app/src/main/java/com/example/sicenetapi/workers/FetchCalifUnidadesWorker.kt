package com.example.sicenetapi.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.WorkerParameters
import com.example.sicenetapi.SicenetApplication
import kotlinx.coroutines.flow.firstOrNull

class FetchCalifUnidadesWorker(
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

            val matricula = alumnoActual.matricula
            val contrasena = alumnoActual.contrasena

            // 1. Primer intento
            var result = repository.getCalifUnidades()

            if (result.isFailure && result.exceptionOrNull()?.message == "Servidor devolvió HTML") {
                val loginResult = repository.login(matricula, contrasena)
                if (loginResult.isSuccess) {
                    result = repository.getCalifUnidades() // Segundo intento
                } else {
                    return Result.failure()
                }
            }

            if (result.isSuccess) {
                val listaUnidades = result.getOrNull()!!

                val sb = StringBuilder()
                for (item in listaUnidades) {
                    val matLimpia = item.materia.replace("|", " ").replace(";;", " ")
                    val obsLimpia = item.observaciones.replace("|", " ").replace(";;", " ")

                    sb.append("${matLimpia}|${item.grupo}|${obsLimpia}|${item.unidadesActivas}|")
                    sb.append("${item.c1}|${item.c2}|${item.c3}|${item.c4}|${item.c5}|")
                    sb.append("${item.c6}|${item.c7}|${item.c8}|${item.c9}|${item.c10}|")
                    sb.append("${item.c11}|${item.c12}|${item.c13};;")
                }

                val outputData = Data.Builder()
                    .putString("calif_unidades_csv", sb.toString())
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