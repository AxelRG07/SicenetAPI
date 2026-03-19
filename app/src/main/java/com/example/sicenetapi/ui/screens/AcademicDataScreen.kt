package com.example.sicenetapi.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
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

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (alumno != null) {
                StudentCard(alumno)

                Spacer(modifier = Modifier.height(16.dp))

                // Mostrar fecha de última actualización
                val fechaFormateada = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
                    .format(Date(alumno.fechaSincronizacion))

                Text(
                    text = "Última actualización: $fechaFormateada",
                    color = Color.Gray,
                    style = MaterialTheme.typography.bodySmall
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Mantenemos el botón de cerrar sesión en la pantalla principal
                Button(
                    onClick = {
                        viewModel.cerrarSesion(context = context) {
                            onLogOut()
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Cerrar Sesión", color = MaterialTheme.colorScheme.onError)
                }

            } else {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No hay datos guardados localmente. Conéctate a internet para sincronizar.")
                }
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
            .padding(20.dp),
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