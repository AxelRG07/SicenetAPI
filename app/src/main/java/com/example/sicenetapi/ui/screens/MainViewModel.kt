package com.example.sicenetapi.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sicenetapi.data.SicenetRepository
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    private val repository = SicenetRepository()

    var profileResult by mutableStateOf("Esperando login...")
        private set

    var isLoading by mutableStateOf(false)
        private set

    fun autenticar(mat: String, pass: String) {
        viewModelScope.launch {
            isLoading = true
            profileResult = "Autenticando..."

            val loginResult = repository.login(mat, pass)

            loginResult.fold(
                onSuccess = {
                    profileResult = "Login OK. Obteniendo perfil..."
                    val perfilResult = repository.getProfile()
                    perfilResult.onSuccess { datos ->
                        profileResult = datos
                    }.onFailure {
                        profileResult = "Error al bajar perfil: ${it.message}"
                    }
                },
                onFailure = {
                    profileResult = "Fallo el login. Revisa tus credenciales."
                }
            )
            isLoading = false
        }
    }
}