package com.example.sicenetapi.workers

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.WorkerParameters
import com.example.sicenetapi.SicenetApplication

class FetchPerfilWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        // 1. Recibir los datos de entrada (Matrícula y Password)
        val matricula = inputData.getString("matricula") ?: return Result.failure()
        val password = inputData.getString("password") ?: return Result.failure()

        // 2. Obtener el repositorio manual desde AppContainer
        // usamos el contexto de la aplicación para llegar al contenedor.
        val appContainer = (applicationContext as SicenetApplication).container
        val repository = appContainer.sicenetRepository

        return try {
            //Petición de Red
            val loginResult = repository.login(matricula, password)

            if (loginResult.isSuccess) {
                val perfilResult = repository.getProfile()

                if (perfilResult.isSuccess) {
                    val alumno = perfilResult.getOrNull()!!

                    val cargaAcademicaResult = repository.getCargaAcademica()

                    val califFinalResult = repository.getCalifFinal(alumno.modEducativo)

                    Log.d("califFinal", califFinalResult.toString())

                    Log.d("cargaAcademica", cargaAcademicaResult.toString())

                    Log.d("califUnidades", repository.getCalifUnidades().toString())

                    // 4. Empaquetar los datos de salida para el siguiente Worker
                    val outputData = Data.Builder()
                        .putString("matricula", alumno.matricula)
                        .putString("contrasena", password)
                        .putString("nombre", alumno.nombre)
                        .putString("carrera", alumno.carrera)
                        .putString("semestre", alumno.semestre)
                        .putString("especialidad", alumno.especialidad)
                        .putString("estatus", alumno.estatus)
                        .putInt("lineamiento", alumno.lineamiento)
                        .putInt("modEducativo", alumno.modEducativo)
                        .build()

                    // Entregamos los datos al sistema para que se los pase al Worker 2
                    Log.d("worker1", alumno.nombre)
                    Result.success(outputData)

                } else {
                    // Falló la descarga del perfil, le decimos que lo reintente luego
                    Result.failure()
                }
            } else {
                // Credenciales inválidas
                Result.failure()
            }
        } catch (e: Exception) {
            // Error de conexión
            // Result.retry() hará que Android espere a que regrese el internet y lo intente de nuevo.
            Log.e("WorkerLogin", "El Worker falló por completo: ${e.message}")
            return Result.failure()
        }
    }
}