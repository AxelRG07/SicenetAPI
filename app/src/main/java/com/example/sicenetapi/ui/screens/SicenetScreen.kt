package com.example.sicenetapi.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.* // O material si usas Compose viejo
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.sicenetapi.data.Alumno
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

        // SECCIÓN DE RESULTADOS
        if (viewModel.isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        }

        // Si hay error, mostrar texto rojo
        if (viewModel.errorMessage.isNotEmpty()) {
            Text(text = viewModel.errorMessage, color = Color.Red)
        }

        // Si ya tenemos datos del alumno, mostrar la tarjeta
        viewModel.alumnoData?.let { alumno ->
            StudentCard(alumno)
        }
    }
}

@Composable
fun StudentCard(alumno: Alumno) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Encabezado con Nombre y Matrícula
            Text(text = alumno.nombre, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text(text = alumno.matricula, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            // Datos académicos
            LabeledText("Carrera:", alumno.carrera)
            LabeledText("Especialidad:", alumno.especialidad)
            LabeledText("Semestre:", alumno.semestre)
            LabeledText("Estatus:", alumno.estatus)
        }
    }
}

@Composable
fun LabeledText(label: String, value: String) {
    Row(modifier = Modifier.padding(vertical = 2.dp)) {
        Text(text = label, fontWeight = FontWeight.SemiBold, modifier = Modifier.width(100.dp))
        Text(text = value)
    }
}