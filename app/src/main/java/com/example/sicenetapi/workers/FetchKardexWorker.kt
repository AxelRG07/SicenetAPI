package com.example.sicenetapi.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.WorkerParameters
import com.example.sicenetapi.SicenetApplication
import kotlinx.coroutines.flow.firstOrNull
import org.json.JSONArray
import org.json.JSONObject

class FetchKardexWorker(
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

            val lineamiento = alumnoActual.lineamiento
            val matricula = alumnoActual.matricula
            val contrasena = alumnoActual.contrasena

            var result = repository.getKardex(lineamiento)

            if (result.isFailure && result.exceptionOrNull()?.message == "Servidor devolvió HTML") {
                val loginResult = repository.login(matricula, contrasena)

                if (loginResult.isSuccess) {
                    result = repository.getKardex(lineamiento)
                } else {
                    return Result.failure()
                }
            }

            if (result.isSuccess) {
                val materiasKardex = result.getOrNull()!!

                val sb = StringBuilder()
                for (materia in materiasKardex) {
                    val nombreLimpio = materia.materia.replace("|", " ").replace(";;", " ")
                    sb.append("${materia.claveMateria}|${nombreLimpio}|${materia.calificacion}|${materia.periodo}|${materia.creditos};;")
                }

                val outputData = Data.Builder()
                    .putString("kardex_csv", sb.toString())
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