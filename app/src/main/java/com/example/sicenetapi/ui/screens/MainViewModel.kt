package com.example.sicenetapi.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.sicenetapi.SicenetApplication
import com.example.sicenetapi.data.Alumno
import com.example.sicenetapi.data.SicenetRepository
import kotlinx.coroutines.launch

class MainViewModel(private val repository: SicenetRepository) : ViewModel() {

    var alumnoData by mutableStateOf<Alumno?>(null)
        private set

    var errorMessage by mutableStateOf("")

    //var profileResult by mutableStateOf("Esperando login...")
      //  private set

    var isLoading by mutableStateOf(false)
        private set

    fun autenticar(mat: String, pass: String, onSuccesss: () -> Unit) {
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
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                // Buscamos nuestra aplicación
                val application = (this[APPLICATION_KEY] as SicenetApplication)
                // Sacamos el repositorio del contenedor
                val sicenetRepository = application.container.sicenetRepository

                // Creamos e inyectamos el ViewModel
                MainViewModel(repository = sicenetRepository)
            }
        }
    }
}