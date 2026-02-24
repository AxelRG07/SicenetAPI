package com.example.sicenetapi.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.WorkerParameters
import com.example.sicenetapi.SicenetApplication
import org.json.JSONArray
import org.json.JSONObject

class FetchCargaWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val appContainer = (applicationContext as SicenetApplication).container
        val repository = appContainer.sicenetRepository

        return try {
            // 1. Pedimos la Carga Académica a la red
            val result = repository.getCargaAcademica()

            if (result.isSuccess) {
                val materias = result.getOrNull()!!

                // 2. Empaquetamos la lista de entidades en un String (JSON Array)
                // para poder pasarlo por el de WorkManager sin romper el límite de 10KB
                val jsonArray = JSONArray()
                for (materia in materias) {
                    val obj = JSONObject()
                    obj.put("materia", materia.materia)
                    obj.put("docente", materia.docente)
                    obj.put("grupo", materia.grupo)
                    obj.put("creditos", materia.creditos)
                    obj.put("fecha", materia.fechaSincronizacion)
                    jsonArray.put(obj)
                }

                // 3. Lo metemos en los datos de salida
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