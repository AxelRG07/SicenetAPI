package com.example.sicenetapi.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.sicenetapi.SicenetApplication
import com.example.sicenetapi.data.local.CargaAcademicaEntity
import org.json.JSONArray

class SaveCargaWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        // 1. Recibimos el paquete de texto del Worker 1
        val jsonString = inputData.getString("carga_json") ?: return Result.failure()

        return try {
            val appContainer = (applicationContext as SicenetApplication).container
            val dao = appContainer.cargaAcademicaDao

            // 2. Desempaquetamos el String de vuelta a una Lista de Kotlin
            val jsonArray = JSONArray(jsonString)
            val materiasList = mutableListOf<CargaAcademicaEntity>()

            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                materiasList.add(
                    CargaAcademicaEntity(
                        materia = obj.getString("materia"),
                        docente = obj.getString("docente"),
                        grupo = obj.getString("grupo"),
                        creditos = obj.getString("creditos"),
                        fechaSincronizacion = obj.getLong("fecha")
                    )
                )
            }

            dao.borrarCarga()

            dao.insertCargaAcademica(materiasList)

            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }
}