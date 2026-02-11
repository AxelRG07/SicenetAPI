package com.example.sicenetapi.ui.screens

import android.util.Base64
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.sicenetapi.data.Alumno
import com.example.sicenetapi.navigation.AcademicDataDestination
import com.example.sicenetapi.ui.MainViewModel

@Composable
fun SicenetScreen(
    viewModel: MainViewModel = viewModel(),
    onLoginSuccess: () -> Unit
) {
    var matricula by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(modifier = Modifier
        .fillMaxHeight()
        .padding(20.dp)
        .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

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
            //onClick = { viewModel.autenticar(matricula, password) },
            onClick = {
                viewModel.autenticar(matricula, password) {
                    onLoginSuccess()
                }
            },
            enabled = !viewModel.isLoading,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (viewModel.isLoading) "Cargando..." else "Ingresar")
        }

        if (viewModel.errorMessage.isNotEmpty()) {
            Text("Error: ${viewModel.errorMessage}", color = Color.Red)
        }
    }
}

