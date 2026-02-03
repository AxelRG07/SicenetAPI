package com.example.sicenetapi.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.* // O material si usas Compose viejo
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.sicenetapi.ui.MainViewModel

@Composable
fun SicenetScreen(viewModel: MainViewModel = viewModel()) {
    var matricula by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(modifier = Modifier
        .padding(20.dp)
        .verticalScroll(rememberScrollState())) {

        Text("Acceso SICENET", style = MaterialTheme.typography.headlineMedium)

        OutlinedTextField(
            value = matricula,
            onValueChange = { matricula = it },
            label = { Text("Matrícula") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { viewModel.autenticar(matricula, password) },
            enabled = !viewModel.isLoading,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (viewModel.isLoading) "Cargando..." else "Ingresar y Consultar")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("Resultado del servidor:")
        Card(modifier = Modifier.fillMaxWidth().height(400.dp)) {
            Text(text = viewModel.profileResult, modifier = Modifier.padding(8.dp).verticalScroll(rememberScrollState()))
        }
    }
}