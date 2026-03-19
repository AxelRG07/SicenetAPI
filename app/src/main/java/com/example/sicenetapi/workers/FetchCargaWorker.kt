package com.example.sicenetapi.workers

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.WorkerParameters
import com.example.sicenetapi.SicenetApplication
import kotlinx.coroutines.flow.firstOrNull
import org.json.JSONArray
import org.json.JSONObject

class FetchCargaWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val appContainer = (applicationContext as SicenetApplication).container
        val repository = appContainer.sicenetRepository
        val alumnoDao = appContainer.alumnoDao // ¡NUEVO: Acceso a la BD local!

        return try {
            // 1. Buscamos las credenciales en SQLite
            val alumnoActual = alumnoDao.getPerfilActual().firstOrNull()
            if (alumnoActual == null) return Result.failure()

            val matricula = alumnoActual.matricula
            val contrasena = alumnoActual.contrasena

            var result = repository.getCargaAcademica()
            Log.d("WorkerCarga", "Resultado inicial: $result")

            val errorMessage = result.exceptionOrNull()?.message ?: ""

            if (result.isFailure && errorMessage.contains("HTML", ignoreCase = true)) {
                try {
                    val loginResult = repository.login(matricula, contrasena)
                    Log.d("WorkerReconexion", "Resultado del Login: $loginResult")

                    if (loginResult.isSuccess) {
                        result = repository.getCargaAcademica() // Segundo intento
                    } else {
                        return Result.failure()
                    }
                } catch (e: Exception) {
                    return Result.failure()
                }
            }

            if (result.isSuccess) {
                val materias = result.getOrNull()!!

                val jsonArray = JSONArray()
                for (materia in materias) {
                    val obj = JSONObject()
                    obj.put("materia", materia.materia)
                    obj.put("docente", materia.docente)
                    obj.put("grupo", materia.grupo)
                    obj.put("creditos", materia.creditos)
                    obj.put("lunes", materia.lunes)
                    obj.put("martes", materia.martes)
                    obj.put("miercoles", materia.miercoles)
                    obj.put("jueves", materia.jueves)
                    obj.put("viernes", materia.viernes)
                    obj.put("fecha", materia.fechaSincronizacion)
                    jsonArray.put(obj)
                }

                val outputData = Data.Builder()
                    .putString("carga_json", jsonArray.toString())
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