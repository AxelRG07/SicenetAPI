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
import com.example.sicenetapi.data.AppContainer
import com.example.sicenetapi.data.SicenetRepository
import com.example.sicenetapi.data.local.AlumnoDao
import com.example.sicenetapi.data.local.CargaAcademicaDao
import com.example.sicenetapi.workers.FetchCalifFinalWorker
import com.example.sicenetapi.workers.FetchCalifUnidadesWorker
import com.example.sicenetapi.workers.FetchCargaWorker
import com.example.sicenetapi.workers.FetchKardexWorker
import com.example.sicenetapi.workers.FetchPerfilWorker
import com.example.sicenetapi.workers.SaveCalifFinalWorker
import com.example.sicenetapi.workers.SaveCalifUnidadesWorker
import com.example.sicenetapi.workers.SaveCargaWorker
import com.example.sicenetapi.workers.SaveKardexWorker
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(
    private val repository: SicenetRepository,
    private val container: AppContainer,
    private val workManager: WorkManager
) : ViewModel() {

    var alumnoData by mutableStateOf<Alumno?>(null)
        private set

    var errorMessage by mutableStateOf("")

    var isLoading by mutableStateOf(false)
        private set

    val alumnoLocal = container.alumnoDao.getPerfilActual()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val cargaAcademicaLocal = container.cargaAcademicaDao.getCargaAcademica()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val kardexLocal = container.kardexDao.getKardex()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val califFinalLocal = container.califFinalDao.getCalifFinal()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val califUnidadesLocal = container.califUnidadesDao.getCalifUnidades()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

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

    fun sincronizarCargaAcademica() {
        isLoading = true
        errorMessage = ""

        // 1. Creamos la petición 1
        val fetchRequest = OneTimeWorkRequestBuilder<FetchCargaWorker>()
            .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
            .build()

        // 2. Creamos la petición 2
        val saveRequest = OneTimeWorkRequestBuilder<SaveCargaWorker>().build()

        val workName = "SyncCarga"

        // 3. Encadenamos (Chaining)
        workManager.beginUniqueWork(workName, ExistingWorkPolicy.REPLACE, fetchRequest)
            .then(saveRequest)
            .enqueue()

        viewModelScope.launch {
            workManager.getWorkInfosForUniqueWorkFlow(workName).collect { workInfos ->
                if (workInfos.isNotEmpty()) {
                    val isFinished = workInfos.all { it.state.isFinished }
                    val hasFailed = workInfos.any { it.state == WorkInfo.State.FAILED }

                    if (isFinished) {
                        isLoading = false
                        if (hasFailed) {
                            errorMessage = "Error al obtener la carga académica."
                        }
                    } else {
                        isLoading = true
                    }
                }
            }
        }
    }

    fun sincronizarKardex() {
        isLoading = true
        errorMessage = ""

        val fetchRequest = OneTimeWorkRequestBuilder<FetchKardexWorker>()
            .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
            .build()

        val saveRequest = OneTimeWorkRequestBuilder<SaveKardexWorker>().build()

        val workName = "SyncKardex"

        workManager.beginUniqueWork(workName, ExistingWorkPolicy.REPLACE, fetchRequest)
            .then(saveRequest)
            .enqueue()

        viewModelScope.launch {
            workManager.getWorkInfosForUniqueWorkFlow(workName).collect { workInfos ->
                if (workInfos.isNotEmpty()) {
                    val isFinished = workInfos.all { it.state.isFinished }
                    val hasFailed = workInfos.any { it.state == WorkInfo.State.FAILED }

                    if (isFinished) {
                        isLoading = false
                        if (hasFailed) {
                            errorMessage = "Error al obtener el Kardex."
                        }
                    } else {
                        isLoading = true
                    }
                }
            }
        }
    }

    fun sincronizarCalifFinal() {
        isLoading = true
        errorMessage = ""

        val fetchRequest = OneTimeWorkRequestBuilder<FetchCalifFinalWorker>()
            .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
            .build()

        val saveRequest = OneTimeWorkRequestBuilder<SaveCalifFinalWorker>().build()

        val workName = "SyncCalifFinal"

        workManager.beginUniqueWork(workName, ExistingWorkPolicy.REPLACE, fetchRequest)
            .then(saveRequest)
            .enqueue()

        // Monitoreo
        viewModelScope.launch {
            workManager.getWorkInfosForUniqueWorkFlow(workName).collect { workInfos ->
                if (workInfos.isNotEmpty()) {
                    val isFinished = workInfos.all { it.state.isFinished }
                    val hasFailed = workInfos.any { it.state == WorkInfo.State.FAILED }

                    if (isFinished) {
                        isLoading = false
                        if (hasFailed) {
                            errorMessage = "Error al obtener Calificaciones Finales."
                        }
                    } else {
                        isLoading = true
                    }
                }
            }
        }
    }

    fun sincronizarCalifUnidades() {
        isLoading = true
        errorMessage = ""

        val fetchRequest = OneTimeWorkRequestBuilder<FetchCalifUnidadesWorker>()
            .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
            .build()

        val saveRequest = OneTimeWorkRequestBuilder<SaveCalifUnidadesWorker>().build()

        val workName = "SyncCalifUnidades"

        workManager.beginUniqueWork(workName, ExistingWorkPolicy.REPLACE, fetchRequest)
            .then(saveRequest)
            .enqueue()

        // Monitoreo
        viewModelScope.launch {
            workManager.getWorkInfosForUniqueWorkFlow(workName).collect { workInfos ->
                if (workInfos.isNotEmpty()) {
                    val isFinished = workInfos.all { it.state.isFinished }
                    val hasFailed = workInfos.any { it.state == WorkInfo.State.FAILED }

                    if (isFinished) {
                        isLoading = false
                        if (hasFailed) {
                            errorMessage = "Error al obtener Calificaciones por Unidad."
                        }
                    } else {
                        isLoading = true
                    }
                }
            }
        }
    }

    fun cerrarSesion(context: Context, onLogoutComplete: () -> Unit) {
        viewModelScope.launch {
            val appContainer = (context.applicationContext as SicenetApplication).container

            appContainer.alumnoDao.borrarSesion()
            appContainer.cargaAcademicaDao.borrarCarga()
            appContainer.kardexDao.borrarKardex()
            appContainer.califFinalDao.borrarCalifFinal()
            appContainer.califUnidadesDao.borrarCalifUnidades()

            appContainer.cookieJar.clearSession()

            workManager.cancelAllWork()

            errorMessage = ""

            onLogoutComplete()
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
                    container = appContainer,
                    workManager = WorkManager.getInstance(application.applicationContext)
                )
            }
        }
    }
}