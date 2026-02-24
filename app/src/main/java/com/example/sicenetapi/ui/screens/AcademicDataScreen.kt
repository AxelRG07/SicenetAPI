package com.example.sicenetapi.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.sicenetapi.data.local.AlumnoEntity
import com.example.sicenetapi.navigation.CalifFinalDestination
import com.example.sicenetapi.navigation.CalifUnidadesDestination
import com.example.sicenetapi.navigation.CargaAcademicaDestination
import com.example.sicenetapi.navigation.KardexDestination
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AcademicDataScreen(
    viewModel: MainViewModel = viewModel(),
    onLogOut: () -> Unit,
    navController: NavController
) {
    val alumnoState = viewModel.alumnoLocal.collectAsState()
    val alumno = alumnoState.value

    val context = LocalContext.current

    // UI
    Column(
        modifier = Modifier.fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (alumno != null) {
            // Mostramos la tarjeta del estudiante
            StudentCard(alumno)

            // Mostrar fecha de última actualización
            Spacer(modifier = Modifier.height(16.dp))
            val fechaFormateada = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
                .format(Date(alumno.fechaSincronizacion))

            Text(
                text = "Última actualización: $fechaFormateada",
                color = Color.Gray,
                style = MaterialTheme.typography.bodySmall
            )

            Button(
                onClick = {
                    viewModel.cerrarSesion(context = context) {
                        onLogOut()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Cerrar Sesión")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    navController.navigate(CargaAcademicaDestination.route)
                }
            ) {
                Text("Carga Académica")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    navController.navigate(KardexDestination.route)
                }
            ) {
                Text("Kardex")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    navController.navigate(CalifFinalDestination.route)
                }
            ) {
                Text("Calificaciones Finales")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    navController.navigate(CalifUnidadesDestination.route)
                }
            ) {
                Text("Calificaciones Parciales")
            }

        } else {
            Text("No hay datos guardados localmente. Conéctate a internet para sincronizar.")
        }
    }
}

@Composable
fun StudentCard(
    alumno: AlumnoEntity
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier
            .padding(20.dp)
            .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Encabezado con Nombre y Matrícula
            Text(text = alumno.nombre, style = MaterialTheme.typography.titleLarge, textAlign = TextAlign.Center, fontWeight = FontWeight.Bold)
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
    Column(modifier = Modifier.padding(vertical = 5.dp)) {
        Text(text = label, fontWeight = FontWeight.SemiBold, textAlign = TextAlign.Center,modifier = Modifier.fillMaxWidth())
        Text(text = value, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
    }
}