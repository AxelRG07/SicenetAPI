package com.example.sicenetapi.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.sicenetapi.SicenetApplication
import com.example.sicenetapi.data.local.CalifUnidadesEntity

class SaveCalifUnidadesWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val csvString = inputData.getString("calif_unidades_csv") ?: return Result.failure()

        return try {
            val appContainer = (applicationContext as SicenetApplication).container
            val dao = appContainer.califUnidadesDao

            val unidadesList = mutableListOf<CalifUnidadesEntity>()
            val timestamp = System.currentTimeMillis()

            if (csvString.isNotBlank()) {
                val materias = csvString.split(";;")

                for (fila in materias) {
                    if (fila.isBlank()) continue

                    val col = fila.split("|")
                    if (col.size >= 17) {
                        unidadesList.add(
                            CalifUnidadesEntity(
                                materia = col[0],
                                grupo = col[1],
                                observaciones = col[2],
                                unidadesActivas = col[3].toIntOrNull() ?: 0,
                                c1 = col[4], c2 = col[5], c3 = col[6], c4 = col[7], c5 = col[8],
                                c6 = col[9], c7 = col[10], c8 = col[11], c9 = col[12], c10 = col[13],
                                c11 = col[14], c12 = col[15], c13 = col[16],
                                fechaSincronizacion = timestamp
                            )
                        )
                    }
                }
            }

            dao.borrarCalifUnidades()
            dao.insertCalifUnidades(unidadesList)

            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }
}