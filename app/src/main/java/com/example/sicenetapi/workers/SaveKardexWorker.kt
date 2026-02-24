package com.example.sicenetapi.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.sicenetapi.SicenetApplication
import com.example.sicenetapi.data.local.KardexEntity

class SaveKardexWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val csvString = inputData.getString("kardex_csv") ?: return Result.failure()

        return try {
            val appContainer = (applicationContext as SicenetApplication).container
            val dao = appContainer.kardexDao

            val kardexList = mutableListOf<KardexEntity>()
            val timestamp = System.currentTimeMillis()

            // 1. Separamos por materias (cada materia termina en ;;)
            if (csvString.isNotBlank()) {
                val materias = csvString.split(";;")

                for (fila in materias) {
                    if (fila.isBlank()) continue // Ignoramos el último corte vacío

                    // 2. Separamos las columnas de cada materia (separadas por |)
                    val columnas = fila.split("|")
                    if (columnas.size >= 5) {
                        kardexList.add(
                            KardexEntity(
                                claveMateria = columnas[0],
                                materia = columnas[1],
                                calificacion = columnas[2],
                                periodo = columnas[3],
                                creditos = columnas[4],
                                fechaSincronizacion = timestamp
                            )
                        )
                    }
                }
            }

            // 3. Limpiamos y guardamos
            dao.borrarKardex()
            dao.insertKardex(kardexList)

            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }
}