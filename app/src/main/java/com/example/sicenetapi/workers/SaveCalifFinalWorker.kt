package com.example.sicenetapi.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.sicenetapi.SicenetApplication
import com.example.sicenetapi.data.local.CalifFinalEntity

class SaveCalifFinalWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val csvString = inputData.getString("calif_final_csv") ?: return Result.failure()

        return try {
            val appContainer = (applicationContext as SicenetApplication).container
            val dao = appContainer.califFinalDao

            val califList = mutableListOf<CalifFinalEntity>()
            val timestamp = System.currentTimeMillis()

            if (csvString.isNotBlank()) {
                val materias = csvString.split(";;")

                for (fila in materias) {
                    if (fila.isBlank()) continue

                    val col = fila.split("|")
                    if (col.size >= 5) {
                        califList.add(
                            CalifFinalEntity(
                                materia = col[0],
                                grupo = col[1],
                                calificacion = col[2],
                                acreditacion = col[3],
                                observaciones = col[4],
                                fechaSincronizacion = timestamp
                            )
                        )
                    }
                }
            }

            dao.borrarCalifFinal()
            dao.insertCalifFinal(califList)

            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }
}