package com.example.sicenetapi.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sicenetapi.data.Alumno
import com.example.sicenetapi.data.SicenetRepository
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    private val repository = SicenetRepository()

    var alumnoData by mutableStateOf<Alumno?>(null)
        private set

    var errorMessage by mutableStateOf("")

    var profileResult by mutableStateOf("Esperando login...")
        private set

    var isLoading by mutableStateOf(false)
        private set

    fun autenticar(mat: String, pass: String) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = ""

            val loginResult = repository.login(mat, pass)

            loginResult.fold(
                onSuccess = {
                    val perfilResult = repository.getProfile()
                    perfilResult.onSuccess { alumno ->
                        alumnoData = alumno
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
}