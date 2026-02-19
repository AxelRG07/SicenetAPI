package com.example.sicenetapi.ui.screens

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.example.sicenet.workers.SavePerfilWorker
import com.example.sicenetapi.SicenetApplication
import com.example.sicenetapi.data.Alumno
import com.example.sicenetapi.data.SicenetRepository
import com.example.sicenetapi.data.local.AlumnoDao
import com.example.sicenetapi.workers.FetchPerfilWorker
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(
    private val repository: SicenetRepository,
    private val alumnoDao: AlumnoDao,
    private val workManager: WorkManager
) : ViewModel() {

    var alumnoData by mutableStateOf<Alumno?>(null)
        private set

    var errorMessage by mutableStateOf("")

    var isLoading by mutableStateOf(false)
        private set

    val alumnoLocal = alumnoDao.getPerfilActual()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    /*fun autenticar(mat: String, pass: String, onSuccesss: () -> Unit) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = ""

            val loginResult = repository.login(mat, pass)

            loginResult.fold(
                onSuccess = {
                    //obtenemos el perfil completo con el metodo getProfile
                    val perfilResult = repository.getProfile()
                    perfilResult.onSuccess { alumno ->
                        alumnoData = alumno
                        onSuccesss()
                    }.onFailure {
                        errorMessage = "Error al bajar perfil: ${it.message}"
                    }
                },
                onFailure = {
                    errorMessage = "Fallo el login. Revisa tus credenciales."
                }
            )
            isLoading = false
        }
    }*/

    fun iniciarSincronizacionPerfil(matricula: String, pass: String, onSuccess: () -> Unit) {

        if (matricula.isBlank() || pass.isBlank()) {
            errorMessage = "Por favor, ingresa tus credenciales."
            return
        }

        alumnoData?.matricula = matricula

        // 1. Preparamos los datos iniciales para el primer Worker (las credenciales)
        val inputCredenciales = Data.Builder()
            .putString("matricula", matricula)
            .putString("password", pass)
            .build()

        // 2. Creamos la petición para el Worker 1 (Descargar)
        val fetchRequest = OneTimeWorkRequestBuilder<FetchPerfilWorker>()
            .setInputData(inputCredenciales)
            .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
            .build()

        // 3. Creamos la petición para el Worker 2 (Guardar)
        val saveRequest = OneTimeWorkRequestBuilder<SavePerfilWorker>()
            .build()

        val workName = "SyncPerfil"

        // 4. Encadenamiento de WorkManager: Descargar -> Guardar
        workManager.beginUniqueWork(workName, ExistingWorkPolicy.REPLACE, fetchRequest)
            .then(saveRequest)
            .enqueue()

        // 5. Monitoreo
        viewModelScope.launch {
            // Escuchamos el flujo de estados de este trabajo específico
            workManager.getWorkInfosForUniqueWorkFlow(workName).collect { workInfos ->
                if (workInfos.isNotEmpty()) {
                    // workInfos contiene el estado de todos los workers en la cadena.
                    // Verificamos si toda la cadena terminó con éxito o si falló.
                    val isFinished = workInfos.all { it.state.isFinished }
                    val hasFailed = workInfos.any { it.state == WorkInfo.State.FAILED }

                    if (isFinished) {
                        isLoading = false
                        if (hasFailed) {
                            errorMessage = "Error de sincronización. Verifica tus datos o conexión."
                        } else {
                            // El Worker 2 guardó en la DB.
                            // Ejecutamos la navegación a la siguiente pantalla.
                            onSuccess()
                        }
                    } else {
                        isLoading = true
                    }
                }
            }
        }
    }

    fun cerrarSesion() {
        viewModelScope.launch {
            alumnoDao.borrarSesion()
        }
    }


    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                // Buscamos nuestra aplicación
                val application = (this[APPLICATION_KEY] as SicenetApplication)
                // Sacamos el repositorio del contenedor
                val sicenetRepository = application.container.sicenetRepository
                val appContainer = application.container


                // Creamos e inyectamos el ViewModel
                MainViewModel(
                    repository = sicenetRepository,
                    alumnoDao = appContainer.alumnoDao,
                    workManager = WorkManager.getInstance(application.applicationContext)
                )
            }
        }
    }
}